package com.example.office.controller;

import com.example.office.common.Result;
import com.example.office.entity.SysUser;
import com.example.office.security.JwtUtils;
import com.example.office.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private org.springframework.data.redis.core.StringRedisTemplate stringRedisTemplate;

    @org.springframework.beans.factory.annotation.Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        // 1. 查询用户是否存在
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            return Result.error(500, "用户不存在或账号有误");
        }

        // 2. 校验密码 (BCrypt 安全验证)
        boolean isMatch = passwordEncoder.matches(password, user.getPassword());
        if (!isMatch) {
            return Result.error(500, "密码错误");
        }

        // 3. 校验账号状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            return Result.error(500, "账号已被停用，请联系管理员");
        }

        // 4. 生成 JWT Token
        String token = jwtUtils.generateToken(user.getUsername());

        // 【Redis 改造】：将 Token 存入 Redis 的白名单中，设置相同有效期
        stringRedisTemplate.opsForValue().set("login_token:" + token, username, expiration, java.util.concurrent.TimeUnit.SECONDS);

        // 5. 返回 Token 与基础用户信息
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);

        return Result.success(data);
    }

    /**
     * 退出登录接口
     */
    @PostMapping("/logout")
    public Result<String> logout(javax.servlet.http.HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (org.springframework.util.StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String token = headerAuth.substring(7);
            // 【Redis 改造】：强制下线，删除 Redis 中的 Token
            stringRedisTemplate.delete("login_token:" + token);
        }
        return Result.success("退出成功");
    }
}

package com.example.office.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.office.common.Result;
import com.example.office.entity.SysUser;
import com.example.office.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @org.springframework.cache.annotation.Cacheable(value = "userList")
    @GetMapping("/list")
    public Result<Page<SysUser>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        // 查询时排除密码字段
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(SysUser.class, info -> !info.getColumn().equals("password"));
        return Result.success(sysUserService.page(page, queryWrapper));
    }

    /**
     * 新增用户（仅管理员可操作）
     */
    @org.springframework.cache.annotation.CacheEvict(value = "userList", allEntries = true)
    @PostMapping("/add")
    public Result<String> addUser(@RequestBody SysUser user) {
        // 1. 校验必填字段
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return Result.error("登录账号不能为空");
        }
        // 2. 检查用户名是否已存在
        SysUser existing = sysUserService.getByUsername(user.getUsername().trim());
        if (existing != null) {
            return Result.error("该登录账号已被使用，请换一个");
        }
        // 3. 密码加密（默认密码 123456）
        String rawPassword = (user.getPassword() != null && !user.getPassword().trim().isEmpty())
                ? user.getPassword() : "123456";
        user.setPassword(passwordEncoder.encode(rawPassword));
        // 4. 设置默认值
        user.setStatus(1);
        user.setCreateTime(new Date());
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("staff");
        }
        sysUserService.save(user);
        return Result.success("用户创建成功，初始密码为：" + rawPassword);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "userList", allEntries = true)
    @PostMapping("/update")
    public Result<String> update(@RequestBody SysUser user) {
        // 防止篡改敏感字段
        user.setPassword(null);
        user.setUsername(null);
        sysUserService.updateById(user);
        return Result.success("更新成功");
    }

    /**
     * 更新当前用户头像
     */
    @org.springframework.cache.annotation.CacheEvict(value = "userList", allEntries = true)
    @PostMapping("/avatar")
    public Result<String> updateAvatar(@RequestBody java.util.Map<String, String> params) {
        String avatarUrl = params.get("avatar");
        if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
            return Result.error("头像地址不能为空");
        }
        // 获取当前登录用户
        String username = (String) org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setAvatar(avatarUrl);
        sysUserService.updateById(user);
        return Result.success("头像更新成功");
    }

    /**
     * 获取当前登录用户的详细信息
     */
    @GetMapping("/info")
    public Result<SysUser> getUserInfo() {
        String username = (String) org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 安全起见不返回密码
        user.setPassword(null);
        return Result.success(user);
    }
}

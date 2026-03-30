package com.example.office.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.office.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    
    /**
     * 根据用户名获取用户信息
     */
    SysUser getByUsername(String username);
}

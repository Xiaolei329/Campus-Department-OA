package com.example.office.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String realName;
    
    private String avatar;
    
    private String email;
    
    private String phone;
    
    private Integer gender;
    
    private Long deptId;
    
    private Integer status;
    
    /** 角色标识： admin=超级管理员 / manager=部门主管 / staff=普通员工 */
    private String role;
    
    private Date createTime;
    
    private Date updateTime;
}

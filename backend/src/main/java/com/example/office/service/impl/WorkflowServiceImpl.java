package com.example.office.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.office.entity.SysUser;
import com.example.office.entity.WfFormLeave;
import com.example.office.entity.WfInstance;
import com.example.office.mapper.SysUserMapper;
import com.example.office.mapper.WfFormLeaveMapper;
import com.example.office.mapper.WfInstanceMapper;
import com.example.office.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class WorkflowServiceImpl extends ServiceImpl<WfInstanceMapper, WfInstance> implements WorkflowService {

    @Autowired
    private WfFormLeaveMapper leaveMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private com.example.office.mapper.WfInstanceNodeMapper wfInstanceNodeMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean applyLeave(WfFormLeave leaveForm, String username) {
        // 1. 安全获取当前登录人
        java.util.List<SysUser> userList = sysUserMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUser>().eq("username", username)
        );
        if (userList.isEmpty()) {
            return false;
        }
        SysUser user = userList.get(0);
        
        // 2. 创建流程实例
        WfInstance instance = new WfInstance();
        instance.setTemplateId(1L);
        instance.setApplyUserId(user.getId());
        instance.setApplyTime(new Date());
        instance.setStatus(0); // 审批中
        this.save(instance);

        // 3. 保存业务表单
        leaveForm.setInstanceId(instance.getId());
        leaveForm.setUserId(user.getId());
        leaveForm.setCreateTime(new Date());
        leaveMapper.insert(leaveForm);

        // 4. 更新实例表的 business_key
        instance.setBusinessKey(leaveForm.getId().toString());
        this.updateById(instance);

        // 5. 动态查询审批人
        // 首先尝试找 role='manager' 的第一个主管用户
        java.util.List<SysUser> managers = sysUserMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUser>()
                .eq("role", "manager")
                .last("LIMIT 1")
        );
        // 若没有 manager 角色，回退选一个非当前用户且 id 最小的用户作备用
        if (managers.isEmpty()) {
            managers = sysUserMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUser>()
                    .ne("id", user.getId())
                    .orderByAsc("id")
                    .last("LIMIT 1")
            );
        }
        
        if (!managers.isEmpty()) {
            com.example.office.entity.WfInstanceNode node = new com.example.office.entity.WfInstanceNode();
            node.setInstanceId(instance.getId());
            node.setNodeId(1L);
            node.setApproverId(managers.get(0).getId()); // 动态敏运审批人 ID
            node.setAction(0);
            node.setStatus(1);
            wfInstanceNodeMapper.insert(node);
        }

        return true;
    }
}


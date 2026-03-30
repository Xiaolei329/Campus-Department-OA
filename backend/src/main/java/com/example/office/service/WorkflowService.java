package com.example.office.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.office.entity.WfFormLeave;
import com.example.office.entity.WfInstance;

public interface WorkflowService extends IService<WfInstance> {
    
    /**
     * 发起请假流程
     */
    boolean applyLeave(WfFormLeave leaveForm, String username);
}

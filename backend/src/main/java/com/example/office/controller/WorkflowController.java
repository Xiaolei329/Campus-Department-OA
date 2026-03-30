package com.example.office.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.office.common.Result;
import com.example.office.entity.WfFormLeave;
import com.example.office.entity.WfInstance;
import com.example.office.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    /**
     * 发起请假申请
     */
    @PostMapping("/leave/apply")
    public Result<String> applyLeave(@RequestBody WfFormLeave leaveForm) {
        // 从安全上下文获取当前用户
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        boolean success = workflowService.applyLeave(leaveForm, username);
        if (success) {
            return Result.success("请假申请提交成功");
        }
        return Result.error("提交失败");
    }

    /**
     * 查询我的发起实例列表
     */
    @GetMapping("/myApply")
    public Result<Page<WfInstance>> myApplyList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        java.util.List<com.example.office.entity.SysUser> users = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.example.office.entity.SysUser>() != null ? 
            sysUserMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.example.office.entity.SysUser>().eq("username", username)) : java.util.Collections.emptyList();
        
        Page<WfInstance> page = new Page<>(pageNum, pageSize);
        QueryWrapper<WfInstance> queryWrapper = new QueryWrapper<>();
        
        if (!users.isEmpty()) {
            // 严格按当前登录人 ID 过滤，确保出现厏只能看到自己的申请
            queryWrapper.eq("apply_user_id", users.get(0).getId());
        }
        
        queryWrapper.orderByDesc("apply_time");
        Page<WfInstance> resultPage = workflowService.page(page, queryWrapper);
        return Result.success(resultPage);
    }

    /**
     * 查询我的待办事项列表
     */
    @Autowired
    private com.example.office.mapper.SysUserMapper sysUserMapper;

    @Autowired
    private com.example.office.mapper.WfInstanceNodeMapper wfInstanceNodeMapper;

    @Autowired
    private com.example.office.mapper.WfFormLeaveMapper wfFormLeaveMapper;

    @GetMapping("/todo")
    public Result<java.util.Map<String, Object>> todoList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
            
        // 从安全上下文获取当前用户
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        com.example.office.entity.SysUser user = sysUserMapper.selectList(new QueryWrapper<com.example.office.entity.SysUser>().eq("username", username)).get(0);
        
        Page<com.example.office.entity.WfInstanceNode> page = new Page<>(pageNum, pageSize);
        QueryWrapper<com.example.office.entity.WfInstanceNode> queryWrapper = new QueryWrapper<>();
        // 查询当前指定给我（或我的角色）并且尚未处理的任务 (action=0)
        queryWrapper.eq("approver_id", user.getId()).eq("action", 0).orderByAsc("id");
        
        Page<com.example.office.entity.WfInstanceNode> resultPage = wfInstanceNodeMapper.selectPage(page, queryWrapper);
        
        java.util.List<java.util.Map<String, Object>> enrichedRecords = new java.util.ArrayList<>();
        for (com.example.office.entity.WfInstanceNode node : resultPage.getRecords()) {
            java.util.Map<String, Object> record = new java.util.HashMap<>();
            record.put("id", node.getId());
            record.put("instanceId", node.getInstanceId());
            record.put("action", node.getAction());
            record.put("handleTime", node.getHandleTime());
            
            // 联查请假详情补充审批事由
            QueryWrapper<WfFormLeave> leaveQa = new QueryWrapper<>();
            leaveQa.eq("instance_id", node.getInstanceId());
            java.util.List<WfFormLeave> leaves = wfFormLeaveMapper.selectList(leaveQa);
            if (!leaves.isEmpty()) {
                 record.put("reason", leaves.get(0).getReason());
            } else {
                 record.put("reason", "无事由");
            }
            enrichedRecords.add(record);
        }
        
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("records", enrichedRecords);
        map.put("total", resultPage.getTotal());
        
        return Result.success(map);
    }

    /**
     * 历史审批追踪记录
     */
    @GetMapping("/history")
    public Result<java.util.List<com.example.office.entity.WfInstanceNode>> history(@RequestParam("instanceId") Long instanceId) {
        return Result.success(wfInstanceNodeMapper.selectList(new QueryWrapper<com.example.office.entity.WfInstanceNode>().eq("instance_id", instanceId).orderByAsc("id")));
    }

    /**
     * 获取本系统的全量请假申请记录
     */
    @GetMapping("/leave/list")
    public Result<java.util.Map<String, Object>> leaveList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String reason) {
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<WfFormLeave> page = 
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
        QueryWrapper<WfFormLeave> queryWrapper = new QueryWrapper<>();
        
        if (reason != null && !reason.trim().isEmpty()) {
            queryWrapper.like("reason", reason);
        }
        
        // 按请假单号（id）倒序
        queryWrapper.orderByDesc("id");
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<WfFormLeave> resultPage = 
                wfFormLeaveMapper.selectPage(page, queryWrapper);
                
        java.util.List<java.util.Map<String, Object>> enrichedRecords = new java.util.ArrayList<>();
        for (WfFormLeave leave : resultPage.getRecords()) {
            java.util.Map<String, Object> record = new java.util.HashMap<>();
            record.put("id", leave.getId());
            record.put("instanceId", leave.getInstanceId());
            record.put("userId", leave.getUserId());
            record.put("leaveType", leave.getLeaveType());
            record.put("startTime", leave.getStartTime());
            record.put("endTime", leave.getEndTime());
            record.put("reason", leave.getReason());
            record.put("createTime", leave.getCreateTime());
            
            // 提取并发起人姓名
            com.example.office.entity.SysUser user = sysUserMapper.selectById(leave.getUserId());
            record.put("userName", user != null ? (user.getRealName() != null ? user.getRealName() : user.getUsername()) : "未知用户");
            
            // 提取引擎流转极终态
            com.example.office.entity.WfInstance instance = workflowService.getById(leave.getInstanceId());
            record.put("status", instance != null ? instance.getStatus() : 0);
            
            enrichedRecords.add(record);
        }
                
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("records", enrichedRecords);
        map.put("total", resultPage.getTotal());
        
        return Result.success(map);
    }

    /**
     * 真正完成审批链路
     */
    @PostMapping("/todo/approve")
    public Result<String> approveNode(@RequestBody java.util.Map<String, Object> params) {
        Long nodeId = Long.valueOf(params.get("nodeId").toString());
        Integer action = Integer.valueOf(params.get("action").toString()); // 1 同意，2 驳回
        
        // 1. 更新节点信息 wf_instance_node
        com.example.office.entity.WfInstanceNode node = wfInstanceNodeMapper.selectById(nodeId);
        if (node != null) {
            node.setAction(action);
            node.setStatus(2); // 标记此节点已处理完闭
            node.setHandleTime(new java.util.Date());
            wfInstanceNodeMapper.updateById(node);

            // 2. 更新实例主表 wf_instance
            com.example.office.entity.WfInstance instance = workflowService.getById(node.getInstanceId());
            if (instance != null) {
                // 按照注释规范：1=已通过, 2=已驳回
                Integer finalStatus = (action == 1) ? 1 : 2;
                instance.setStatus(finalStatus); 
                workflowService.updateById(instance);
                
                // 3. 业务表单 wf_form_leave
            }
        }
        return Result.success("流转节点审批动作顺利执行闭环");
    }

    /**
     * 导出请假记录为 Excel 文件
     * 使用 EasyExcel 直接将查询结果输出为 .xlsx 流
     */
    @GetMapping("/leave/export")
    public void exportLeaveExcel(javax.servlet.http.HttpServletResponse response) throws Exception {
        // 1. 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = java.net.URLEncoder.encode("请假记录导出", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        // 2. 查询所有请假记录
        java.util.List<WfFormLeave> leaveList = wfFormLeaveMapper.selectList(
                new QueryWrapper<WfFormLeave>().orderByDesc("id"));

        // 3. 转换为导出 DTO（包含中文列名和可读状态）
        String[] leaveTypeNames = {"", "年假", "事假", "病假", "调休"};
        String[] statusNames = {"审批中", "已通过", "已驳回", "已撤销"};
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        java.util.List<com.example.office.entity.LeaveExportDTO> exportList = new java.util.ArrayList<>();
        for (WfFormLeave leave : leaveList) {
            com.example.office.entity.LeaveExportDTO dto = new com.example.office.entity.LeaveExportDTO();
            dto.setId(leave.getId());

            // 查询发起人姓名
            com.example.office.entity.SysUser user = sysUserMapper.selectById(leave.getUserId());
            dto.setUserName(user != null ? (user.getRealName() != null ? user.getRealName() : user.getUsername()) : "未知用户");

            // 请假类型转中文
            int lt = leave.getLeaveType() != null ? leave.getLeaveType() : 0;
            dto.setLeaveTypeName(lt >= 1 && lt <= 4 ? leaveTypeNames[lt] : "其他");

            // 时间格式化
            dto.setStartTime(leave.getStartTime() != null ? sdf.format(leave.getStartTime()) : "");
            dto.setEndTime(leave.getEndTime() != null ? sdf.format(leave.getEndTime()) : "");

            dto.setReason(leave.getReason());

            // 审批状态转中文
            com.example.office.entity.WfInstance instance = workflowService.getById(leave.getInstanceId());
            int st = instance != null ? instance.getStatus() : 0;
            dto.setStatusName(st >= 0 && st <= 3 ? statusNames[st] : "未知");

            exportList.add(dto);
        }

        // 4. 使用 EasyExcel 写入到响应输出流
        com.alibaba.excel.EasyExcel.write(response.getOutputStream(), com.example.office.entity.LeaveExportDTO.class)
                .sheet("请假记录")
                .doWrite(exportList);
    }
}

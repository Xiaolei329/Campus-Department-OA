package com.example.office.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.office.common.Result;
import com.example.office.entity.SysUser;
import com.example.office.entity.WfFormLeave;
import com.example.office.entity.WfInstanceNode;
import com.example.office.entity.TaskInfo;
import com.example.office.mapper.WfFormLeaveMapper;
import com.example.office.mapper.WfInstanceNodeMapper;
import com.example.office.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private com.example.office.mapper.SysUserMapper sysUserMapper;

    @Autowired
    private WfInstanceNodeMapper wfInstanceNodeMapper;

    @Autowired
    private TaskInfoService taskInfoService;

    @Autowired
    private WfFormLeaveMapper wfFormLeaveMapper;

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<SysUser> users = sysUserMapper.selectList(new QueryWrapper<SysUser>().eq("username", username));
        if (users.isEmpty()) {
            return Result.error("用户信息异常，请重新登录");
        }
        Long userId = users.get(0).getId();

        Map<String, Object> stats = new HashMap<>();
        
        long todoCount = wfInstanceNodeMapper.selectCount(new QueryWrapper<WfInstanceNode>().eq("approver_id", userId).eq("action", 0));
        stats.put("todoCount", todoCount);

        long doneCount = wfInstanceNodeMapper.selectCount(new QueryWrapper<WfInstanceNode>().eq("approver_id", userId).ne("action", 0));
        stats.put("doneCount", doneCount);

        long taskCount = taskInfoService.count(new QueryWrapper<TaskInfo>().eq("assignee_id", userId));
        stats.put("taskCount", taskCount);

        long activeTaskCount = taskInfoService.count(new QueryWrapper<TaskInfo>().eq("assignee_id", userId).eq("status", 1));
        stats.put("activeTaskCount", activeTaskCount);

        return Result.success(stats);
    }

    /**
     * 图表统计数据接口 —— 为首页 ECharts 可视化提供真实数据
     * 返回：请假类型分布（饼图用）、任务状态分布（柱状图用）
     */
    @GetMapping("/charts")
    public Result<Map<String, Object>> getChartData() {
        Map<String, Object> result = new HashMap<>();

        // ========== 1. 请假类型分布（饼图） ==========
        // leaveType: 1=年假, 2=事假, 3=病假, 4=调休
        String[] leaveTypeNames = {"", "年假", "事假", "病假", "调休"};
        List<Map<String, Object>> leavePieData = new ArrayList<>();
        for (int type = 1; type <= 4; type++) {
            long count = wfFormLeaveMapper.selectCount(
                    new QueryWrapper<WfFormLeave>().eq("leave_type", type));
            if (count > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", leaveTypeNames[type]);
                item.put("value", count);
                leavePieData.add(item);
            }
        }
        result.put("leavePieData", leavePieData);

        // ========== 2. 任务状态分布（柱状图） ==========
        // status: 0=未开始, 1=进行中, 2=已完成
        String[] taskStatusNames = {"未开始", "进行中", "已完成"};
        List<String> taskBarCategories = new ArrayList<>();
        List<Long> taskBarValues = new ArrayList<>();
        for (int status = 0; status <= 2; status++) {
            long count = taskInfoService.count(
                    new QueryWrapper<TaskInfo>().eq("status", status));
            taskBarCategories.add(taskStatusNames[status]);
            taskBarValues.add(count);
        }
        result.put("taskBarCategories", taskBarCategories);
        result.put("taskBarValues", taskBarValues);

        return Result.success(result);
    }
}

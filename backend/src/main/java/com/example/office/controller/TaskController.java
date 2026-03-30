package com.example.office.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.office.common.Result;
import com.example.office.entity.TaskInfo;
import com.example.office.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private TaskInfoService taskInfoService;

    /**
     * 保存/指派任务
     */
    @Autowired
    private com.example.office.mapper.SysUserMapper sysUserMapper;

    @PostMapping("/save")
    public Result<String> saveTask(@RequestBody TaskInfo taskInfo) {
        if (taskInfo.getId() == null) {
            taskInfo.setCreateTime(new Date());
            
            // 补全创建人信息，解决数据库报错
            String username = (String) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            com.example.office.entity.SysUser user = sysUserMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.example.office.entity.SysUser>().eq("username", username)).get(0);
            taskInfo.setCreatorId(user.getId());
            taskInfo.setStatus(0);
            
            taskInfoService.save(taskInfo);
            return Result.success("任务下发成功");
        } else {
            taskInfo.setUpdateTime(new Date());
            taskInfoService.updateById(taskInfo);
            return Result.success("任务更新成功");
        }
    }

    /**
     * 任务办理流转
     */
    @PostMapping("/updateStatus")
    public Result<String> updateTaskStatus(@RequestBody java.util.Map<String, Integer> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = params.get("status");
        TaskInfo task = taskInfoService.getById(id);
        
        if(task != null) {
            String username = (String) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            com.example.office.entity.SysUser user = sysUserMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.example.office.entity.SysUser>().eq("username", username)).get(0);
            
            // 权限校验：仅任务被指派人或系统管理员可以修改其状态
            if (!user.getUsername().equals("admin") && !user.getId().equals(task.getAssigneeId())) {
                return Result.error("越权操作：仅任务指派本人或管理员有权流转状态");
            }
            
            task.setStatus(status);
            task.setUpdateTime(new Date());
            taskInfoService.updateById(task);
            return Result.success("任务进度流转成功");
        }
        return Result.error("目标任务异常丢失");
    }

    /**
     * 任务列表（待办/已办等）
     */
    @GetMapping("/list")
    public Result<Page<TaskInfo>> getTaskList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long assigneeId) {
            
        Page<TaskInfo> page = new Page<>(pageNum, pageSize);
        QueryWrapper<TaskInfo> query = new QueryWrapper<>();
        if (assigneeId != null) {
            query.eq("assignee_id", assigneeId);
        }
        query.orderByDesc("create_time");
        Page<TaskInfo> resultPage = taskInfoService.page(page, query);
        for (TaskInfo task : resultPage.getRecords()) {
            if (task.getAssigneeId() != null) {
                com.example.office.entity.SysUser assignee = sysUserMapper.selectById(task.getAssigneeId());
                if (assignee != null) {
                    task.setAssigneeName(assignee.getRealName() != null ? assignee.getRealName() : assignee.getUsername());
                }
            }
        }
        return Result.success(resultPage);
    }

    /**
     * 导出任务记录为 Excel 文件
     */
    @GetMapping("/export")
    public void exportTaskExcel(javax.servlet.http.HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = java.net.URLEncoder.encode("任务派发记录导出", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        java.util.List<TaskInfo> taskList = taskInfoService.list(new QueryWrapper<TaskInfo>().orderByDesc("id"));

        String[] statusNames = {"未开始", "进行中", "已完成", "已逾期"};
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        java.util.List<com.example.office.entity.TaskExportDTO> exportList = new java.util.ArrayList<>();
        for (TaskInfo task : taskList) {
            com.example.office.entity.TaskExportDTO dto = new com.example.office.entity.TaskExportDTO();
            dto.setId(task.getId());
            dto.setTitle(task.getTitle());
            dto.setContent(task.getContent());
            
            com.example.office.entity.SysUser creator = sysUserMapper.selectById(task.getCreatorId());
            dto.setCreatorName(creator != null ? (creator.getRealName() != null ? creator.getRealName() : creator.getUsername()) : "未知");
            
            com.example.office.entity.SysUser assignee = sysUserMapper.selectById(task.getAssigneeId());
            dto.setAssigneeName(assignee != null ? (assignee.getRealName() != null ? assignee.getRealName() : assignee.getUsername()) : "未知");

            int st = task.getStatus() != null ? task.getStatus() : 0;
            dto.setStatusName(st >= 0 && st <= 3 ? statusNames[st] : "未知");
            dto.setCreateTime(task.getCreateTime() != null ? sdf.format(task.getCreateTime()) : "");

            exportList.add(dto);
        }

        com.alibaba.excel.EasyExcel.write(response.getOutputStream(), com.example.office.entity.TaskExportDTO.class)
                .sheet("协同任务看板")
                .doWrite(exportList);
    }
}

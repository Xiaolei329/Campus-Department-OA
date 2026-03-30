package com.example.office.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TaskExportDTO {
    @ExcelProperty("任务单号")
    private Long id;
    
    @ExcelProperty("任务名称")
    private String title;
    
    @ExcelProperty("任务内容")
    private String content;

    @ExcelProperty("创建人")
    private String creatorName;

    @ExcelProperty("接收人")
    private String assigneeName;

    @ExcelProperty("任务状态")
    private String statusName;

    @ExcelProperty("创建时间")
    private String createTime;
}

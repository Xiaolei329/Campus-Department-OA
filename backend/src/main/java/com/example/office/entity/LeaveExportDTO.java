package com.example.office.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 请假记录导出专用 DTO
 * 使用 EasyExcel 的 @ExcelProperty 注解定义 Excel 列名
 */
@Data
public class LeaveExportDTO {

    @ExcelProperty("请假单号")
    @ColumnWidth(12)
    private Long id;

    @ExcelProperty("发起人")
    @ColumnWidth(15)
    private String userName;

    @ExcelProperty("请假类型")
    @ColumnWidth(12)
    private String leaveTypeName;

    @ExcelProperty("开始时间")
    @ColumnWidth(22)
    private String startTime;

    @ExcelProperty("结束时间")
    @ColumnWidth(22)
    private String endTime;

    @ExcelProperty("请假事由")
    @ColumnWidth(30)
    private String reason;

    @ExcelProperty("审批状态")
    @ColumnWidth(12)
    private String statusName;
}

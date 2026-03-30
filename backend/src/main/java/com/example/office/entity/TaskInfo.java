package com.example.office.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("task_info")
public class TaskInfo implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String content;
    
    private Long creatorId;
    
    private Long assigneeId;
    
    private Date deadline;
    
    // 状态 (0待办 1进行中 2已完成 3已逾期)
    private Integer status;
    
    private Integer priority;
    
    private Date createTime;
    
    private Date updateTime;
    
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String assigneeName;
}

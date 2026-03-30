package com.example.office.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("wf_form_leave")
public class WfFormLeave implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long instanceId;
    
    private Long userId;
    
    private Integer leaveType;
    
    private Date startTime;
    
    private Date endTime;
    
    private String reason;
    
    private Date createTime;
}

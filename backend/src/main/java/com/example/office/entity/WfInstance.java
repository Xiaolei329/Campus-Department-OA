package com.example.office.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("wf_instance")
public class WfInstance implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long templateId;
    
    private Long applyUserId;
    
    private Date applyTime;
    
    /** 实例状态 (0审批中 1已通过 2已驳回 3已撤销) */
    private Integer status;
    
    private String businessKey;
}

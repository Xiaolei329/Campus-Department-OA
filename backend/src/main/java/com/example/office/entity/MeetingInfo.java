package com.example.office.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sys_meeting")
public class MeetingInfo implements java.io.Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String room;
    private Date startTime;
    private Date endTime;
    private String participants;
    private Date createTime;
}

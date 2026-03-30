package com.example.office.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("notice_info")
public class NoticeInfo implements java.io.Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    // 1全员 2部门
    private Integer type;

    private Long publisherId;

    // 0草稿 1发布
    private Integer status;

    private Date createTime;

    private Date updateTime;
    
    @TableField(exist = false)
    private String publisherName;
}

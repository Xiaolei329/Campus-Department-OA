package com.example.office.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 文档信息实体类
 */
@Data
@TableName("doc_info")
public class DocInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;

    private String title;

    private String fileName;

    private String fileUrl;

    private Long fileSize;

    private String fileType;

    private Long uploaderId;

    // 0私有只读 1公开
    private Integer isShare;

    private Date createTime;

    private Date updateTime;
    
    // 上传人姓名
    @TableField(exist = false)
    private String uploaderName;
}

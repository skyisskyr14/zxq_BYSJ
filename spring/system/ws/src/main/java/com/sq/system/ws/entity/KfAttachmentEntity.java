package com.sq.system.ws.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("kf_attachment")
public class KfAttachmentEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long agentId;

    private String filename;
    private String url;
    private String mime;
    private Long size;
    private String kind;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

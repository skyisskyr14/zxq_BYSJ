package com.sq.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class ZxyUserEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long sysId;

    private String nickname;

    private String avatar;
    private Integer gender; // 0未知 1男 2女

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
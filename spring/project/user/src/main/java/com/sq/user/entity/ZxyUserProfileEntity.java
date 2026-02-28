package com.sq.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user_profile")
public class ZxyUserProfileEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String realName;
    private String idCard;

    private LocalDate birthday;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
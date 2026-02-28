package com.sq.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_address")
public class ZxyUserAddressEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String contactName;
    private String contactPhone;

    private String province;
    private String city;
    private String district;
    private String detail;

    private Integer isDefault; // 1是 0否

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
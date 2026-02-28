package com.sq.system.usercore.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_permission")
public class UserPermissionEntity {
    private Long id;
    private String permCode;
    private String permName;
    private String type;
    private String description;
    private String module;
    private Integer sort;
}

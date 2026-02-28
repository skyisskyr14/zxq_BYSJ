package com.sq.system.admincore.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("admin_permission")
public class PermissionEntity {
    private Long id;
    private String permCode;    // 如 user:add
    private String permName;    // 如 添加用户
    private String type;        // menu / button / api / data
    private String description;
    private String module;
    private Integer sort;
}
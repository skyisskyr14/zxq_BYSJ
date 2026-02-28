package com.sq.system.admincore.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("admin_role")
public class RoleEntity {
    private Long id;
    private String roleCode;   // 如 SYS_ADMIN
    private String roleName;   // 如 系统管理员
    private String description;
}

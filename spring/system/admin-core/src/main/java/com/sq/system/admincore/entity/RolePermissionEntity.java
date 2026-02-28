package com.sq.system.admincore.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("admin_role_to_permission")
public class RolePermissionEntity {
    private Long id;
    private Long roleId;
    private Long permissionId;
}

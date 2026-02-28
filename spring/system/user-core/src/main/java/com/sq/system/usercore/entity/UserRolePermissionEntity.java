package com.sq.system.usercore.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_role_to_permission")
public class UserRolePermissionEntity {
    private Long id;
    private Long roleId;
    private Long permissionId;
}

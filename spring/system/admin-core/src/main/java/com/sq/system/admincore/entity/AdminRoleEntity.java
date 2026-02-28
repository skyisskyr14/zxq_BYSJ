package com.sq.system.admincore.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("admin_to_role")
public class AdminRoleEntity {
    private Long id;
    private Long adminId;
    private Long roleId;
}

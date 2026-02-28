package com.sq.system.usercore.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_role")
public class UserRoleEntity {
    private Long id;
    private String roleCode;
    private String roleName;
    private String description;
}

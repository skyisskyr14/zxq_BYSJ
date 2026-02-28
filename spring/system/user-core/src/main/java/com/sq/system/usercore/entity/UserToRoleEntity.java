package com.sq.system.usercore.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_to_role")
public class UserToRoleEntity {
    private Long userId;
    private Long roleId;
}

package com.sq.system.usercore.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_to_project")
public class UserToProjectEntity {
    private Long userId;
    private Long projectId;
}

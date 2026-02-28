package com.sq.system.admincore.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("admin")
public class AdminUserEntity {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private Integer status;
    private String create_time;
    private String update_time;
    private String description;
    private int isSuperAdmin;
}

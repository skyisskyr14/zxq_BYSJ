package com.sq.system.usercore.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class UserEntity {
    private Long id;
    private String username;
    private String nickname;
    private Integer status;
    private LocalDateTime createTime;
    private String password;
    private String securePassword;
    private String email;
    private String phone;
    private LocalDateTime updateTime;
    private String alipay;
    private String usdt;
    private String wxpay;
    private String createIp;
    private String nowIp;
    private LocalDateTime lastLoginTime;
}

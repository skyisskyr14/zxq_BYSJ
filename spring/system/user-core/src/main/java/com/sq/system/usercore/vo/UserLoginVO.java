package com.sq.system.usercore.vo;

import com.sq.system.usercore.entity.UserEntity;
import lombok.Data;

@Data
public class UserLoginVO {
    private String token;
    private String status;
    private UserEntity user;

}


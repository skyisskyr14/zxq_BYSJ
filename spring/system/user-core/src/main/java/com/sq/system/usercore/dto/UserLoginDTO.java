package com.sq.system.usercore.dto;

import lombok.Data;

@Data
public class UserLoginDTO {
    private String username;
    private String password;
    private String captchaUuid;
    private String captchaInput;
    private String captchaType;
    private Long type;
    private Long role;
}


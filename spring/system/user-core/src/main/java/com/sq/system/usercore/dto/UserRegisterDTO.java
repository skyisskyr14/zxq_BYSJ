package com.sq.system.usercore.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String username;
    private String password;
    private  String securePassword;
//    private String captchaUuid;
//    private String captchaInput;
    private String email;
}

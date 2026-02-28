package com.sq.user.dto;

import lombok.Data;

@Data
public class ZxyUserRegisterDto {
    private String phone;
    private String password;
    private String nickname;
    private String uuid;
    private String captcha;
    private String captchaType;
}

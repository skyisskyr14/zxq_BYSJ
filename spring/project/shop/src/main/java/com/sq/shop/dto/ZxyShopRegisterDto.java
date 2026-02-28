package com.sq.shop.dto;

import lombok.Data;

@Data
public class ZxyShopRegisterDto {
    private String phone;
    private String password;
    private String nickname;
    private String uuid;
    private String captcha;
    private String captchaType;
}

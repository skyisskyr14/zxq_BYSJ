package com.sq.shop.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ZxyBaseInfoShopVo {
    private String phone;
    private String realname;
    private String avatar;
    private int gender;
    private int age;
    private LocalDateTime createTime;
}

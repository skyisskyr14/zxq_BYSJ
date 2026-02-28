package com.sq.shop.dto;

import lombok.Data;

import java.util.List;

@Data
public class ZxyShopStoreSaveDto {
    private String name;
    private String city;
    private String address;
    private String phone;
    private Integer score;
    private String intro;
    private List<String> images;
}

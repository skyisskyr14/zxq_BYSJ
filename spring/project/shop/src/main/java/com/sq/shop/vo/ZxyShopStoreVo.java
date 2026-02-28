package com.sq.shop.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ZxyShopStoreVo {
    private Long id;
    private String name;
    private String city;
    private String address;
    private String phone;
    private Integer score;
    private String intro;
    private List<String> images;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

package com.sq.shop.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ZxyShopServiceVo {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

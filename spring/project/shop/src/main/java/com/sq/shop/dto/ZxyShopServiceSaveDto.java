package com.sq.shop.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ZxyShopServiceSaveDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
}

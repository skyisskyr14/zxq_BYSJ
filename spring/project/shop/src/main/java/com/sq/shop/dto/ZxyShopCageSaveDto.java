package com.sq.shop.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ZxyShopCageSaveDto {
    private Long id;
    private String code;
    private String size;
    private BigDecimal price;
    private String status;
}

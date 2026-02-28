package com.sq.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("shop_service")
public class ZxyShopServiceEntity {
    private Long id;
    private Long shopId;
    private String name;
    private BigDecimal price;
    private String description;
    private Integer isDelete;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

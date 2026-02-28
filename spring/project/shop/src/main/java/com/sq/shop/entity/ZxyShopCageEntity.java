package com.sq.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("shop_cage")
public class ZxyShopCageEntity {
    private Long id;
    private Long shopId;
    private String code;
    private String size;
    private BigDecimal price;
    private String status;
    private Integer isDelete;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

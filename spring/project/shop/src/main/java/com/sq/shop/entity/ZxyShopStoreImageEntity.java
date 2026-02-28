package com.sq.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("shop_store_image")
public class ZxyShopStoreImageEntity {
    private Long id;
    private Long storeId;
    private String imageUrl;
    private Integer sort;
    private LocalDateTime createTime;
}

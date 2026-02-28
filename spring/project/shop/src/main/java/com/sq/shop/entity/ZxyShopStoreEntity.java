package com.sq.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("shop_store")
public class ZxyShopStoreEntity {
    private Long id;
    private Long shopId;
    private String name;
    private String city;
    private String address;
    private String phone;
    private Integer score;
    private String intro;
    private Integer isDelete;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

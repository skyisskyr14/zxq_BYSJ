package com.sq.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("shop")
public class ZxyShopEntity {
    private Long id;
    private Long sysId;
    private int gender;
    private String avatar;
    private int isDelete;
    private String realname;
    private int age;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

package com.sq.pet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pet")
public class ZxyPetEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private String name;
    private Integer type;
    private Float age;
    private Float weight;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

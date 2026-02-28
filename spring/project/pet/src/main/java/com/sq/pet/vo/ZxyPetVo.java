package com.sq.pet.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ZxyPetVo {
    private Long id;
    private String name;
    private Integer type;
    private Float age;
    private Float weight;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

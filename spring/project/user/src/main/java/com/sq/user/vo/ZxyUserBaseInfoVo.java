package com.sq.user.vo;

import com.sq.user.entity.ZxyUserEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ZxyUserBaseInfoVo {
    private String phone;
    private String nickname;
    private String avatar;
    private int gender;
    private LocalDateTime createTime;
}

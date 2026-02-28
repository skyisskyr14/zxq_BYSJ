package com.sq.system.ws.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("kf_normal_q")
public class KfNormalQEntity {
    private Long id;
    private int userId;
    private int type;
    private int status;
    private String question;
    private String result;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

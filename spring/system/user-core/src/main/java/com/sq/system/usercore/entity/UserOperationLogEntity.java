package com.sq.system.usercore.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_operation_log")
public class UserOperationLogEntity {
    private Long id;
    private Long userId;
    private String userAccount;
    private String ipAddress;
    private String requestUri;
    private String requestMethod;
    private String userAgent;
    private String actionModule;
    private String actionType;
    private String requestParam;
    private String resultStatus;
    private String resultMessage;
    private LocalDateTime createTime;
}
package com.sq.system.admincore.entity.log;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("admin_operation_log")
public class AdminOperationLogEntity {
    private Long id;
    private Long adminId;
    private String adminAccount;
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
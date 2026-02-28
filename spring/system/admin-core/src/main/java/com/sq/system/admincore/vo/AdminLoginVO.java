package com.sq.system.admincore.vo;

import com.sq.system.admincore.entity.AdminUserEntity;
import lombok.Data;

/**
 * 登录成功后返回信息（可扩展 token、用户信息等）
 */
@Data // 自动生成 Getter/Setter、toString、equals 等
public class AdminLoginVO {

    private String token;
    private String status;
    private AdminUserEntity user;
}

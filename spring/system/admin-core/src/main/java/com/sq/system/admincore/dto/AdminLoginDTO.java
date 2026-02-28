package com.sq.system.admincore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class AdminLoginDTO {
    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "管理员登录项目Id,可不填")
    private String projectCode;

    @Schema(description = "验证码类型")
    private String captchaType;

    @Schema(description = "uuid")
    private String captchaUuid;

    @Schema(description = "验证码值")
    private String captchaInput;
}

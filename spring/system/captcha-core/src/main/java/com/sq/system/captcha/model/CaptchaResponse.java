package com.sq.system.captcha.model;

import lombok.Data;

@Data
public class CaptchaResponse {
    private String uuid;       // 验证唯一标识（用于 Redis）
    private String type;       // 类型（image、slider）
    private String data;       // 主图 base64（背景图 或 文字图）
    private String extra;      // 辅助图 base64（滑块图，image 验证码为空）
}

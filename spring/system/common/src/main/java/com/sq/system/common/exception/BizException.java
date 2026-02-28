package com.sq.system.common.exception;

/**
 * 业务异常（如：登录失败、权限不足等）
 */
public class BizException extends RuntimeException {

    private final int code; // 错误码，默认 400

    public BizException(String message) {
        super(message);
        this.code = 400; // 默认业务错误码
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.code = 400;
    }

    public int getCode() {
        return code;
    }
}

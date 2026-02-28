package com.sq.system.common.result;

import lombok.Data;

/**
 * 通用返回包装类
 */
@Data
public class ResponseResult<T> {

    private int code;        // 状态码
    private String message;  // 提示信息
    private T data;          // 数据内容

    public static <T> ResponseResult<T> success(T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> ResponseResult<T> success() {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(null);
        return result;
    }

    public static <T> ResponseResult<T> fail(String message) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(400);
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    public static <T> ResponseResult<T> of(int code, String message, T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
}

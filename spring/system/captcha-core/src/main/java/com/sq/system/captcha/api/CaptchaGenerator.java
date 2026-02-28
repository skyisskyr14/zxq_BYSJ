package com.sq.system.captcha.api; // 当前接口属于 captcha.api 包

import com.sq.system.captcha.model.CaptchaResponse; // 引入验证码响应体的数据模型

/**
 * 验证码生成与校验统一接口
 * 所有验证码生成器都必须实现这个接口，确保有一致的生成与校验逻辑
 */
public interface CaptchaGenerator {

    /**
     * 获取类型标识，如：image / slider
     * 用于标识该生成器的类型，在 Dispatcher 中通过这个值路由
     */
    String getType(); // 例如返回 "image" 或 "slider"

    /**
     * 生成验证码响应体（图像等）
     * 这个方法会被调用以生成验证码（返回的 CaptchaResponse 中一般包括图片、UUID 等）
     */
    CaptchaResponse generate();

    /**
     * 校验验证码
     * @param uuid 验证码唯一标识，客户端与服务器之间用它作为验证码的 key
     * @param input 用户输入（image 类型是文本，slider 是滑动偏移量）
     * @return 返回是否校验成功
     */
    boolean verify(String uuid, String input);
}

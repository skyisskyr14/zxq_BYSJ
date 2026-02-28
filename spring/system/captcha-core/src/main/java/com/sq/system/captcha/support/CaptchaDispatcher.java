package com.sq.system.captcha.support; // 当前类属于 captcha.support 包

import com.sq.system.captcha.api.CaptchaGenerator; // 引入验证码生成器接口
import org.springframework.stereotype.Component; // 标记该类为 Spring 的组件类（自动注入）

import java.util.HashMap; // 使用 HashMap 存储策略映射
import java.util.List; // List 用于接收多个生成器
import java.util.Map; // Map 用于保存验证码类型到生成器的映射

@Component // 将该类注册为 Spring Bean，可供其他类依赖注入
public class CaptchaDispatcher {

    private final Map<String, CaptchaGenerator> strategyMap = new HashMap<>(); // 存储各类型验证码生成器的映射表，如 "image" -> ImageCaptchaGenerator

    public CaptchaDispatcher(List<CaptchaGenerator> generators) {
        // Spring 会自动把所有实现了 CaptchaGenerator 接口的 Bean 注入进来，作为列表
        for (CaptchaGenerator generator : generators) {
            strategyMap.put(generator.getType(), generator); // 将每个生成器的类型（如 image、slider）作为 key 注册到 map 中
        }
    }

    /** 
     * 获取验证码生成器，如果找不到默认返回 image 类型
     */
    public CaptchaGenerator get(String type) {
        return strategyMap.getOrDefault(type, strategyMap.get("image")); // 根据请求类型获取对应生成器，如果没有就使用默认的 image 生成器
    }
}

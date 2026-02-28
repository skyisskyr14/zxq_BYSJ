package com.sq.system.captcha.impl; // 声明当前类属于 captcha.impl 包

import com.sq.system.captcha.api.CaptchaGenerator; // 引入统一验证码接口
import com.sq.system.captcha.model.CaptchaResponse; // 响应模型
import com.sq.system.captcha.util.SliderImageUtil; // 图片生成工具类
import com.sq.system.captcha.util.SliderImageUtil.SliderResult; // 内部类，表示生成后的滑动图结果
import org.springframework.data.redis.core.StringRedisTemplate; // Redis 操作类
import org.springframework.stereotype.Component; // 注解为 Spring Bean

import java.util.UUID; // 用于生成唯一 ID
import java.util.concurrent.TimeUnit; // Redis 过期时间单位

@Component // 标记为 Spring 组件
public class SliderCaptchaGenerator implements CaptchaGenerator { // 实现 CaptchaGenerator 接口

    private final StringRedisTemplate redisTemplate; // Redis 模板，用于存取验证码校验值

    public SliderCaptchaGenerator(StringRedisTemplate redisTemplate) { // 构造方法注入 Redis 模板
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getType() {
        return "slider"; // 指定类型为 slider（用于 dispatcher 中识别）
    }

    @Override
    public CaptchaResponse generate() {
        try {
            // 调用工具类生成滑动验证码图像，返回背景图 + 滑块图 + 正确 x 坐标
            SliderResult result = SliderImageUtil.createSliderCaptcha();
            String uuid = UUID.randomUUID().toString(); // 生成唯一标识

            // 把正确的滑块 x 坐标存入 Redis，有效期 5 分钟
            redisTemplate.opsForValue().set("captcha:slider:" + uuid, String.valueOf(result.x), 5, TimeUnit.MINUTES);

            // 构建返回对象（包含背景图与滑块图 Base64）
            CaptchaResponse response = new CaptchaResponse();
            response.setUuid(uuid); // 返回 UUID 给前端
            response.setType("slider"); // 设置类型
            response.setData(result.backgroundBase64);  // 背景图 Base64 字符串
            response.setExtra(result.sliderBase64);     // 滑块图 Base64 字符串
            return response;

        } catch (Exception e) {
            throw new RuntimeException("生成滑动验证码失败", e); // 捕获错误，避免接口崩溃
        }
    }

    @Override
    public boolean verify(String uuid, String input) {
        String key = "captcha:slider:" + uuid; // 构造 Redis 键
        String realX = redisTemplate.opsForValue().get(key); // 获取正确的 x 坐标

        if (realX == null) return false; // 未找到即校验失败

        redisTemplate.delete(key); // 删除 Redis 中的数据，防止重复验证

        try {
            int offset = Integer.parseInt(input); // 用户滑动输入的 x 值
            int real = Integer.parseInt(realX);   // 正确的 x 值
            return Math.abs(offset - real) <= 5;  // 判断是否在 ±5px 容差范围内
        } catch (NumberFormatException e) {
            return false; // 输入格式非法
        }
    }
}

package com.sq.system.authapp.controller.captcha; // 定义控制器类所在的包路径


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sq.system.captcha.support.CaptchaDispatcher; // 引入验证码分发器（策略管理器）
import com.sq.system.common.result.ResponseResult;
import com.sq.system.framework.redis.AdminIpAccessService;
import com.sq.system.framework.redis.AdminTokenService;
import com.sq.system.security.config.IpAccessConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*; // 引入 Spring Web 的注解

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map; // 引入 Map 类型，用于接收前端请求体中的数据

@Tag(name = "验证码获取", description = "获取验证码")
@RestController // 声明这是一个 REST 风格的控制器，返回 JSON 数据
@RequestMapping("/auth/captcha") // 定义请求路径前缀为 /auth/captcha
public class CaptchaController {
    //commmon模块以及Redis操作
    @Resource
    private AdminTokenService adminTokenService;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private AdminIpAccessService adminIpAccessService;
    //ip防御的静态配置
    @Autowired
    private IpAccessConfig ipAccessConfig;
    //接口启动打印
    @PostConstruct
    public void init() {
        System.out.println("Cont======[1/1 system/authapp/captcha] CaptchaController 初始化完成 ======");
    }

    // 注入验证码分发器，用于根据类型选择验证码生成器
    private final CaptchaDispatcher captchaDispatcher;
    public CaptchaController(CaptchaDispatcher captchaDispatcher) {
        this.captchaDispatcher = captchaDispatcher; // 构造函数注入 CaptchaDispatcher 依赖
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Operation(summary = "获取验证码", description = "query传参，image = 图片，slider = 滑块")
    @GetMapping
    public ResponseResult<Map<String, Object>>  getCaptcha(@RequestParam(name = "type", defaultValue = "image") String type,HttpServletRequest request) throws JsonProcessingException {

        Object result = captchaDispatcher.get(type).generate();
        Map<String, Object> data = objectMapper.convertValue(result, new TypeReference<>() {});
        return ResponseResult.success(data); // 根据类型从 dispatcher 获取对应的生成器并生成验证码
    }

//    @PostMapping("/verify")
//    public boolean verify(@RequestBody Map<String, String> body) {
//        String type = body.getOrDefault("type", "image"); // 从请求体中获取验证码类型，默认为 image
//        String uuid = body.get("uuid"); // 获取验证码对应的唯一 ID
//        String input = body.get("input"); // 获取用户输入的验证码内容
//
//        return captchaDispatcher.get(type).verify(uuid, input); // 获取对应生成器并进行验证码验证
//    }
}

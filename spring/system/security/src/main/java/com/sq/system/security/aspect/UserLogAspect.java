package com.sq.system.security.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sq.system.common.annotation.UserLog;
import com.sq.system.common.utils.IpUtil;
import com.sq.system.framework.redis.UserIpAccessService;
import com.sq.system.framework.redis.UserTokenService;
import com.sq.system.usercore.entity.UserEntity;
import com.sq.system.usercore.entity.UserOperationLogEntity;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class UserLogAspect {

    @Resource
    private HttpServletRequest request;

    @Resource
    private UserTokenService userTokenService;

    @Resource
    private UserIpAccessService userIpAccessService;

    @Resource
    private ObjectMapper objectMapper;

    @Around("@annotation(com.sq.system.common.annotation.UserLog)")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        UserLog annotation = signature.getMethod().getAnnotation(UserLog.class);

        String ip = IpUtil.getIp(request);
        String location = IpUtil.getIp2region(ip);
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String ua = request.getHeader("User-Agent");

        // 优先从 Header 获取 token（App、小程序、前端自带）
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 若 Header 无 token，则从 Cookie 取（H5 浏览器自动带）
        if (token == null || token.isEmpty()) {
            token = extractToken();
        }

        UserEntity user = userTokenService.getUserByToken(token);

        UserOperationLogEntity log = new UserOperationLogEntity();
        log.setIpAddress(ip);
        log.setRequestUri(uri);
        log.setRequestMethod(method);
        log.setUserAgent(ua);
        log.setActionModule(annotation.module());
        log.setActionType(annotation.action());
        log.setCreateTime(LocalDateTime.now());

        try {
            String params = objectMapper.writeValueAsString(joinPoint.getArgs());
            log.setRequestParam(params);
        } catch (Exception ignored) {}

        if (user == null) {
            log.setUserId(null);
            log.setUserAccount(null);
            log.setResultStatus("失败");
            log.setResultMessage("未登录");
            userIpAccessService.recordUserIpAccess(log, token);
            throw new RuntimeException("未登录或 token 失效");
        }

        log.setUserId(user.getId());
        log.setUserAccount(user.getUsername());

        try {
            Object result = joinPoint.proceed();
            log.setResultStatus("成功");
            if (result != null) {
                String resultJson = objectMapper.writeValueAsString(result);
                log.setResultMessage(resultJson);
            } else {
                log.setResultMessage("操作成功");
            }
            userIpAccessService.recordUserIpAccess(log, token);
            return result;
        } catch (Exception e) {
            log.setResultStatus("失败");
            log.setResultMessage("异常：" + e.getMessage());
            userIpAccessService.recordUserIpAccess(log, token);
            throw e;
        }
    }

    private String extractToken() {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("token".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }
}

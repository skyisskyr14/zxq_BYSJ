package com.sq.system.security.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sq.system.admincore.entity.AdminUserEntity;
import com.sq.system.admincore.entity.log.AdminOperationLogEntity;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.utils.IpUtil;
import com.sq.system.framework.redis.AdminIpAccessService;
import com.sq.system.framework.redis.AdminTokenService;
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
public class AdminLogAspect {

    @Resource
    private HttpServletRequest request;

    @Resource
    private AdminTokenService adminTokenService;

    @Resource
    private AdminIpAccessService adminIpAccessService;

    @Resource
    private ObjectMapper objectMapper;

    @Around("@annotation(com.sq.system.common.annotation.AdminLog)")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AdminLog annotation = signature.getMethod().getAnnotation(AdminLog.class);

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
        AdminUserEntity admin = adminTokenService.getUserByToken(token);

        AdminOperationLogEntity log = new AdminOperationLogEntity();
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

        if (admin == null) {
            log.setAdminId(null);
            log.setAdminAccount(null);
            log.setResultStatus("失败");
            log.setResultMessage("未登录");
            adminIpAccessService.recordAdminIpAccess(log, token);
            throw new RuntimeException("未登录或 token 失效");
        }

        log.setAdminId(admin.getId());
        log.setAdminAccount(admin.getUsername());

        try {
            Object result = joinPoint.proceed();
            log.setResultStatus("成功");
            if (result != null) {
                String resultJson = objectMapper.writeValueAsString(result);
                log.setResultMessage(resultJson);
            } else {
                log.setResultMessage("操作成功");
            }
            adminIpAccessService.recordAdminIpAccess(log, token);
            return result;
        } catch (Exception e) {
            log.setResultStatus("失败");
            log.setResultMessage("异常：" + e.getMessage());
            adminIpAccessService.recordAdminIpAccess(log, token);
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

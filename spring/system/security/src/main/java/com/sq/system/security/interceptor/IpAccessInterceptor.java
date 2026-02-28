package com.sq.system.security.interceptor;

import com.sq.system.admincore.entity.log.AdminOperationLogEntity;
import com.sq.system.admincore.entity.AdminUserEntity;
import com.sq.system.common.utils.IpUtil;
import com.sq.system.security.config.IpAccessConfig;
import com.sq.system.security.resolver.IpAccessResolver;
import com.sq.system.security.resolver.TokenResolver;
import com.sq.system.usercore.entity.UserEntity;
import com.sq.system.usercore.entity.UserOperationLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class IpAccessInterceptor implements HandlerInterceptor {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private IpAccessConfig ipAccessConfig;

    @Resource
    private IpAccessResolver ipAccessResolver;
    @Autowired
    private TokenResolver tokenResolver;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String ALLOW_LIST_KEY = ipAccessConfig.getAllowListKey();
        String BLOCK_LIST_KEY = ipAccessConfig.getBlockListKey();
        boolean DEFAULT_ALLOW_UNKNOWN_IP = ipAccessConfig.isAllowUnknownIp();

        String ip = IpUtil.getIp(request); // 获取客户端 IP
        String location = IpUtil.getIp2region(ip); // IP 归属地
        String uri = request.getRequestURI(); // 当前访问 URI

        // 提取 Cookie 中的 token
        // 优先从 Header 获取 token（App、小程序、前端自带）
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 若 Header 无 token，则从 Cookie 取（H5 浏览器自动带）
        if (token == null || token.isEmpty()) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("token".equals(c.getName())) {
                        token = c.getValue();
                        break;
                    }
                }
            }
        }

        String sf = tokenResolver.hasKey(token);
        if (Objects.equals(sf, "admin")) {
            AdminOperationLogEntity log_admin = new AdminOperationLogEntity();
            AdminUserEntity admin = ipAccessResolver.findAdminBytoken(token);
            if (admin != null) {
                log_admin.setAdminId(admin.getId());
                log_admin.setAdminAccount(admin.getUsername());
            } else {
                log_admin.setAdminId(null);
                log_admin.setAdminAccount(null);
            }
            log_admin.setIpAddress(ip);
            log_admin.setRequestUri(uri);
            log_admin.setRequestMethod(request.getMethod());
            log_admin.setUserAgent(request.getHeader("User-Agent"));
            log_admin.setActionModule(null);
            log_admin.setActionType(null);
            log_admin.setRequestParam(null);
            log_admin.setCreateTime(LocalDateTime.now());
            // 黑名单直接拒绝
            if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(BLOCK_LIST_KEY, ip))) {
                log_admin.setResultStatus("拒绝");
                log_admin.setResultMessage("黑名单ip");
                ipAccessResolver.recordAdminIpAccess(log_admin, token);
                return deny(response, "您的 IP 被禁止访问：" + ip);
            }
            if (Objects.equals(request.getRequestURI(), "/auth/captcha") || Objects.equals(request.getRequestURI(), "/auth/admin/login")) {
                return white(response, ALLOW_LIST_KEY, DEFAULT_ALLOW_UNKNOWN_IP, ip, token, log_admin);
            }
            return white(response, ALLOW_LIST_KEY, DEFAULT_ALLOW_UNKNOWN_IP, ip, token, log_admin);
        } else {
            UserOperationLogEntity log_user = new UserOperationLogEntity();
            UserEntity user = ipAccessResolver.findUserBytoken(token);
            if (user != null) {
                log_user.setUserId(user.getId());
                log_user.setUserAccount(user.getUsername());
            } else {
                log_user.setUserId(null);
                log_user.setUserAccount(null);
            }
            log_user.setIpAddress(ip);
            log_user.setRequestUri(uri);
            log_user.setRequestMethod(request.getMethod());
            log_user.setUserAgent(request.getHeader("User-Agent"));
            log_user.setActionModule(null);
            log_user.setActionType(null);
            log_user.setRequestParam(null);
            log_user.setCreateTime(LocalDateTime.now());
            // 黑名单直接拒绝
            if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(BLOCK_LIST_KEY, ip))) {
                log_user.setResultStatus("拒绝");
                log_user.setResultMessage("黑名单ip");
                ipAccessResolver.recordUserIpAccess(log_user, token);
                return deny(response, "您的 IP 被禁止访问：" + ip);
            }
            if (Objects.equals(request.getRequestURI(), "/auth/captcha") || Objects.equals(request.getRequestURI(), "/auth/user/login")) {
                return whiteUser(response, ALLOW_LIST_KEY, DEFAULT_ALLOW_UNKNOWN_IP, ip, token, log_user);
            }
            return whiteUser(response, ALLOW_LIST_KEY, DEFAULT_ALLOW_UNKNOWN_IP, ip, token, log_user);
        }
    }

    private boolean white(HttpServletResponse response, String ALLOW_LIST_KEY, boolean DEFAULT_ALLOW_UNKNOWN_IP, String ip, String token, AdminOperationLogEntity log) throws IOException {
        if (DEFAULT_ALLOW_UNKNOWN_IP) {
            return true;
        }else{
            // 白名单放行
            if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(ALLOW_LIST_KEY, ip))) {
                return true;
            }else{
                // 不在白名单也不是默认放行 → 拒绝
                log.setResultStatus("拒绝");
                log.setResultMessage("不是白名单ip");
                ipAccessResolver.recordAdminIpAccess(log,token);
                return deny(response, "您的 IP 不在白名单中，禁止访问：" + ip);
            }
        }
    }

    private boolean whiteUser(HttpServletResponse response, String ALLOW_LIST_KEY, boolean DEFAULT_ALLOW_UNKNOWN_IP, String ip, String token, UserOperationLogEntity log) throws IOException {
        if (DEFAULT_ALLOW_UNKNOWN_IP) {
            return true;
        }else{
            // 白名单放行
            if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(ALLOW_LIST_KEY, ip))) {
                return true;
            }else{
                // 不在白名单也不是默认放行 → 拒绝
                log.setResultStatus("拒绝");
                log.setResultMessage("不是白名单ip");
                ipAccessResolver.recordUserIpAccess(log,token);
                return deny(response, "您的 IP 不在白名单中，禁止访问：" + ip);
            }
        }
    }

    /**
     * 从 HttpOnly Cookie 中提取名为 token 的 token
     */
    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 访问拒绝处理
     */
    private boolean deny(HttpServletResponse response, String msg) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        response.setStatus(444); // 自定义状态码
        response.getWriter().write(msg);
        return false;
    }

}

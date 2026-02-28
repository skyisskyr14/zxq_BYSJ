package com.sq.system.security.interceptor;

import com.sq.system.admincore.entity.AdminUserEntity;
import com.sq.system.security.context.TokenContextHolder;
import com.sq.system.security.context.UserTokenContextHolder;
import com.sq.system.security.resolver.TokenResolver;
import com.sq.system.usercore.entity.UserEntity;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;


@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Resource
    private TokenResolver tokenResolver;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI(); // 例如：/api/uploads/fans/xxx.jpg
        if (uri != null) {
            // 按需补充其它前缀
            if (
                    uri.startsWith("/api/uploads/kf/") || // 想更宽就放开这一行
                    uri.startsWith("/api/uploads/recharge/")|| // 想更宽就放开这一行
                            uri.startsWith("/api/uploads/pay/")||
                    uri.startsWith("/api/uploads/user/avatar/") ||
                            uri.startsWith("/api/fd/user/register")
            ) {
                return true;
            }
        }
        // 首页特殊处理
        if ("/api".equals(request.getRequestURI())) {
            return deny(response, "你不应该窥探，当它开发完，这里将会是你二开的源码的下载位置~");
        }

        // 优先从 Header 获取 token（App、小程序、前端自带）
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 若 Header 无 token，则从 Cookie 取（H5 浏览器自动带）
        if (token == null || token.isEmpty()) {
            token = extractTokenFromCookie(request);
        }

        if (token != null && !token.isEmpty()) {
            String type = tokenResolver.hasKey(token);
            if ("admin".equals(type)) {
                AdminUserEntity user = tokenResolver.resolve(token);
                if (user != null) {
                    TokenContextHolder.set(user);
                    return true;
                } else {
                    return deny(response, "token异常，别试了孩子");
                }
            } else if ("user".equals(type)) {
                UserEntity user = tokenResolver.resolve_user(token);
                if (user != null) {
                    UserTokenContextHolder.set(user);
                    return true;
                } else {
                    return deny(response, "token异常，别试了孩子");
                }
            } else {
                return deny(response, "登录已过期，请重新登录！");
            }
        } else {

            return deny(response, "无权访问");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TokenContextHolder.clear();
        UserTokenContextHolder.clear();
    }

    /**
     * 从 Cookie 提取 token
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

    private boolean deny(HttpServletResponse response, String message) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(message);
        return false;
    }
}


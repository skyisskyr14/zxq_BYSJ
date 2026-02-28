package com.sq.system.security.interceptor;

import com.sq.system.admincore.entity.AdminUserEntity;
import com.sq.system.security.context.ProjectContextHolder;
import com.sq.system.framework.redis.AdminTokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ProjectContextInterceptor implements HandlerInterceptor {

    @Resource
    private AdminTokenService adminTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

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


        AdminUserEntity user = adminTokenService.getUserByToken(token);
        if (user != null && Boolean.FALSE.equals(user.getIsSuperAdmin())) {
            // ⚠️ 此处为示例，后续你可从 header 或 token 中读取当前项目 ID
            String projectIdStr = request.getHeader("X-Project-Id");
            if (projectIdStr != null) {
                try {
                    Long projectId = Long.valueOf(projectIdStr);
                    ProjectContextHolder.set(projectId);
                } catch (Exception ignored) {}
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        ProjectContextHolder.clear();
    }
}

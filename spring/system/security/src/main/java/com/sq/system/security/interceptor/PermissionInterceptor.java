package com.sq.system.security.interceptor;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sq.system.admincore.entity.AdminUserEntity;
import com.sq.system.admincore.model.AdminPermissionModel;
import com.sq.system.common.annotation.PermissionLimit;
import com.sq.system.framework.redis.AdminTokenService;
import com.sq.system.framework.redis.UserTokenService;
import com.sq.system.security.resolver.TokenResolver;
import com.sq.system.usercore.entity.UserEntity;
import com.sq.system.usercore.entity.UserToRoleEntity;
import com.sq.system.usercore.model.UserPermissionModel;
import com.sq.system.usercore.repository.UserToRoleRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;

import java.util.Objects;
import java.util.Set;


@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private AdminTokenService adminTokenService;

    @Resource
    private AdminPermissionModel adminPermissionModel;

    @Resource
    private TokenResolver tokenResolver;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private UserPermissionModel userPermissionModel;
    @Resource
    private UserToRoleRepository userToRoleRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod method = (HandlerMethod) handler;
        PermissionLimit value = method.getMethodAnnotation(PermissionLimit.class);

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

        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("未登录或token过期！");
            return false;
        }

        if (value == null) {
            return true; // 没有权限注解，直接放行
        }

        String sf = tokenResolver.hasKey(token);
        if (Objects.equals(sf, "admin")) {
            AdminUserEntity user = adminTokenService.getUserByToken(token);
            // 超级管理员直接放行
            if (user.getIsSuperAdmin() == 1) return true;

            Set<String> permissions = adminPermissionModel.getPermissionCodes(user.getId());
            if (value != null && permissions.contains(value.value())) {
                return true;
            } else{
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("当前管理员权限不足！");
                return false;
            }
        } else if (Objects.equals(sf, "user")) {
            UserEntity user = userTokenService.getUserByToken(token);

            if (userToRoleRepository.selectOne(
                    Wrappers.lambdaQuery(UserToRoleEntity.class)
                            .eq(UserToRoleEntity::getUserId,user.getId())
            ).getUserId() == 1) return true;

            Set<String> permissions = userPermissionModel.getPermissionCodes(user.getId());
            if (value != null && permissions.contains(value.value())) {
                return true;
            } else{
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("当前用户权限不足！");
                return false;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("未查询到账号");
            return false;
        }

    }
}




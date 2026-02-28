package com.sq.system.security.resolver;

import com.sq.system.admincore.entity.AdminUserEntity;
import com.sq.system.common.utils.JwtUtil;
import com.sq.system.framework.redis.AdminTokenService;
import com.sq.system.framework.redis.UserTokenService;
import com.sq.system.usercore.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component
public class TokenResolver {

    @Resource
    private AdminTokenService adminTokenService;
    @Autowired
    private UserTokenService userTokenService;

    /**
     * 从 token 获取用户（校验 + 续签）
     */
    public AdminUserEntity resolve(String token) {
        try {
            JwtUtil.parseToken(token); // 校验 token 格式及签名
            AdminUserEntity user = adminTokenService.getUserByToken(token);
            if (user != null) {
                adminTokenService.refreshToken(token); // 自动续签
            }
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    public UserEntity resolve_user(String token) {
        try {
            JwtUtil.parseToken(token); // 校验 token 格式及签名
            UserEntity user = userTokenService.getUserByToken(token);
            if (user != null) {
                userTokenService.refreshToken(token); // 自动续签
            }
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    public String hasKey(String token) {

        String sf;

        if(adminTokenService.exists(token)){
            sf = "admin";
        }else if(userTokenService.exists(token)){
            sf = "user";
        }else{
            sf = "null";
        }

        return sf;
    }
}

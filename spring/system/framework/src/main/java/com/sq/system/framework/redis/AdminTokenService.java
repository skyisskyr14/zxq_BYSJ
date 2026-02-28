package com.sq.system.framework.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sq.system.admincore.entity.AdminUserEntity;
import com.sq.system.common.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class AdminTokenService {

    private static final String ADMIN_TOKEN_KEY = "ADMIN:TOKEN:";
    private static final long EXPIRE_SECONDS = 60 * 60 * 24 * 7; // 24小时

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 存储管理员 Token 数据
     */
    public void saveToken(String token, AdminUserEntity user) {
        try {
            String json = objectMapper.writeValueAsString(user);
            redisTemplate.opsForValue().set(ADMIN_TOKEN_KEY + token, json, EXPIRE_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("存储 Admin Token 失败", e);
        }
    }

    /**
     * 读取管理员用户信息
     */
    public AdminUserEntity getUserByToken(String token) {
        try {
            String json = redisTemplate.opsForValue().get(ADMIN_TOKEN_KEY + token);
            return json != null ? objectMapper.readValue(json, AdminUserEntity.class) : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 删除 Token（退出登录）
     */
    public void removeToken(String token) {
        redisTemplate.delete(ADMIN_TOKEN_KEY + token);
    }

    /**
     * 刷新 Token 的过期时间
     */
    public void refreshToken(String token) {
        redisTemplate.expire(ADMIN_TOKEN_KEY + token, EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 判断 Token 是否存在
     */
    public boolean exists(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(ADMIN_TOKEN_KEY + token));
    }

    /**
     * 判断 Token 是否存在，存在就删除
     */
    public void existsDelete(Long userId) {

        Set<String> keys = redisTemplate.keys(ADMIN_TOKEN_KEY + "*");
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                String token = key.substring("ADMIN:TOKEN:".length()); // 提取 token
                try {
                    Map<String, Object> claims = JwtUtil.parseToken(token);
                    Long id = Long.parseLong(claims.get("id").toString());
                    if (Objects.equals(id, userId)) {
                        // 找到旧 token，删除
                        redisTemplate.delete(key);
                        System.out.println("删除旧 token：" + key);
                    }
                } catch (Exception e) {
                    System.err.println("token解码失败：" + token);
                }
            }
        } else {
            System.out.println("未找到"+keys);
        }
    }

}

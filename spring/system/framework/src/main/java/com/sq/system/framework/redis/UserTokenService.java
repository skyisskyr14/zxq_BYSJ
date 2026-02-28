package com.sq.system.framework.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sq.system.common.utils.JwtUtil;
import com.sq.system.usercore.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class UserTokenService {

    private static final String USER_TOKEN_KEY = "USER:TOKEN:";
    private static final long EXPIRE_SECONDS = 60 * 60 * 24 * 7; // 24小时

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 存储用户 Token 数据
     */
    public void saveToken(String token, UserEntity user) {
        try {
            String json = objectMapper.writeValueAsString(user);
            redisTemplate.opsForValue().set(USER_TOKEN_KEY + token, json, EXPIRE_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("存储 User Token 失败", e);
        }
    }

    /**
     * 读取用户信息
     */
    public UserEntity getUserByToken(String token) {
        try {
            String json = redisTemplate.opsForValue().get(USER_TOKEN_KEY + token);
            return json != null ? objectMapper.readValue(json, UserEntity.class) : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 删除 Token（退出登录）
     */
    public void removeToken(String token) {
        redisTemplate.delete(USER_TOKEN_KEY + token);
    }

    /**
     * 刷新 Token 的过期时间
     */
    public void refreshToken(String token) {
        redisTemplate.expire(USER_TOKEN_KEY + token, EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 判断 Token 是否存在
     */
    public boolean exists(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(USER_TOKEN_KEY + token));
    }

    /**
     * 判断 Token 是否存在，存在就删除
     */
    public boolean existsDelete(Long userId) {

        Set<String> keys = redisTemplate.keys(USER_TOKEN_KEY + "*");
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                String token = key.substring("USER:TOKEN:".length()); // 提取 token
                try {
                    Map<String, Object> claims = JwtUtil.parseToken(token);
                    Long id = Long.parseLong(claims.get("id").toString());
                    if (Objects.equals(id, userId)) {
                        // 找到旧 token，删除
                        redisTemplate.delete(key);
                        System.out.println("删除旧 token：" + key);
                        return true;
                    }
                } catch (Exception e) {
                    System.err.println("token解码失败：" + token);
                }
            }
            return false;
        } else {
            System.out.println("未找到"+keys);
            return false;
        }
    }
}

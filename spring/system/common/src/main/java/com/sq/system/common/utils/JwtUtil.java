package com.sq.system.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类，用于生成和解析令牌
 */
public class JwtUtil {

    // 建议后期替换为从配置文件读取
    private static final String SECRET_KEY = "skyissky-platform-key-for-jwt-signature-123456";
    private static final long EXPIRE_TIME_MS = 1000 * 60 * 60 * 24; // 24小时

    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * 生成 JWT Token
     */
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)                      // 自定义载荷
                .setIssuedAt(new Date())                // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME_MS)) // 过期时间
                .signWith(key, SignatureAlgorithm.HS256) // 签名算法与密钥
                .compact();
    }

    /**
     * 解析 JWT Token
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)      // 验签用密钥
                .build()
                .parseClaimsJws(token)   // 解析并校验
                .getBody();              // 返回载荷数据
    }
}

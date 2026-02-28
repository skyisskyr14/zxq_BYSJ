package com.sq.system.framework.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sq.system.admincore.entity.log.AdminOperationLogEntity;
import com.sq.system.admincore.entity.log.AdminOperationLogEntity;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Component
public class AdminIpAccessService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String BASEKEY = "ADMIN:OPERATION:LOG:";

    @Async
    public void recordAdminIpAccess(AdminOperationLogEntity log , String token) {
        // 使用 Redis 记录 IP 行为
        try {
            String json = objectMapper.writeValueAsString(log);
            stringRedisTemplate.opsForList().rightPush(BASEKEY + token, json);
        } catch (Exception e) {
            throw new RuntimeException("存储 Admin Token LOG 失败", e);
        }
    }

//    public AdminOperationLogEntity getLogBykey(String key) {
//        try {
//            String json = stringRedisTemplate.opsForValue().get(key);
//            return json != null ? objectMapper.readValue(json, AdminOperationLogEntity.class) : null;
//        } catch (Exception e) {
//            return null;
//        }
//    }

    public Set<String> findAllByKey() {
        Set<String> keySet = new HashSet<>();

        // SCAN 配置
        ScanOptions options = ScanOptions.scanOptions()
                .match(BASEKEY + "*")
                .count(1000)  // 每次遍历最多返回多少条，可调大调小
                .build();

        // 获取原始 Redis 连接（注意需要转换编码）
        RedisConnection connection = stringRedisTemplate.getConnectionFactory().getConnection();

        try (Cursor<byte[]> cursor = connection.scan(options)) {
            while (cursor.hasNext()) {
                keySet.add(new String(cursor.next(), StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            System.err.println("❌ Redis SCAN 出错: " + e.getMessage());
        } finally {
            connection.close(); // 手动关闭连接
        }

        return keySet;
    }

    public void deleteByKey(String key) {
        stringRedisTemplate.delete( key);
    }
}

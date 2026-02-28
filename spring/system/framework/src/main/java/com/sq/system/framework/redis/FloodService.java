package com.sq.system.framework.redis;

import com.sq.system.admincore.entity.log.FloodBlockLogEntity;
import com.sq.system.admincore.repository.log.FloodBlockLogRepository;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class FloodService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private FloodBlockLogRepository floodBlockLogRepository;

    /**
     * 洪水攻击检测
     */
    public Long increment(String key, long expireSeconds) {
        Long value = stringRedisTemplate.opsForValue().increment(key);
        if (value == 1) {
            // 第一次访问时设置过期时间
            stringRedisTemplate.expire(key, Duration.ofSeconds(expireSeconds));
        }
        return value;
    }

    public boolean insertLog(String key,FloodBlockLogEntity log ,int time) {
        Boolean alreadyLogged = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", time, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(alreadyLogged)) {
            floodBlockLogRepository.insert(log); // 只记录一次
            return true;
        }else {
            return false;
        }
    }

    public void expire(String key, long seconds) {
        stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }
}

package com.sq.system.authapp.task;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.sq.system.framework.redis.UserIpAccessService;
import com.sq.system.usercore.entity.UserOperationLogEntity;
import com.sq.system.usercore.repository.UserOperationLogRepository;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component // 标注为 Spring 组件，用于注册为定时任务
public class UserIpLogSyncTask {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserIpAccessService userIpAccessService;

    @Resource
    private UserOperationLogRepository userOperationLogRepository;

    @Resource
    private ObjectMapper objectMapper;

    @Async
    @Scheduled(cron = "10 * * * * ?") // 每分钟执行一次
    public void syncToDatabase() {
        Set<String> keys = userIpAccessService.findAllByKey();
        if (keys == null || keys.isEmpty()) return;

        int processed = 0;
        for (String key : keys) {
            // 从 Redis List 中读取所有日志（可做分页优化）
            Long size = stringRedisTemplate.opsForList().size(key);
            if (size == null || size == 0) continue;

            for (int i = 0; i < size; i++) {
                String json = stringRedisTemplate.opsForList().leftPop(key);
                if (json != null) {
                    try {
                        UserOperationLogEntity log = objectMapper.readValue(json, UserOperationLogEntity.class);
                        userOperationLogRepository.insert(log);
                        processed++;
                    } catch (Exception e) {
                        System.err.println("❌ 日志入库失败：" + e);
                        e.printStackTrace(); // ⬅ 打印完整堆栈
                    }
                }
            }

            // 若空列表，考虑删除 key
            if (stringRedisTemplate.opsForList().size(key) == 0) {
                stringRedisTemplate.delete(key);
            }
        }

        if (processed > 0) {
            System.out.println("✅ 用户操作日志入库完成，处理条数：" + processed + "，时间：" + LocalDateTime.now());
        }
    }
}



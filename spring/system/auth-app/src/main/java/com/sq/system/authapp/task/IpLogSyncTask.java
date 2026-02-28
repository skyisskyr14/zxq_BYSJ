package com.sq.system.authapp.task;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sq.system.admincore.entity.log.AdminOperationLogEntity;
import com.sq.system.admincore.repository.log.AdminOperationLogRepository;
import com.sq.system.framework.redis.AdminIpAccessService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component // 标注为 Spring 组件，用于注册为定时任务
public class IpLogSyncTask {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private AdminIpAccessService adminIpAccessService;

    @Resource
    private AdminOperationLogRepository adminOperationLogRepository;

    @Resource
    private ObjectMapper objectMapper;

    @Async
    @Scheduled(cron = "30 * * * * ?") // 每分钟执行一次
    public void syncToDatabase() {
        Set<String> keys = adminIpAccessService.findAllByKey();
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
                        AdminOperationLogEntity log = objectMapper.readValue(json, AdminOperationLogEntity.class);
                        adminOperationLogRepository.insert(log);
                        processed++;
                    } catch (Exception e) {
                        System.err.println("❌ 日志入库失败：" + e.getMessage());
                    }
                }
            }

            // 若空列表，考虑删除 key
            if (stringRedisTemplate.opsForList().size(key) == 0) {
                stringRedisTemplate.delete(key);
            }
        }

        if (processed > 0) {
            System.out.println("✅ 管理员操作日志入库完成，处理条数：" + processed + "，时间：" + LocalDateTime.now());
        }
    }
}



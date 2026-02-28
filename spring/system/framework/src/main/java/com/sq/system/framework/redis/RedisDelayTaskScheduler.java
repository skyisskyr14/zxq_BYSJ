package com.sq.system.framework.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * 通用 Redis 延迟调度器工具类
 * 基于 Redis ZSet 实现定时任务调度，支持多业务注册监听处理逻辑。
 */
@Slf4j
public class RedisDelayTaskScheduler implements DisposableBean {

    private final RedisTemplate<String, String> redisTemplate;
    private final ScheduledExecutorService scheduler;
    private final Map<String, Consumer<String>> handlerMap = new ConcurrentHashMap<>();

    private volatile boolean running = true;

    public RedisDelayTaskScheduler(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        // 每秒执行一次拉取任务
        scheduler.scheduleWithFixedDelay(this::pollTasks, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * 注册任务类型和处理逻辑
     * @param type    任务类型唯一标识
     * @param handler 任务处理逻辑（参数为任务内容）
     */
    public void registerHandler(String type, Consumer<String> handler) {
        handlerMap.put(type, handler);
    }

    /**
     * 添加一个延迟任务
     * @param type      任务类型
     * @param taskKey   任务唯一键（如 subId、userId+subId 等）
     * @param taskData  任务内容（JSON 字符串或其他结构）
     * @param delaySec  延迟秒数（多久后执行）
     */
    public void schedule(String type, String taskKey, String taskData, long delaySec) {
        String redisKey = buildRedisKey(type);
        long score = Instant.now().getEpochSecond() + delaySec;
        // 值格式：taskKey::taskData
        String value = taskKey + "::" + taskData;
        redisTemplate.opsForZSet().add(redisKey, value, score);
    }

    /**
     * 取消某个任务（按 taskKey 删除）
     */
    public void cancel(String type, String taskKey) {
        String redisKey = buildRedisKey(type);
        Set<String> values = redisTemplate.opsForZSet().range(redisKey, 0, -1);
        if (values != null) {
            for (String value : values) {
                if (value.startsWith(taskKey + "::")) {
                    redisTemplate.opsForZSet().remove(redisKey, value);
                    break; // 通常只有一条，找到就退出
                }
            }
        }
    }


    /**
     * 定时拉取任务
     */
    private void pollTasks() {
        if (!running) return;
        long now = Instant.now().getEpochSecond();

        for (Map.Entry<String, Consumer<String>> entry : handlerMap.entrySet()) {
            String type = entry.getKey();
            Consumer<String> handler = entry.getValue();
            String redisKey = buildRedisKey(type);

            try {
                // 获取到期任务（最多拉 50 个）
                var tasks = redisTemplate.opsForZSet().rangeByScore(redisKey, 0, now, 0, 50);
                if (tasks == null || tasks.isEmpty()) continue;

                for (String task : tasks) {
                    // 原子移除，避免并发执行
                    Long removed = redisTemplate.opsForZSet().remove(redisKey, task);
                    if (removed != null && removed > 0) {
                        // 解析 taskData
                        int sep = task.indexOf("::");
                        if (sep > 0) {
                            String taskData = task.substring(sep + 2);
                            handler.accept(taskData);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("调度任务失败：" + type, e);
            }
        }
    }

    private String buildRedisKey(String type) {
        return "delay_task:" + type;
    }

    @Override
    public void destroy() {
        this.running = false;
        scheduler.shutdownNow();
    }
}

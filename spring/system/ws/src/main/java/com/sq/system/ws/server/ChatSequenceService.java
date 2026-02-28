
package com.sq.system.ws.server;

import com.sq.system.ws.repository.KfMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ChatSequenceService {

    private final StringRedisTemplate redis;
    private final KfMessageRepository msgRepo;

    // 原子脚本：如 key 不存在 -> 先写入 base（DB 最大值），再 INCR；若已存在 -> 直接 INCR
    private static final DefaultRedisScript<Long> INIT_AND_INCR = new DefaultRedisScript<>(
            "local k = KEYS[1]\n" +
                    "local base = tonumber(ARGV[1])\n" +
                    "if redis.call('EXISTS', k) == 0 then\n" +
                    "  redis.call('SET', k, base)\n" +
                    "end\n" +
                    "return redis.call('INCR', k)\n",
            Long.class
    );

    private static String keyOf(Long convId) {
        return "kf:msgseq:conv:" + convId; // 不设 TTL，保证重启不回退
    }

    /** 取得该会话下一个 serverMsgId（0 开始、全局单调、线程安全、集群安全） */
    public long nextForConv(Long convId) {
        Long base = msgRepo.maxServerMsgId(convId); // -1 表示新会话，从 0 开始
        if (base == null) base = -1L;

        Long next = redis.execute(INIT_AND_INCR,
                Collections.singletonList(keyOf(convId)),
                base.toString());

        if (next == null) throw new IllegalStateException("Redis seq failed");
        return next;
    }
}

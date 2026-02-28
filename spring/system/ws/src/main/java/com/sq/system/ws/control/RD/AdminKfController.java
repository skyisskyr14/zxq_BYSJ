package com.sq.system.ws.control.RD;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sq.system.common.annotation.AdminLog;
import com.sq.system.common.result.ResponseResult;
import com.sq.system.usercore.entity.UserEntity;
import com.sq.system.usercore.repository.UserRepository;
import com.sq.system.ws.entity.KfAgent;
import com.sq.system.ws.entity.KfConversation;
import com.sq.system.ws.entity.KfDept;
import com.sq.system.ws.entity.KfNormalQEntity;
import com.sq.system.ws.repository.KfAgentRepository;
import com.sq.system.ws.repository.KfConversationRepository;
import com.sq.system.ws.repository.KfDeptRepository;
import com.sq.system.ws.repository.KfNormalQRepository;
import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/kf/RD")
public class AdminKfController {

    @Resource private KfDeptRepository kfDeptRepository;          // JPA
    @Resource private KfAgentRepository kfAgentRepository;        // JPA
    @Resource private KfConversationRepository kfConversationRepository; // JPA

    @Resource private KfNormalQRepository kfNormalQRepository;    // 这个是 MP（保持你现在的用法）
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/info")
    public ResponseResult<?> info(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();

        // 1) 部门
        List<KfDept> depts = kfDeptRepository.findAll();
        result.put("type", depts);

        // 2) 当前管理员名下客服
        List<KfAgent> agents = kfAgentRepository.findByUserId(id);
        result.put("info", agents);

        // 3) FAQ：通用(userId=0) + 专属(userId=agentId)
        Set<Long> agentIds = agents.stream()
                .map(KfAgent::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<KfNormalQEntity> faqAll = kfNormalQRepository.selectList(
                new LambdaQueryWrapper<KfNormalQEntity>()
                        .eq(KfNormalQEntity::getUserId, 0L) // 通用
                        .or(!agentIds.isEmpty(), w -> w.in(KfNormalQEntity::getUserId, agentIds))
        );
        result.put("normal", faqAll);

        // 4) 会话列表（持久化）：按多个 agentId 一次拉出
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (!agentIds.isEmpty()) {
            List<KfConversation> convs = kfConversationRepository
                    .findByAgentIdInOrderByLastMsgAtDesc(new ArrayList<>(agentIds));

            ZoneId zone = ZoneId.systemDefault();
            for (KfConversation c : convs) {
                long lastMs = c.getLastMsgAt() == null
                        ? 0L
                        : c.getLastMsgAt().atZone(zone).toInstant().toEpochMilli();

                UserEntity user = userRepository.selectById(c.getUserId());

                Map<String, Object> m = new HashMap<>();
                m.put("id", c.getDeptId() + ":" + c.getAgentId() + ":" + c.getUserId());
                m.put("dept", c.getDeptId());
                m.put("agentId", c.getAgentId());
                m.put("userId", c.getUserId());
                m.put("name", user.getNickname());
                m.put("phone", user.getPhone());
                m.put("lastMsgAt", lastMs);                     // ← 转成毫秒给前端
                m.put("lastPreview", Optional.ofNullable(c.getLastPreview()).orElse(""));
                m.put("unread", Optional.ofNullable(c.getUnreadForAgent()).orElse(0)); // ← 字段名适配
                conversations.add(m);
            }
        }
        result.put("conversations", conversations);

        return ResponseResult.success(result);
    }

    @Transactional
    @PostMapping("/conv/markRead")
    public ResponseResult<?> markRead(@RequestParam Long deptId,
                                      @RequestParam Long agentId,
                                      @RequestParam Long userId) {
        return kfConversationRepository.findByDeptIdAndAgentIdAndUserId(deptId, agentId, userId)
                .map(c -> {
                    kfConversationRepository.clearAgentUnread(c.getId());
                    return ResponseResult.success("ok");
                })
                .orElseGet(() -> ResponseResult.success("ok")); // 没有会话也算成功
    }

    @GetMapping("/create")
    public ResponseResult<?> create(@RequestParam Long type, @RequestParam String name,@RequestParam Long id) {

        boolean exists = kfAgentRepository.existsByDeptIdAndNickname(type,name);
        if(exists) {
            return ResponseResult.fail("当前部门用户名已存在");
        }

        var entity = new KfAgent();

        entity.setUserId(id);
        entity.setDeptId(type);
        entity.setNickname(name);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setStatus(1);

        kfAgentRepository.save(entity);

        KfNormalQEntity q = new KfNormalQEntity();
        q.setUserId(Math.toIntExact(entity.getId()));
        q.setType(0);
        q.setStatus(0);
        q.setQuestion("自我介绍");
        q.setCreateTime(LocalDateTime.now());
        q.setUpdateTime(LocalDateTime.now());
        kfNormalQRepository.insert(q);

        Map<String, Object> result = new HashMap<>();
        result.put("id", entity.getId());

        return ResponseResult.success(result);

    }

    @AdminLog(module = "FANS_客服", action = "修改客服信息")
    @GetMapping("/update")
    public ResponseResult<?> update(@RequestParam Long id, @RequestParam Long deptId,@RequestParam String name) {

        KfAgent agent = kfAgentRepository.findById(id).orElse(null);
        if(agent == null) {
            return ResponseResult.fail("该客服不存在");
        }

        if(agent.getDeptId().equals(deptId) && agent.getNickname().equals(name)) {
            ResponseResult.success("ok");
        }

        agent.setNickname(name);
        agent.setDeptId(deptId);
        kfAgentRepository.save(agent);

        return ResponseResult.success("ok");

    }
}


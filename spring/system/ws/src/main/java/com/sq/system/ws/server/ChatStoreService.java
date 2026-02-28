// src/main/java/com/skyissky/ws/service/ChatStoreService.java
package com.sq.system.ws.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.sq.system.ws.entity.KfAgent;
import com.sq.system.ws.entity.KfConversation;
import com.sq.system.ws.entity.KfMessage;
import com.sq.system.ws.entity.KfNormalQEntity;
import com.sq.system.ws.enums.MsgSide;
import com.sq.system.ws.enums.MsgType;
import com.sq.system.ws.repository.KfAgentRepository;
import com.sq.system.ws.repository.KfConversationRepository;
import com.sq.system.ws.repository.KfMessageRepository;
import com.sq.system.ws.repository.KfNormalQRepository;
import com.sq.system.ws.ws.ChatSessions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.websocket.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatStoreService {
    private final KfAgentRepository agentRepo;
    private final KfConversationRepository convRepo;
    private final KfMessageRepository msgRepo;
    private final KfNormalQRepository normalRepo;     // 自我介绍来源（kf_noemal_q）
    private final ChatSequenceService seq;            // 分配 serverMsgId
    private final ObjectMapper om = new ObjectMapper();

    private static final ObjectMapper M = new ObjectMapper();

    public Long ensureConvId(Long userId, Long agentId) {
        KfAgent agent = agentRepo.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("agent not found: " + agentId));
        Long deptId = agent.getDeptId();
        KfConversation conv = getOrCreateConv(deptId, agentId, userId);
        List<KfMessage> list = msgRepo.findKfMessageByConvId(conv.getId());
        System.out.println("当前消息数量" + list.size());
        if(list.size() == 0 && normalRepo.selectType0ByUserId(agentId).getStatus() == 1) {
            // 首次会话 → 自动发送一次“自我介绍”
            trySendAutoIntroOnce(conv);
        }
        return conv.getId();
    }

    /* ---------- 用户 -> 客服：文本 ---------- */
    @Transactional
    public void storeUserText(Long userId, Long toAgentId, String text,
                              String clientMsgId, Long serverMsgId, long tsMillis) {
        KfAgent agent = agentRepo.findById(toAgentId)
                .orElseThrow(() -> new IllegalArgumentException("agent not found: " + toAgentId));
        Long deptId = agent.getDeptId();
        KfConversation conv = getOrCreateConv(deptId, toAgentId, userId);

        // 幂等（客户端重试）
        if (clientMsgId != null && msgRepo.findByClientMsgId(clientMsgId).isPresent()) return;

        KfMessage m = new KfMessage();
        m.setConvId(conv.getId());
        m.setServerMsgId(serverMsgId);
        m.setClientMsgId(clientMsgId);
        m.setFromSide(MsgSide.USER);
        m.setMsgType(MsgType.TEXT);
        m.setText(text);
        m.setRevoked(false);
        m.setCreatedAt(toLocal(tsMillis));
        msgRepo.save(m);

        // 更新会话摘要 & 未读（给坐席）
        conv.setLastMsgAt(m.getCreatedAt());
        conv.setLastPreview(cutPreview(text));
        conv.setUnreadForAgent((conv.getUnreadForAgent() == null ? 0 : conv.getUnreadForAgent()) + 1);
        convRepo.save(conv);
    }

    /* ---------- 用户 -> 客服：附件/图片 ---------- */
    @Transactional
    public void storeUserAttachment(Long userId, Long toAgentId, Map<String,Object> payload,
                                    String clientMsgId, Long serverMsgId, long tsMillis) {
        KfAgent agent = agentRepo.findById(toAgentId)
                .orElseThrow(() -> new IllegalArgumentException("agent not found: " + toAgentId));
        Long deptId = agent.getDeptId();
        KfConversation conv = getOrCreateConv(deptId, toAgentId, userId);

        if (clientMsgId != null && msgRepo.findByClientMsgId(clientMsgId).isPresent()) return;

        KfMessage m = new KfMessage();
        m.setConvId(conv.getId());
        m.setServerMsgId(serverMsgId);
        m.setClientMsgId(clientMsgId);
        m.setFromSide(MsgSide.USER);
        m.setMsgType(detectType(payload));     // IMAGE/FILE
        m.setPayloadJson(toJson(payload));
        m.setRevoked(false);
        m.setCreatedAt(toLocal(tsMillis));
        msgRepo.save(m);

        String pv = previewOf(payload);
        conv.setLastMsgAt(m.getCreatedAt());
        conv.setLastPreview(pv);
        conv.setUnreadForAgent((conv.getUnreadForAgent() == null ? 0 : conv.getUnreadForAgent()) + 1);
        convRepo.save(conv);
    }

    /* ---------- 用户 -> 客服：FAQ1（面板点击）---------- */
    @Transactional
    public void storeUserFAQ(Long userId, Long toAgentId, Long faqId, String question,
                             String clientMsgId, Long serverMsgId, long tsMillis) {
        KfAgent agent = agentRepo.findById(toAgentId)
                .orElseThrow(() -> new IllegalArgumentException("agent not found: " + toAgentId));
        Long deptId = agent.getDeptId();
        KfConversation conv = getOrCreateConv(deptId, toAgentId, userId);

        // 幂等
        if (clientMsgId != null && msgRepo.findByClientMsgId(clientMsgId).isPresent()) return;

        KfMessage m = new KfMessage();
        m.setConvId(conv.getId());
        m.setServerMsgId(serverMsgId);
        m.setClientMsgId(clientMsgId);
        m.setFromSide(MsgSide.USER);
        m.setMsgType(MsgType.TEXT);          // 仍然作为 TEXT
        m.setText(question);                  // 文本保存问题本身
        m.setRevoked(false);
        m.setCreatedAt(toLocal(tsMillis));
        msgRepo.save(m);

        // 更新会话摘要 & 未读（给坐席）
        conv.setLastMsgAt(m.getCreatedAt());
        conv.setLastPreview(cutPreview(question));
        conv.setUnreadForAgent((conv.getUnreadForAgent() == null ? 0 : conv.getUnreadForAgent()) + 1);
        convRepo.save(conv);
    }

    /* ---------- 客服 -> 用户：文本 ---------- */
    @Transactional
    public void storeAgentText(Long fromAgentId, Long toUserId, String text,
                               String clientMsgId, Long serverMsgId, long tsMillis) {
        KfAgent agent = agentRepo.findById(fromAgentId)
                .orElseThrow(() -> new IllegalArgumentException("agent not found: " + fromAgentId));
        Long deptId = agent.getDeptId();
        KfConversation conv = getOrCreateConv(deptId, fromAgentId, toUserId);

        if (clientMsgId != null && msgRepo.findByClientMsgId(clientMsgId).isPresent()) return;

        KfMessage m = new KfMessage();
        m.setConvId(conv.getId());
        m.setServerMsgId(serverMsgId);
        m.setClientMsgId(clientMsgId);
        m.setFromSide(MsgSide.AGENT);
        m.setMsgType(MsgType.TEXT);
        m.setText(text);
        m.setRevoked(false);
        m.setCreatedAt(toLocal(tsMillis));
        msgRepo.save(m);

        // 给“用户侧”的未读（必要时）
        conv.setLastMsgAt(m.getCreatedAt());
        conv.setLastPreview(cutPreview(text));
        conv.setUnreadForUser((conv.getUnreadForUser() == null ? 0 : conv.getUnreadForUser()) + 1);
        convRepo.save(conv);
    }

    /* ---------- 客服 -> 用户：附件 ---------- */
    @Transactional
    public void storeAgentAttachment(Long fromAgentId, Long toUserId, Map<String,Object> payload,
                                     String clientMsgId, Long serverMsgId, long tsMillis) {
        KfAgent agent = agentRepo.findById(fromAgentId)
                .orElseThrow(() -> new IllegalArgumentException("agent not found: " + fromAgentId));
        Long deptId = agent.getDeptId();
        KfConversation conv = getOrCreateConv(deptId, fromAgentId, toUserId);

        if (clientMsgId != null && msgRepo.findByClientMsgId(clientMsgId).isPresent()) return;

        KfMessage m = new KfMessage();
        m.setConvId(conv.getId());
        m.setServerMsgId(serverMsgId);
        m.setClientMsgId(clientMsgId);
        m.setFromSide(MsgSide.AGENT);
        m.setMsgType(detectType(payload));
        m.setPayloadJson(toJson(payload));
        m.setRevoked(false);
        m.setCreatedAt(toLocal(tsMillis));
        msgRepo.save(m);

        String pv = previewOf(payload);
        conv.setLastMsgAt(m.getCreatedAt());
        conv.setLastPreview(pv);
        conv.setUnreadForUser((conv.getUnreadForUser() == null ? 0 : conv.getUnreadForUser()) + 1);
        convRepo.save(conv);
    }

    /* ---------- 内部工具 ---------- */
    private KfConversation getOrCreateConv(Long deptId, Long agentId, Long userId) {
        return convRepo.findByDeptIdAndAgentIdAndUserId(deptId, agentId, userId)
                .orElseGet(() -> {
                    KfConversation c = new KfConversation();
                    c.setDeptId(deptId);
                    c.setAgentId(agentId);
                    c.setUserId(userId);
                    c.setUnreadForAgent(0);
                    c.setUnreadForUser(0);
                    c.setIsOpen(true);
                    c.setCreatedAt(LocalDateTime.now());
                    c.setUpdatedAt(LocalDateTime.now());
                    return convRepo.save(c);
                });
    }

    private LocalDateTime toLocal(long ts) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(ts), ZoneId.systemDefault());
    }

    private String cutPreview(String s) {
        if (s == null) return "";
        return s.length() <= 120 ? s : s.substring(0, 120);
    }

    private MsgType detectType(Map<String,Object> payload) {
        String kind = String.valueOf(payload.getOrDefault("kind", "FILE"));
        if ("IMAGE".equalsIgnoreCase(kind)) return MsgType.IMAGE;
        if ("FILE".equalsIgnoreCase(kind))  return MsgType.FILE;
        return MsgType.FILE;
    }

    private String previewOf(Map<String,Object> payload) {
        String kind = String.valueOf(payload.getOrDefault("kind", "FILE"));
        if ("IMAGE".equalsIgnoreCase(kind)) return "[图片]";
        if ("FILE".equalsIgnoreCase(kind))  return "[文件] " + String.valueOf(payload.getOrDefault("filename",""));
        return "[附件]";
    }

    private String toJson(Object o) {
        try { return om.writeValueAsString(o); }
        catch (JsonProcessingException e) { return "{}"; }
    }

    /** 首次会话自动发送“自我介绍”（type=0） */
    private void trySendAutoIntroOnce(KfConversation conv) {
        String intro = resolveIntroText(conv.getAgentId());
        if (intro == null || intro.isBlank()) return;
        System.out.println("dsadsa");
        // 幂等：保证只发一次
        String clientMsgId = "AUTO_INTRO_CONV_" + conv.getId();
        if (msgRepo.findByClientMsgId(clientMsgId).isPresent()) return;

        long serverMsgId = seq.nextForConv(conv.getId());
        long ts = System.currentTimeMillis();

        // 入库为“客服→用户：文本”
        storeAgentText(conv.getAgentId(), conv.getUserId(), intro, clientMsgId, serverMsgId, ts);

        // 通过 WS 推送给用户
        Session user = ChatSessions.USER.get(conv.getUserId());
        if (user != null && user.isOpen()) {
            try {
                user.getAsyncRemote().sendText(om.writeValueAsString(Map.of(
                        "type","MESSAGE",
                        "from","AGENT",
                        "userId", conv.getUserId(),
                        "agentId", conv.getAgentId(),
                        "serverMsgId", serverMsgId,
                        "text", intro,
                        "ts", ts
                )));
            } catch (Exception ignore) {}
        }
    }

    /** 从 kf_noemal_q 读取 type=0 的“自我介绍”：优先取专属(user_id=agentId)，否则取全局(user_id=0) */
    private String resolveIntroText(Long agentId) {
        try {
            QueryWrapper<KfNormalQEntity> q1 = new QueryWrapper<>();
            q1.eq("type", 0).eq("user_id", agentId).last("limit 1");
            KfNormalQEntity e1 = normalRepo.selectOne(q1);
            if (e1 != null && e1.getResult() != null && !e1.getResult().isBlank()) return e1.getResult();

            QueryWrapper<KfNormalQEntity> q2 = new QueryWrapper<>();
            q2.eq("type", 0).eq("user_id", 0).last("limit 1");
            KfNormalQEntity e2 = normalRepo.selectOne(q2);
            if (e2 != null && e2.getResult() != null && !e2.getResult().isBlank()) return e2.getResult();
        } catch (Exception ignore) {}
        return null;
    }
}

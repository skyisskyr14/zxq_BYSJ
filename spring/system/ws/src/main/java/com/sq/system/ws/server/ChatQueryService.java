// com.skyissky.ws.server.ChatQueryService
package com.sq.system.ws.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.sq.system.ws.entity.KfConversation;
import com.sq.system.ws.entity.KfMessage;
import com.sq.system.ws.enums.MsgType;
import com.sq.system.ws.repository.KfConversationRepository;
import com.sq.system.ws.repository.KfMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatQueryService {

    private final KfConversationRepository convRepo;
    private final KfMessageRepository msgRepo;
    private static final ObjectMapper M = new ObjectMapper();

    @Transactional(readOnly = true)
    public HistoryResult getHistoryByTriple(Long deptId, Long agentId, Long userId,
                                            Long before, int size) {
        var conv = convRepo.findByDeptIdAndAgentIdAndUserId(deptId, agentId, userId)
                .orElse(null);
        if (conv == null) {
            return HistoryResult.emptyFor(deptId, agentId, userId);
        }
        return getHistoryByConvId(conv, before, size);
    }

    @Transactional(readOnly = true)
    public HistoryResult getHistoryByConvId(KfConversation conv, Long before, int size) {
        var pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "serverMsgId"));
        var list = msgRepo.fetchHistory(conv.getId(), before,  pageable);

        // 倒序 -> 正序返回，便于前端 push 到顶部
        Collections.reverse(list);

        var items = list.stream().map(this::toView).collect(Collectors.toList());
        Long nextBefore = items.isEmpty() ? before : items.get(0).getServerMsgId(); // 正序的第一条是最老的
        boolean hasMore = list.size() == size; // 粗略判断

        return HistoryResult.builder()
                .conv(new ConvView(conv.getId(), conv.getDeptId(), conv.getAgentId(), conv.getUserId()))
                .messages(items)
                .hasMore(hasMore)
                .nextBefore(nextBefore)
                .build();
    }

    private MessageView toView(KfMessage m) {
        Map<String, Object> payload = null;
        if (m.getPayloadJson() != null) {
            try {
                payload = M.readValue(m.getPayloadJson(), new TypeReference<Map<String, Object>>() {});
            } catch (Exception ignore) {}
        } else if (m.getMsgType() == MsgType.TEXT) {
            // 统一前端结构：都走 payload；文本放 text
            payload = new HashMap<>();
            payload.put("text", Optional.ofNullable(m.getText()).orElse(""));
        }
        long ts = m.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        return MessageView.builder()
                .serverMsgId(m.getServerMsgId())
                .clientMsgId(m.getClientMsgId())
                .from(m.getFromSide().name())            // USER / AGENT / SYSTEM
                .msgType(m.getMsgType().name())          // TEXT / IMAGE / FILE / SYSTEM
                .payload(payload)                        // 文本：{text: "..."}；图片/文件：入库的payload
                .quoteMsgId(m.getQuoteMsgId())
                .revoked(Boolean.TRUE.equals(m.getRevoked()))
                .ts(ts)
                .build();
    }

    /* ==== DTO ==== */

    @lombok.Builder @lombok.Data
    public static class HistoryResult {
        private ConvView conv;
        private List<MessageView> messages;
        private boolean hasMore;
        private Long nextBefore;

        public static HistoryResult emptyFor(Long deptId, Long agentId, Long userId) {
            return HistoryResult.builder()
                    .conv(new ConvView(null, deptId, agentId, userId))
                    .messages(Collections.emptyList())
                    .hasMore(false)
                    .nextBefore(null)
                    .build();
        }
    }

    @lombok.AllArgsConstructor @lombok.Data
    public static class ConvView {
        private Long id;
        private Long deptId;
        private Long agentId;
        private Long userId;
    }

    @lombok.Builder @lombok.Data
    public static class MessageView {
        private Long serverMsgId;
        private String clientMsgId;
        private String from;         // USER/AGENT/SYSTEM
        private String msgType;      // TEXT/IMAGE/FILE/SYSTEM
        private Map<String,Object> payload;
        private Long quoteMsgId;
        private boolean revoked;
        private Long ts;
    }
}

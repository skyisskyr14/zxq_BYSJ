// com.skyissky.ws.ws.ChatAgentEndpoint
package com.sq.system.ws.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sq.system.ws.server.ChatSequenceService;
import com.sq.system.ws.server.ChatStoreService;
import com.sq.system.ws.support.SpringContext;               // ★
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@ServerEndpoint("/ws/chat/agent/{agentId}")
public class ChatAgentEndpoint {
    private static final ObjectMapper M = new ObjectMapper();
    private ChatSequenceService seq() {
        return SpringContext.getBean(ChatSequenceService.class);
    }

    private Long agentId;

    // ★ 从 Spring 容器拿 Service
    private ChatStoreService store() {
        return SpringContext.getBean(ChatStoreService.class);
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("agentId") Long agentId) {
        this.agentId = agentId;
        ChatSessions.AGENT.put(agentId, session);
    }

    @OnClose
    public void onClose(Session s) {
        if (agentId != null) ChatSessions.AGENT.remove(agentId);
    }

    @OnError
    public void onError(Session s, Throwable e) { log.error("AGENT WS error", e); }

    @OnMessage
    public void onMessage(String message, Session session) throws Exception {
        var node = M.readTree(message);
        String type = node.path("type").asText();

        long toUserId     = node.path("toUserId").asLong();
        String clientMsgId= node.path("clientMsgId").asText();
        long ts           = System.currentTimeMillis();

        // 1) 先确保会话并取 convId
        Long convId = store().ensureConvId(toUserId, agentId);

        // 2) 基于该 convId 取“不会倒退”的 serverMsgId（0 开始）
        long serverMsgId = seq().nextForConv(convId);

        if ("MESSAGE".equals(type)) {    // 注意：坐席端用的是 MESSAGE（你协议里的设定）
            String text = node.path("text").asText("");

            // 1) ACK 回给客服
            session.getAsyncRemote().sendText(M.writeValueAsString(Map.of(
                    "type","ACK","clientMsgId",clientMsgId,"serverMsgId",serverMsgId,"ts",ts
            )));

            // 2) ★ 入库（客服→用户：文本）
            store().storeAgentText(agentId, toUserId, text, clientMsgId, serverMsgId, ts);

            // 3) 转给用户
            var user = ChatSessions.USER.get(toUserId);
            if (user != null && user.isOpen()) {
                user.getAsyncRemote().sendText(M.writeValueAsString(Map.of(
                        "type","MESSAGE",
                        "from","AGENT","userId",toUserId,"agentId",agentId,
                        "serverMsgId",serverMsgId,"text",text,"ts",ts
                )));
            }
        }
        else if ("ATTACHMENT".equals(type)) {
            var payload = M.convertValue(node.path("payload"), Map.class);

            // 1) ACK
            session.getAsyncRemote().sendText(M.writeValueAsString(Map.of(
                    "type","ACK","clientMsgId",clientMsgId,"serverMsgId",serverMsgId,"ts",ts
            )));

            // 2) ★ 入库（客服→用户：附件/图片）
            store().storeAgentAttachment(agentId, toUserId, payload, clientMsgId, serverMsgId, ts);

            // 3) 转给用户
            var user = ChatSessions.USER.get(toUserId);
            if (user != null && user.isOpen()) {
                user.getAsyncRemote().sendText(M.writeValueAsString(Map.of(
                        "type","ATTACHMENT",
                        "from","AGENT","userId",toUserId,"agentId",agentId,
                        "serverMsgId",serverMsgId,"ts",ts,
                        "payload",payload
                )));
            }
        }
    }

}

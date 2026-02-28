// com.skyissky.ws.ws.ChatUserEndpoint
package com.sq.system.ws.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sq.system.ws.entity.KfNormalQEntity;
import com.sq.system.ws.repository.KfNormalQRepository;
import com.sq.system.ws.server.ChatSequenceService;
import com.sq.system.ws.server.ChatStoreService;
import com.sq.system.ws.support.SpringContext;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@ServerEndpoint("/ws/chat/user/{userId}")
public class ChatUserEndpoint {

    public ChatUserEndpoint() {}

    private static final ObjectMapper M = new ObjectMapper();
    private Long userId;

    /* ---- 从 Spring 取 Bean（不要在 @ServerEndpoint 类里用 @Autowired/@Resource） ---- */
    private ChatStoreService store() { return SpringContext.getBean(ChatStoreService.class); }
    private ChatSequenceService seq() { return SpringContext.getBean(ChatSequenceService.class); }
    private KfNormalQRepository normalRepo() { return SpringContext.getBean(KfNormalQRepository.class); }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        this.userId = userId;
        ChatSessions.USER.put(userId, session);
    }

    @OnClose
    public void onClose(Session session) {
        if (userId != null) ChatSessions.USER.remove(userId);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("USER WS error", error);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws Exception {
        JsonNode node = M.readTree(message);
        String type = node.path("type").asText("");
        long toAgentId   = node.path("toAgentId").asLong(node.path("agentId").asLong(0));
        String clientMsgId = node.path("clientMsgId").asText("");
        long ts = System.currentTimeMillis();

        Long convId = store().ensureConvId(userId, toAgentId);
        long serverMsgId = seq().nextForConv(convId);

        if ("MESSAGE".equalsIgnoreCase(type)) {
            String text = node.path("text").asText("");

            System.out.println("接受到用户消息："+text);

            session.getAsyncRemote().sendText(M.writeValueAsString(Map.of(
                    "type","ACK","clientMsgId",clientMsgId,"serverMsgId",serverMsgId,"ts",ts
            )));
            log.info("{} -> agent {} : {}", userId, toAgentId, text);

            store().storeUserText(userId, toAgentId, text, clientMsgId, serverMsgId, ts);

            Session agent = ChatSessions.AGENT.get(toAgentId);
            if (agent != null && agent.isOpen()) {
                agent.getAsyncRemote().sendText(M.writeValueAsString(Map.of(
                        "type","MESSAGE",
                        "from","USER","userId",userId,"agentId",toAgentId,
                        "serverMsgId",serverMsgId,"text",text,"ts",ts
                )));
            }
            return;
        }

        if ("ATTACHMENT".equalsIgnoreCase(type)) {
            Map<String,Object> payload = M.convertValue(node.path("payload"), Map.class);

            session.getAsyncRemote().sendText(M.writeValueAsString(Map.of(
                    "type","ACK","clientMsgId",clientMsgId,"serverMsgId",serverMsgId,"ts",ts
            )));

            store().storeUserAttachment(userId, toAgentId, payload, clientMsgId, serverMsgId, ts);

            Session agent = ChatSessions.AGENT.get(toAgentId);
            if (agent != null && agent.isOpen()) {
                agent.getAsyncRemote().sendText(M.writeValueAsString(Map.of(
                        "type","ATTACHMENT",
                        "from","USER","userId",userId,"agentId",toAgentId,
                        "serverMsgId",serverMsgId,"ts",ts,"payload",payload
                )));
            }
            return;
        }

        if ("FAQ1".equalsIgnoreCase(type)) {
            // 兼容两种前端负载：
            // 1) { type:'FAQ1', toAgentId, clientMsgId, faqId, payload:{question} }
            // 2) { type:'FAQ1', toAgentId, clientMsgId, payload:{ id, question } }
            JsonNode payload = node.path("payload");
            long faqId = node.path("faqId").asLong(payload.path("id").asLong(0));
            String question = node.path("question").asText(payload.path("question").asText(""));

            session.getAsyncRemote().sendText(M.writeValueAsString(Map.of(
                    "type","ACK","clientMsgId",clientMsgId,"serverMsgId",serverMsgId,"ts",ts
            )));

            // 入库“用户选择了 FAQ”（不加表）
            store().storeUserFAQ(userId, toAgentId, faqId, question, clientMsgId, serverMsgId, ts);

            // 转发问题给坐席
            Session agent = ChatSessions.AGENT.get(toAgentId);
            if (agent != null && agent.isOpen()) {
                agent.getAsyncRemote().sendText(M.writeValueAsString(Map.of(
                        "type","MESSAGE",
                        "from","USER","userId",userId,"agentId",toAgentId,
                        "serverMsgId",serverMsgId,"text",question,"ts",ts
                )));
            }

            // 自动回复
            String answer = findFaqAnswerSafe(faqId);
            long srvIdAnswer = seq().nextForConv(convId);
            long tsAns = System.currentTimeMillis();

            String autoClientId = "AUTO_FAQ1_" + srvIdAnswer;   // 幂等用（可选）
            store().storeAgentText(toAgentId, userId, answer, autoClientId, srvIdAnswer, tsAns);

            Session user = ChatSessions.USER.get(userId);
            if (user != null && user.isOpen()) {
                user.getAsyncRemote().sendText(M.writeValueAsString(Map.of(
                        "type","MESSAGE",
                        "from","AGENT","userId",userId,"agentId",toAgentId,
                        "serverMsgId",srvIdAnswer,"text",answer,"ts",tsAns
                )));
            }
            if (agent != null && agent.isOpen()) {
                agent.getAsyncRemote().sendText(M.writeValueAsString(Map.of(
                        "type","MESSAGE",
                        "from","AGENT","userId",userId,"agentId",toAgentId,
                        "serverMsgId",srvIdAnswer,"text","[系统自动回复] " + answer,"ts",tsAns
                )));
            }
            return;
        }

        log.warn("Unknown user message type: {}", type);
    }

    private String findFaqAnswerSafe(long faqId) {
        try {
            KfNormalQRepository repo = normalRepo();
            if (repo == null) {
                log.warn("KfNormalQRepository bean not found in SpringContext");
                return "客服暂时没有配置回应,请等待客户回复";
            }
            if (faqId <= 0) return "客服暂时没有配置回应,请等待客户回复";
            KfNormalQEntity e = repo.selectById(faqId);
            if (e == null) return "客服暂时没有配置回应,请等待客户回复";
            String ans = e.getResult();
            if (ans == null || ans.isBlank()) return "客服暂时没有配置回应,请等待客户回复";
            return ans;
        } catch (Exception ex) {
            log.error("FAQ1 lookup error, faqId={}", faqId, ex);
            return "客服暂时没有配置回应,请等待客户回复";
        }
    }
}

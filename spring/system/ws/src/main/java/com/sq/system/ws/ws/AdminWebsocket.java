// com.skyissky.ws.ws.ChatUserEndpoint
package com.sq.system.ws.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sq.system.common.utils.JsonUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/ws/fans/admin/1geyi/{adminId}")
public class AdminWebsocket {
    private static final Map<Long, Session> adminSessionMap = new ConcurrentHashMap<>();

    private static final ObjectMapper M = new ObjectMapper();
    private Long adminId;

    @OnOpen
    public void onOpen(Session session, @PathParam("adminId") Long adminId) {
        this.adminId = adminId;
        adminSessionMap.put(adminId, session);
    }

    @OnClose
    public void onClose(Session session) {
        if (adminId != null) adminSessionMap.remove(adminId);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("Admin WS error", error);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws Exception {
    }

    public static void sendToAdmin(Long adminId, String content) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("content", content);

        String json = JsonUtil.toJson(msg);
        Session session = adminSessionMap.get(adminId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(json);
            } catch (Exception e) {
                log.warn("向用户 {} 推送消息失败", adminId, e);
            }
        }
    }
}

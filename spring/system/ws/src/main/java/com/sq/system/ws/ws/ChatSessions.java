// com.skyissky.ws.ws.ChatSessions
package com.sq.system.ws.ws;

import jakarta.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatSessions {
    // 用户ID -> WS会话
    public static final Map<Long, Session> USER = new ConcurrentHashMap<>();
    // 客服ID -> WS会话
    public static final Map<Long, Session> AGENT = new ConcurrentHashMap<>();
}

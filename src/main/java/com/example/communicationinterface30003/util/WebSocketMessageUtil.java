package com.example.communicationinterface30003.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.example.communicationinterface30003.model.WebSocketMessage;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Slf4j
/**
 *
 * @author lst
 * @date 2022/12/15 11:12
 * @param null
 * @return null
 */
public class WebSocketMessageUtil {

    /**
     * 客户端的连接会话
     */
    public static final ConcurrentHashMap<String, Session> SESSION_MAP = new ConcurrentHashMap<>();

    /**
     * @param session 客户端
     * @param message 消息
     *                向指定客户端推送消息
     */
    public static void sendMessage(Session session, String message) {
        if (ObjectUtil.isNotNull(session)) {
            try {
                synchronized (session) {
                    session.getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
                log.info("向指定客户端推送消息异常");
                log.error(e.getMessage());
            }
        }
    }

    public static void close(Session session) {
        log.info("连接关闭，sid=" + session.getId());
        for (String key : SESSION_MAP.keySet()) {
            if (SESSION_MAP.get(key).getId().equals(session.getId())) {
                SESSION_MAP.remove(key);
            }
        }
    }

    /**
     * 发送到所有用户
     */
    public static void sendMessageToAll(Integer code, String message) {
        for (Session session : SESSION_MAP.values()) {
            // 检查会话是否处于打开状态
            if (session.isOpen()) {
                log.info("检测会话正常*********************************************");
                sendWebSocketMessage(session, code, message);
            }else{
                log.warn("有会话已关闭");
            }
        }
    }

    /**
     * @param session          客户端
     * @param webSocketMessage 消息
     *                         向指定客户端推送WebSocketMessage消息
     */
    public static void sendWebSocketMessage(Session session, WebSocketMessage webSocketMessage) {
        String message = JSONUtil.toJsonStr(webSocketMessage);
        sendMessage(session, message);
    }

    /**
     * 从后端往前端发数据
     *
     * @param session
     * @param code
     * @param message
     */
    public static void sendWebSocketMessage(Session session, Integer code, String message) {
        WebSocketMessage webSocketMessage = new WebSocketMessage();
        webSocketMessage.setCode(code);
        webSocketMessage.setMsg(message);
        sendWebSocketMessage(session, webSocketMessage);
    }


}

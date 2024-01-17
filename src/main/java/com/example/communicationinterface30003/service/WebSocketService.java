package com.example.communicationinterface30003.service;

import com.example.communicationinterface30003.util.WebSocketMessageUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.example.communicationinterface30003.util.WebSocketMessageUtil.SESSION_MAP;


/**
 * ws://127.0.0.1:8081/api/pad/websocket
 */
@Component
@Slf4j
@ServerEndpoint("/websocket")
/**
 *
 * @author lst
 * @date 2022/12/15 11:12
 * @param null
 * @return null
 */
public class WebSocketService {

    @OnOpen
    public void onOpen(Session session) throws IOException {
        SESSION_MAP.put(session.getId(), session);
        log.info("客户端建立连接:{}", session);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        log.info("客户端结束连接:{}", session);
        WebSocketMessageUtil.close(session);
    }

    /**
     * 连接错误调用的方法
     */
    @OnError
    public void onError(Throwable error) {
        log.error("错误原因:" + error.getMessage());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     **/
    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("收到并开始处理消息:{}", message);
    }

}

package com.sky.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;

/**
 * @projectName: super-takeout
 * @package: com.sky.websocket
 * @className: WebSocketServer
 * @author: 749291
 * @description: TODO
 * @date: 5/12/2024 16:22
 * @version: 1.0
 */

@Component
@Slf4j
@ServerEndpoint("/ws/{sid}")
public class WebSocketServer {
    private static HashMap<String, Session> sessionMap = new HashMap<>();

    public static synchronized void sendMessageToAllClient(String message) {
        for (Session session : sessionMap.values()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        log.info("有新窗口开始监听:{}" + session.getId());
        sessionMap.put(sid, session);
    }

    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        log.info("有一连接关闭: {}", sid);
        sessionMap.remove(sid);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来自窗口" + session.getId() + "的信息:" + message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }
}

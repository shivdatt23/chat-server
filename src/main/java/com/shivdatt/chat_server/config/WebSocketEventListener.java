package com.shivdatt.chat_server.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.shivdatt.chat_server.controller.ChatMessage;
import com.shivdatt.chat_server.controller.MessageType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j //for logging when user leaves the chat
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messageSendingOperations;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor=StompHeaderAccessor.wrap(event.getMessage());
        String username=(String) headerAccessor.getSessionAttributes().get("username");
        if (username!=null) {
            log.info("User disconnected : {}",username);
            var chatMessage=ChatMessage.builder()
            .type(MessageType.LEAVE)
            .sender(username)
            .build();
            messageSendingOperations.convertAndSend("/topic/public",chatMessage); //this will send to the other users that this user has left the chat
        }
    }

}

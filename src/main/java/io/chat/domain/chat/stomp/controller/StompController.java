package io.chat.domain.chat.stomp.controller;

import io.chat.domain.chat.dto.ChatMessageRequestDto;
import io.chat.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompController {

    private final SimpMessageSendingOperations messageTemplate;
    private final ChatService chatService;

    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageRequestDto chatMessageRequestDto) {

        log.info("Received message: {}", chatMessageRequestDto);

        chatService.saveMessage(roomId, chatMessageRequestDto);

        messageTemplate.convertAndSend("/topic/"+roomId, chatMessageRequestDto);
    }
}

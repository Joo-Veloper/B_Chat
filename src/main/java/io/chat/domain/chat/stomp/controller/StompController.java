package io.chat.domain.chat.stomp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.chat.domain.chat.dto.ChatMessageRequestDto;
import io.chat.domain.chat.service.ChatService;
import io.chat.domain.chat.service.RedisPubSubService;
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
    private final RedisPubSubService pubSubService;

    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageRequestDto chatMessageRequestDto) throws JsonProcessingException {

        log.info("Received message: {}", chatMessageRequestDto);

        chatService.saveMessage(roomId, chatMessageRequestDto);

        chatMessageRequestDto.setRoomId(roomId);
//        messageTemplate.convertAndSend("/topic/"+roomId, chatMessageReqDto);

        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(chatMessageRequestDto);
        pubSubService.publish("chat", message);
    }
}

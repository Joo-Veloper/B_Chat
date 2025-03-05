package io.chat.domain.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.chat.domain.chat.dto.ChatMessageRequestDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisPubSubService implements MessageListener {

    private final StringRedisTemplate stringRedisTemplate;
    private final SimpMessageSendingOperations messageTemplate;

    public RedisPubSubService(@Qualifier("chatPubSub") StringRedisTemplate stringRedisTemplate, SimpMessageSendingOperations messageTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.messageTemplate = messageTemplate;
    }

    public void publish(String channel, String message){
        stringRedisTemplate.convertAndSend(channel, message);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {

        String payload = new String(message.getBody());
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ChatMessageRequestDto chatMessageDto = objectMapper.readValue(payload, ChatMessageRequestDto.class);
            messageTemplate.convertAndSend("/topic/" + chatMessageDto.getRoomId(), chatMessageDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
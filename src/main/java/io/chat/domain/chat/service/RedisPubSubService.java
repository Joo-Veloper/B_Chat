package io.chat.domain.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.chat.domain.chat.dto.ChatMessageRequestDto;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
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
    private final MeterRegistry meterRegistry;

    public RedisPubSubService(@Qualifier("chatPubSub") StringRedisTemplate stringRedisTemplate, SimpMessageSendingOperations messageTemplate, MeterRegistry meterRegistry) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.messageTemplate = messageTemplate;
        this.meterRegistry = meterRegistry;
    }

    public void publish(String channel, String message){
        stringRedisTemplate.convertAndSend(channel, message);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        long startTime = System.nanoTime();

        String payload = new String(message.getBody());
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ChatMessageRequestDto chatMessageDto = objectMapper.readValue(payload, ChatMessageRequestDto.class);
            messageTemplate.convertAndSend("/topic/" + chatMessageDto.getRoomId(), chatMessageDto);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            long duration = System.nanoTime() - startTime;

            Timer.builder("redis.pubsub.message.processing.time")
                    .tag("channel", "chat")
                    .register(meterRegistry)
                    .record(duration, java.util.concurrent.TimeUnit.NANOSECONDS);

            System.out.println("Processed message in " + duration + "ns");
        }
    }
}
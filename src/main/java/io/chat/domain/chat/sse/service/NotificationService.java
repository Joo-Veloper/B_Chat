package io.chat.domain.chat.sse.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private final Map<Long, SseEmitter> clients = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = new SseEmitter(60 * 1000L); // 1분 타임아웃
        clients.put(memberId, emitter);

        emitter.onCompletion(() -> clients.remove(memberId));
        emitter.onTimeout(() -> clients.remove(memberId));

        return emitter;
    }

    public void sendNotification(Long memberId, String message) {
        SseEmitter emitter = clients.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("message").data(message));
            } catch (IOException e) {
                clients.remove(memberId);
            }
        }
    }
}

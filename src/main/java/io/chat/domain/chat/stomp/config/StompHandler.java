package io.chat.domain.chat.stomp.config;

import io.chat.domain.chat.service.ChatService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {

    @Value("${jwt.secretKey}")
    private String secretKey;

    private final ChatService chatService;
    private final WebSocketMetrics webSocketMetrics; // ✅ WebSocket 메트릭 추가

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            log.info("WebSocket CONNECT 요청: 토큰 유효성 검증 시작");

            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String token = bearerToken.substring(7);

            // 토큰 검증
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.info("WebSocket CONNECT 요청: 토큰 검증 완료");

            webSocketMetrics.incrementActiveConnections();
        }

        if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            log.info("WebSocket SUBSCRIBE 요청 검증 시작");

            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String token = bearerToken.substring(7);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            String roomId = accessor.getDestination().split("/")[2];

            if (!chatService.isRoomParticipant(email, Long.parseLong(roomId))) {
                throw new AuthenticationServiceException("해당 방에 권한이 없습니다.");
            }

            log.info("WebSocket SUBSCRIBE 요청 검증 완료");
        }

        if (StompCommand.SEND == accessor.getCommand()) {
            log.info("WebSocket 메시지 전송 감지");

            webSocketMetrics.incrementSentMessages();
        }

        if (StompCommand.DISCONNECT == accessor.getCommand()) {
            log.info("WebSocket DISCONNECT 요청 감지");

            webSocketMetrics.decrementActiveConnections();
        }

        return message;
    }
}

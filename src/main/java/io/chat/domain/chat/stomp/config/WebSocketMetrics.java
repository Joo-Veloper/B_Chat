package io.chat.domain.chat.stomp.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class WebSocketMetrics {
    private final MeterRegistry meterRegistry;

    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private final Counter receivedMessages;
    private final Counter sentMessages;

    public WebSocketMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.receivedMessages = Counter.builder("websocket_messages_received_total")
                .description("Total number of received WebSocket messages")
                .register(meterRegistry);
        this.sentMessages = Counter.builder("websocket_messages_sent_total")
                .description("Total number of sent WebSocket messages")
                .register(meterRegistry);

        Gauge.builder("websocket_active_connections", activeConnections, AtomicInteger::get)
                .description("Number of active WebSocket connections")
                .register(meterRegistry);
    }

    public void incrementReceivedMessages() {
        receivedMessages.increment();
    }

    public void incrementSentMessages() {
        sentMessages.increment();
    }

    public void incrementActiveConnections() {
        activeConnections.incrementAndGet();
    }

    public void decrementActiveConnections() {
        activeConnections.decrementAndGet();
    }

}

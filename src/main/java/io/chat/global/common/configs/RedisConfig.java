package io.chat.global.common.configs;

import io.chat.domain.chat.service.RedisPubSubService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;

    @Bean
    @Qualifier("chatPubSub")
    public RedisConnectionFactory chatPubSubFactory(){

        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);

        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    @Qualifier("chatPubSub")
    public StringRedisTemplate stringRedisTemplate(@Qualifier("chatPubSub") RedisConnectionFactory redisConnectionFactory){

        return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @Qualifier("chatPubSub") RedisConnectionFactory redisConnectionFactory,
            MessageListenerAdapter messageListenerAdapter
    ){

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListenerAdapter, new PatternTopic("chat"));
        return container;
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisPubSubService redisPubSubService){

        return new MessageListenerAdapter(redisPubSubService, "onMessage");
    }
}

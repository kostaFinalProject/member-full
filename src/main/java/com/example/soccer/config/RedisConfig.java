package com.example.soccer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration // @Bean 메서드를 정의하여 Bean을 등록
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        return new LettuceConnectionFactory(configuration);
//        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public CacheManager redisCacheManager(
            RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration configuration = RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeKeysWith(SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(configuration)
                .build();
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        return container;
    }

    @Bean // Spring 컨테이너에 Bean으로 등록되어 다른 곳에서 사용할 수 있도록 한다는 의미 > 메서드의 반환 값은 RedisTemplate<String, Object> 타입
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) { // redisTemplate() - RedisTemplate을 설정하고 반환하는 역할(Redis 서버와의 상호작용을 추상화하여 Redis 데이터에 쉽게 접근)
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory); // Redis와의 연결을 템플릿에 전달
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // Redis에 저장되는 키의 직렬화 방식을 설정
        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Object.class)); // Redis에 저장되는 값의 직렬화 방식을 설정
        return redisTemplate;
    }
}

package com.cococlown.cococlawservice.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;

/**
 * Redis配置
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 使用StringRedisSerializer来序列化和反序列化redis的key-value
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // 使用Fastjson2Serializer来序列化和反序列化redis的value
        Fastjson2Serializer<Object> fastjson2Serializer = new Fastjson2Serializer<>();
        template.setValueSerializer(fastjson2Serializer);
        template.setHashValueSerializer(fastjson2Serializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * Fastjson2序列化器
     */
    public static class Fastjson2Serializer<T> implements RedisSerializer<T> {

        @SuppressWarnings("unchecked")
        @Override
        public T deserialize(byte[] bytes) {
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            return JSON.parseObject(bytes, (Class<T>) Object.class, JSONReader.Feature.SupportAutoType);
        }

        @Override
        public byte[] serialize(T t) {
            if (t == null) {
                return new byte[0];
            }
            return JSON.toJSONBytes(t, JSONWriter.Feature.WriteClassName);
        }
    }
}

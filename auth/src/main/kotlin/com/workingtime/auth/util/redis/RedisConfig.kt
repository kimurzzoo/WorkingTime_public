package com.workingtime.auth.util.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Value("\${spring.redis.host}")
    private var host: String = ""

    @Value("\${spring.redis.port}")
    private var port = 0

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory
    {
        return LettuceConnectionFactory(host, port)
    }

    @Bean
    fun stringRedisTemplate() : StringRedisTemplate
    {
        var stringRedisTemplate : StringRedisTemplate = StringRedisTemplate()
        stringRedisTemplate.keySerializer = StringRedisSerializer()
        stringRedisTemplate.valueSerializer = StringRedisSerializer()
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory())
        return stringRedisTemplate
    }
}
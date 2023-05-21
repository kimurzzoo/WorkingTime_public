package com.workingtime.auth.util.redis

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration


@Service
class RedisUtil {
    
    @Autowired
    lateinit var stringRedisTemplate : StringRedisTemplate

    fun getData(key: String): String? {
        val valueOperations = stringRedisTemplate.opsForValue()
        return valueOperations[key]
    }

    fun setDataExpire(key: String, value: String, duration: Long) {
        val valueOperations = stringRedisTemplate.opsForValue()
        val expireDuration: Duration = Duration.ofSeconds(duration)
        valueOperations.set(key, value, expireDuration)
    }

    fun deleteData(key: String) : Boolean {
        return stringRedisTemplate.delete(key)
    }
}
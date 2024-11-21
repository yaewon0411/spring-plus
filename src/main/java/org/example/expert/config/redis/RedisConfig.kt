package org.example.expert.config.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.expert.controller.user.dto.response.UserInfoListRespDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableRedisRepositories
class RedisConfig {

    @Value("\${spring.redis.host:localhost}")
    private lateinit var host: String

    @Value("\${spring.redis.port:6379}")
    private var port: Int = 0

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(host, port)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, UserInfoListRespDto> {
        val redisTemplate = RedisTemplate<String, UserInfoListRespDto>()
        redisTemplate.setConnectionFactory(redisConnectionFactory())
        redisTemplate.keySerializer = StringRedisSerializer()

        val jsonSerializer = Jackson2JsonRedisSerializer(UserInfoListRespDto::class.java)
        redisTemplate.valueSerializer = jsonSerializer

        return redisTemplate
    }
}
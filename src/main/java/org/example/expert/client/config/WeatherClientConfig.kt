package org.example.expert.client.config


import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.*

@Configuration
class WeatherClientConfig {
    @Bean
    fun weatherRestTemplate(builder: RestTemplateBuilder): RestTemplate =
        builder
            .setConnectTimeout(Duration.ofSeconds(5))
            .setReadTimeout(Duration.ofSeconds(5))
            .build()
}
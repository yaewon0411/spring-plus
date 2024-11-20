package org.example.expert.client

import org.example.expert.client.config.WeatherClientConfig
import org.example.expert.client.dto.WeatherDto
import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class WeatherClient (
    private val restTemplate: RestTemplate,
    @Value("\${api.weather.base-url:https://f-api.github.io}") private val baseUrl: String,
    @Value("\${api.weather.path:/f-api/weather.json}") private val path: String
) {

    companion object{
        private val log = LoggerFactory.getLogger(this::class.java)
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd")
    }

    fun getTodayWeather(): String{
        val response = kotlin.runCatching {
            restTemplate.getForEntity<Array<WeatherDto>>(buildWeatherUri())
        }.getOrElse { e ->
            log.error("날씨 정보 조회 중 오류 발생: {}", e.message, e)
            throw CustomApiException(ErrorCode.FAIL_TO_GET_WEATHER)
        }

        val today = getCurrentDate()

        return response.body?.find { it.date == today }
            ?.weather
            ?: run{
                log.error("날씨 데이터를 찾을 수 없습니다: date = {}", today)
                throw CustomApiException(ErrorCode.WEATHER_NOT_FOUND)
            }
    }

    private fun buildWeatherUri(): URI =
        UriComponentsBuilder
            .fromUriString(baseUrl)
            .path(path)
            .encode()
            .build()
            .toUri();


    private fun getCurrentDate(): String =
        DATE_FORMATTER.let { LocalDateTime.now().format(it) }
}
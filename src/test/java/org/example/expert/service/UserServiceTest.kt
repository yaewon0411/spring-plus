package org.example.expert.service

import org.example.expert.controller.user.dto.response.UserInfoListRespDto
import org.example.expert.dummy.DummyUser
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.redis.core.RedisTemplate
import java.time.*

@SpringBootTest
class UserServiceTest{

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, UserInfoListRespDto>


    @BeforeEach
    fun setup() {
        val nickname = "리쿠군🤍"
        val page = 0
        val size = 10
        val cacheKey = "user:$nickname:$page:$size"

        val userData = userService.searchUserList(nickname, page, size)
        redisTemplate.opsForValue().set(cacheKey, userData, Duration.ofMinutes(10))
    }

    @Test
    @DisplayName("성능 측정: 기본 검색")
    fun test_basic_search() {
        val nickname = "리쿠군🤍"

        val start = System.currentTimeMillis()
        val result = userService.searchUserListWithoutIndex(nickname, 0, 10)
        val end = System.currentTimeMillis()

        println("result = ${result}")
        println("기본 검색 소요 시간:  ${end - start}ms")
    }

    @Test
    @DisplayName("성능 측정: 인덱스 사용")
    fun test_index_search() {
        val nickname = "리쿠군🤍"

        val start = System.currentTimeMillis()
        val result = userService.searchUserList(nickname, 0, 10)
        val end = System.currentTimeMillis()

        println("result = ${result}")
        println("인덱스 검색 소요 시간:  ${end - start}ms")
    }

    @Test
    @DisplayName("성능 측정: 해시 사용")
    fun test_hash_search() {
        val nickname = "리쿠군🤍"

        val start = System.currentTimeMillis()
        val result = userService.searchUserListWithHash(nickname, 0, 10)
        val end = System.currentTimeMillis()

        println("result = ${result}")
        println("해시 검색 소요 시간:  ${end - start}ms")
    }


    @Test
    @DisplayName("성능 측정: 캐시 사용")
    fun test_cache_search() {
        val nickname = "리쿠군🤍"

        val start = System.currentTimeMillis()
        val result = userService.searchUserListWithCache(nickname, 0, 10)
        val end = System.currentTimeMillis()

        println("result = ${result}")
        println("캐시 검색 소요 시간:  ${end - start}ms")
    }
}
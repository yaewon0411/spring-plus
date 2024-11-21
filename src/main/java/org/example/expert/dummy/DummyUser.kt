package org.example.expert.dummy;

import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityManager;
import org.example.expert.domain.user.User
import org.example.expert.domain.user.UserRepository;
import org.example.expert.domain.user.UserRole
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.util.*
import org.springframework.context.event.EventListener

@Component
class DummyUser (
    private val userRepository:UserRepository,
    private val entityManager:EntityManager
){
    @EventListener(ContextRefreshedEvent::class)
    @Transactional
    fun init() {
        if (userRepository.count() > 0) {
            println("이미 더미 데이터 존재")
            return
        }
        generateTestUsers()
    }

    fun generateTestUsers(count: Int = 500_000) {
        println("더미 데이터 생성 시작")

        val batchSize = 5000
        var processed = 0

        while (processed < count) {
            val batch = (1..batchSize).map {
                val index = processed + it
                User(
                    nickname = "u${index}",
                    email = "user${index}@test.com",
                    password = "password123",
                    userRole = UserRole.USER
                )
            }

            userRepository.saveAll(batch)

            processed += batchSize

            if (processed % (count/10) == 0) {
                println("진행률: $processed/$count users (${(processed.toDouble()/count * 100).toInt()}%)")
                entityManager.clear()  // 메모리 관리
            }
        }

        val actualCount = userRepository.count()
        println("더미 데이터 생성 완료. DB에 생성된 데이터 크기: $actualCount")
    }


}

package org.example.expert.domain.user

import org.example.expert.domain.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean

    @Query(
        value = "select /*+ NO_INDEX(users idx_nickname) */ * from users where nickname = :nickname order by modified_at desc",
        countQuery = "select count(*) from users where nickname = :nickname",
        nativeQuery = true
    )
    fun findUserByNicknameWithoutIndex(
        @Param("nickname") nickname: String,
        pageable: Pageable
    ): Page<User>

    fun findUserByNicknameEquals(nickname: String, pageable: Pageable) : Page<User>


    @Query("select u from User u where u.nicknameHash = :nicknameHash and u.nickname = :nickname")
    fun findByNicknameHashAndNickname(
        @Param("nicknameHash") nicknameHash: Int,
        @Param("nickname") nickname: String,
        pageable: Pageable
    ): Page<User>
}
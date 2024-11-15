package org.example.expert.domain.user.entity

import jakarta.persistence.*
import lombok.Builder
import org.example.expert.domain.common.dto.AuthUser
import org.example.expert.domain.common.entity.KTimestamped
import org.example.expert.domain.user.enums.UserRole

@Entity
@Table(name = "users")
class User protected constructor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true)
    var email: String,

    var password: String,

    @Enumerated(EnumType.STRING)
    var userRole: UserRole,

    @Column(length = 12, nullable = false)
    var nickname: String
): KTimestamped() {

    constructor(email: String, password: String, userRole: UserRole, nickname: String): this(
        id = null,
        email = email,
        password = password,
        userRole = userRole,
        nickname = nickname
    )

    //private 생성자
    private constructor(id: Long, email: String, userRole: UserRole, nickname: String): this(
        id = id,
        email = email,
        password = "", //private 생성자에서는 비밀번호 빈값으로 설정
        userRole = userRole,
        nickname = nickname
    )


    fun changePassword(password: String){
        this.password = password
    }

    fun updateRole(userRole: UserRole){
        this.userRole = userRole
    }

    companion object{
        @JvmStatic
        fun fromAuthUser(authUser: AuthUser) = User(
            id = authUser.id,
            email = authUser.email,
            userRole = authUser.userRole,
            nickname = authUser.nickname
        )
    }


}
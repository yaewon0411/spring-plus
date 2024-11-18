package org.example.expert.domain.user

import jakarta.persistence.*
import org.example.expert.domain.base.BaseEntity
import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode
import org.springframework.security.crypto.password.PasswordEncoder

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
): BaseEntity() {

    constructor(email: String, password: String, userRole: UserRole, nickname: String): this(
        id = null,
        email = email,
        password = password,
        userRole = userRole,
        nickname = nickname
    )

    //private 생성자
    constructor(id: Long, email: String, userRole: UserRole, nickname: String): this(
        id = id,
        email = email,
        password = "", //private 생성자에서는 비밀번호 빈값으로 설정
        userRole = userRole,
        nickname = nickname
    )

    fun validatePassword(oldPassword: String, passwordEncoder: PasswordEncoder){
        if(!passwordEncoder.matches(oldPassword, password)){
            throw CustomApiException(ErrorCode.INVALID_PASSWORD)
        }
    }

    fun changePassword(password: String){
        this.password = password
    }

    fun updateRole(userRole: UserRole){
        this.userRole = userRole
    }


}
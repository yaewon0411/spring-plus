package org.example.expert.controller.auth.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.example.expert.controller.auth.dto.request.valid.password.ValidPassword
import org.example.expert.controller.auth.dto.request.valid.userRole.ValidUserRole
import org.example.expert.domain.user.User
import org.example.expert.domain.user.UserRole
import org.springframework.security.crypto.password.PasswordEncoder

data class JoinReqDto(
    @field: NotBlank(message = "이메일을 입력해주세요")
    @field: Email(message = "유효한 이메일 형식이 아닙니다")
    val email: String = "",

    @field: NotBlank(message = "비밀번호를 입력해주세요")
    @field: ValidPassword
    val password: String = "",

    @field: NotBlank(message = "userRole을 입력해주세요")
    @field: ValidUserRole
    val userRole: String = "",
    @field: NotBlank(message = "닉네임을 입력해주세요")
    @field: Size(min = 1, max = 12, message = "닉네임은 1자 이상 12자 이하여야 합니다")
    val nickname: String = ""
) {
    fun toEntity(passwordEncoder: PasswordEncoder): User = User(
        email = email,
        password = passwordEncoder.encode(password),
        userRole = UserRole.valueOf(userRole),
        nickname = nickname
    )

}
package org.example.expert.controller.auth.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.example.expert.controller.auth.dto.request.valid.password.ValidPassword

data class LoginReqDto(
    @field: NotBlank(message = "이메일을 입력해주세요")
    @field: Email(message = "유효한 이메일 형식이 아닙니다")
    val email: String = "",

    @field: NotBlank(message = "비밀번호를 입력해주세요")
    @field: ValidPassword
    val password: String = ""
) {
}
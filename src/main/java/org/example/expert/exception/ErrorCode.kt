package org.example.expert.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val message: String,
    val status: Int
) {
    INVALID_USER_STATE("유효하지 않은 사용자 상태입니다", HttpStatus.BAD_REQUEST.value()),
    ALREADY_EXISTS_EMAIL("사용 중인 이메일입니다", HttpStatus.BAD_REQUEST.value())
}
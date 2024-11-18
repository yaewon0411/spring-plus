package org.example.expert.domain.auth.dto.response

import org.example.expert.domain.user.entity.User
import org.example.expert.exception.InvalidRequestException


data class LoginRespDto(
    val id: Long,
    val email: String
) {
    constructor(user: User): this(
        id = user.id?: throw InvalidRequestException("유효하지 않은 사용자입니다"),
        email = user.email
    )
}
package org.example.expert.controller.auth.dto.response

import org.example.expert.domain.user.User
import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode


data class JoinRespDto(
    val id: Long,
    val message: String = "회원가입을 축하합니다🤍"
) {
    constructor(user: User): this(
        id = user.id?: throw CustomApiException(ErrorCode.INVALID_USER_STATE)
    )
}
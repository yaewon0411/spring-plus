package org.example.expert.domain.auth.dto.response

import org.example.expert.domain.user.entity.User
import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode
import org.example.expert.exception.InvalidRequestException


data class JoinRespDto(
    val id: Long,
    val message: String = "회원가입을 축하합니다🤍"
) {
    constructor(user: User): this(
        id = user.id?: throw CustomApiException(ErrorCode.INVALID_USER_STATE)
    )
}
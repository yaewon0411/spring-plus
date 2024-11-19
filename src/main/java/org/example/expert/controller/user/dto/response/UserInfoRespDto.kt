package org.example.expert.controller.user.dto.response

import org.example.expert.domain.user.User
import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode

data class UserInfoRespDto(
    val id: Long,
    val email: String,
    val nickname: String
) {
    constructor(user: User): this(
        id = user.id?: throw CustomApiException(ErrorCode.INVALID_USER_STATE),
        email = user.email,
        nickname = user.nickname
    )
}
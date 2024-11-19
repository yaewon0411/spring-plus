package org.example.expert.domain.user

import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode

enum class UserRole {
    ADMIN, USER;

    companion object{
        @JvmStatic
        fun of(role: String): UserRole {
            return entries.find { it.name == role }
                ?: throw CustomApiException(ErrorCode.INVALID_USER_ROLE)
        }
    }
}
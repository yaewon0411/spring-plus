package org.example.expert.domain.user.enums

import org.example.expert.exception.InvalidRequestException

enum class UserRole {
    ADMIN, USER;

    companion object{
        @JvmStatic
        fun of(role: String): UserRole = try{
            valueOf(role.uppercase())
        }catch (e: IllegalArgumentException){
            throw InvalidRequestException("유효하지 않은 UserRole")
        }
    }
}
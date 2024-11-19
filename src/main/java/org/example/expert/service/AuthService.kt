package org.example.expert.service

import org.example.expert.controller.auth.dto.request.JoinReqDto
import org.example.expert.controller.auth.dto.response.JoinRespDto
import org.example.expert.domain.user.UserRepository
import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun join(joinReqDto: JoinReqDto): JoinRespDto = when {
        userRepository.existsByEmail(joinReqDto.email) ->
            throw CustomApiException(ErrorCode.ALREADY_EXISTS_EMAIL)
        else -> JoinRespDto(
            userRepository.save(joinReqDto.toEntity(passwordEncoder))
        )
    }

}
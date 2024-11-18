package org.example.expert.domain.auth.service

import org.example.expert.domain.auth.dto.request.JoinReqDto
import org.example.expert.domain.auth.dto.response.JoinRespDto
import org.example.expert.domain.user.repository.UserRepository
import org.example.expert.exception.InvalidRequestException
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
            throw InvalidRequestException("이미 존재하는 이메일입니다")
        else -> JoinRespDto(
            userRepository.save(joinReqDto.toEntity(passwordEncoder))
        )
    }

}
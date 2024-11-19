package org.example.expert.config.security.loginuser

import org.example.expert.domain.user.UserRepository
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LoginUserService(
    private val userRepository: UserRepository
): UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        return userRepository.findByEmail(email)
            ?.let { LoginUser(it) }
            ?: throw InternalAuthenticationServiceException("인증 실패")
    }
}
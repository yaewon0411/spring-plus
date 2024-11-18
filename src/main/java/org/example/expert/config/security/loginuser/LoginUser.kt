package org.example.expert.config.security.loginuser

import org.example.expert.domain.user.User
import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


class LoginUser(
    val user: User
): UserDetails {
    companion object{
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    val userId: Long
        get() = requireNotNull(user.id){
            log.error("인증된 사용자의 id가 null 입니다 - User: {}", user)
            CustomApiException(ErrorCode.INVALID_AUTHENTICATION_STATE)
        }
    override fun getAuthorities(): Collection<GrantedAuthority>
     = setOf(SimpleGrantedAuthority("ROLE_${user.userRole.name}"))

    override fun getPassword(): String = user.password

    override fun getUsername(): String = user.email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
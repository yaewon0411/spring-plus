package org.example.expert.config.security.loginuser

import org.example.expert.domain.user.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class LoginUser(
    val user: User
): UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority>
     = setOf(SimpleGrantedAuthority("ROLE_${user.userRole.name}"))

    override fun getPassword(): String = user.password

    override fun getUsername(): String = user.email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
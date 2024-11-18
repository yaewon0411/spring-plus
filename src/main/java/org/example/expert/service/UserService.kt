package org.example.expert.service

import org.example.expert.controller.user.dto.request.PasswordChangeReqDto
import org.example.expert.controller.user.dto.request.UserRoleChangeReqDto
import org.example.expert.controller.user.dto.response.UserInfoRespDto
import org.example.expert.domain.user.User
import org.example.expert.domain.user.UserRole
import org.example.expert.domain.user.UserRepository
import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun findByIdOrFail(userId: Long): User = userRepository
        .findById(userId)
        .orElseThrow { CustomApiException(ErrorCode.USER_NOT_FOUND) }

    fun getUserInfo(userId: Long): UserInfoRespDto = UserInfoRespDto(findByIdOrFail(userId))

    @Transactional
    fun changePassword(userId: Long, passwordChangeReqDto: PasswordChangeReqDto){
        val user = findByIdOrFail(userId)
        user.validatePassword(passwordChangeReqDto.oldPassword, passwordEncoder)
        passwordChangeReqDto.validatePasswordChange();
        user.changePassword(passwordEncoder.encode(passwordChangeReqDto.newPassword))
    }

    @Transactional
    fun changeUserRole(userId: Long, userRoleChangeReqDto: UserRoleChangeReqDto) =
        findByIdOrFail(userId).updateRole(UserRole.of(userRoleChangeReqDto.userRole))



}
package org.example.expert.service

import com.sun.jna.platform.win32.Netapi32Util.UserInfo
import org.example.expert.controller.user.dto.request.PasswordChangeReqDto
import org.example.expert.controller.user.dto.request.UserRoleChangeReqDto
import org.example.expert.controller.user.dto.response.UserInfoListRespDto
import org.example.expert.controller.user.dto.response.UserInfoRespDto
import org.example.expert.domain.user.User
import org.example.expert.domain.user.UserRole
import org.example.expert.domain.user.UserRepository
import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.*

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val redisTemplate: RedisTemplate<String, UserInfoListRespDto>
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

    fun searchUserList(nickname: String, page: Int, size: Int): UserInfoListRespDto {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "modifiedAt"))
        val userList = userRepository.findUserByNicknameEquals(nickname, pageable)
        return UserInfoListRespDto(userList)
    }

    fun searchUserListWithoutIndex(nickname: String, page: Int, size: Int): UserInfoListRespDto {
        val pageable = PageRequest.of(page, size)
        val userList = userRepository.findUserByNicknameWithoutIndex(nickname, pageable)
        return UserInfoListRespDto(userList)
    }

    fun searchUserListWithHash(nickname: String, page: Int, size: Int): UserInfoListRespDto {
        val pageable = PageRequest.of(page, size)
        val userList = userRepository.findByNicknameHashAndNickname(
            nicknameHash = nickname.hashCode(),
            nickname = nickname,
            pageable = pageable
        )
        return UserInfoListRespDto(userList)
    }

    fun searchUserListWithCache(nickname: String, page: Int, size: Int): UserInfoListRespDto {
        val cacheKey = "user:$nickname:$page:$size"
        return redisTemplate.opsForValue().get(cacheKey)?.let { cachedDto ->
            cachedDto
        } ?: run {
            val result = searchUserList(nickname, page, size)
            redisTemplate.opsForValue().set(cacheKey, result, Duration.ofMinutes(10))
            result
        }
    }


}
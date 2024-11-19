package org.example.expert.controller.manager.dto.response

import com.sun.jna.platform.win32.Netapi32Util.UserInfo
import org.example.expert.controller.user.dto.response.UserInfoRespDto
import org.example.expert.domain.manager.Manager
import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode

data class ManagerInfoRespDto(
    val id: Long,
    val userInfo: UserInfoRespDto
) {
    constructor(manager: Manager): this(
        id = manager.id?: throw CustomApiException(ErrorCode.INVALID_MANAGER_STATE),
        userInfo = UserInfoRespDto(manager.user)
    )
}
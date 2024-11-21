package org.example.expert.controller.user.dto.response

import com.sun.jna.platform.win32.Netapi32Util.UserInfo
import org.example.expert.domain.user.User
import org.springframework.data.domain.Page
import java.util.stream.LongStream.LongMapMultiConsumer

class UserInfoListRespDto(
    val userInfo: List<UserInfoRespDto> = listOf(),
    val totalElements: Long = 0,
    val pageNumber: Int = 0,
    val totalPages: Int = 0,
    val hasNext: Boolean = false,
    val hasPrevious: Boolean = false
) {

    constructor(userPage: Page<User>): this(
        userInfo = userPage.content.map { user -> UserInfoRespDto(user) },
        totalElements = userPage.totalElements,
        totalPages = userPage.totalPages,
        pageNumber = userPage.number,
        hasNext = userPage.hasNext(),
        hasPrevious = userPage.hasPrevious()
    )

}
package org.example.expert.controller.comment.dto.response

import com.sun.jna.platform.win32.Netapi32Util.UserInfo
import org.example.expert.controller.user.dto.response.UserInfoRespDto
import org.example.expert.domain.comment.Comment
import org.example.expert.domain.user.User
import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode

data class CommentCreateRespDto(
    val id: Long,
    val contents: String,
    val userInfo: UserInfoRespDto
) {
    constructor(user: User, comment: Comment): this(
        id = comment.id?: throw CustomApiException(ErrorCode.INVALID_COMMENT_STATE),
        contents = comment.contents,
        userInfo = UserInfoRespDto(user)
    )
}
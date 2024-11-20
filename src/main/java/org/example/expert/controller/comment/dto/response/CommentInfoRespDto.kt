package org.example.expert.controller.comment.dto.response

import org.example.expert.controller.user.dto.response.UserInfoRespDto

data class CommentInfoRespDto(
    val id: Long,
    val contents: String,
    val userInfo: UserInfoRespDto
) {
}
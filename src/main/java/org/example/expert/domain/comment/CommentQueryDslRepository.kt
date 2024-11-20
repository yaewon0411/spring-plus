package org.example.expert.domain.comment

import org.example.expert.controller.comment.dto.response.CommentInfoRespDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CommentQueryDslRepository {

    fun getCommentsWithUserByTodoId(todoId: Long, pageable: Pageable): Page<CommentInfoRespDto>
}
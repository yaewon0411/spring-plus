package org.example.expert.controller.comment.dto.request

import jakarta.validation.constraints.NotBlank
import org.example.expert.domain.comment.Comment
import org.example.expert.domain.todo.Todo
import org.example.expert.domain.user.User

data class CommentCreateReqDto(
    @field: NotBlank(message = "댓글 내용을 입력해야 합니다")
    var contents: String = ""
) {
    fun toEntity(user: User, todo: Todo): Comment =
        Comment(contents = contents, user = user, todo = todo)
}
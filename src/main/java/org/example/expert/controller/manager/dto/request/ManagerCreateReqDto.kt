package org.example.expert.controller.manager.dto.request

import jakarta.validation.constraints.NotNull
import org.example.expert.domain.manager.Manager
import org.example.expert.domain.todo.Todo
import org.example.expert.domain.user.User

data class ManagerCreateReqDto(
    @field: NotNull
    var targetUserId: Long = 0L  //일정 작성자가 배치하는 유저 id
) {
    fun toEntity(user: User, todo: Todo): Manager =
        Manager(user = user, todo = todo)
}
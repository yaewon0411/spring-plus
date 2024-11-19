package org.example.expert.controller.todo.dto.request

import jakarta.validation.constraints.NotBlank
import org.example.expert.domain.todo.Todo
import org.example.expert.domain.user.User
import org.hibernate.validator.constraints.Length

data class TodoCreateReqDto(
    @field: NotBlank(message = "제목을 입력해야 합니다")
    @field: Length(min = 1, max = 20, message = "1자에서 20자 사이로 입력해야 합니다")
    val title: String = "",

    @field: NotBlank(message = "내용을 입력해야 합니다")
    @field: Length(min = 1, max = 255, message = "1자에서 255자 사이로 입력해야 합니다")
    val contents: String = ""
){
    fun toEntity(user: User, weather: String): Todo =
        Todo(title = title, contents =  contents, weather = weather, user = user)
}
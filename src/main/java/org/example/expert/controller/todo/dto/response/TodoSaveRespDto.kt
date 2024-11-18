package org.example.expert.controller.todo.dto.response

import org.example.expert.controller.user.dto.response.UserInfoRespDto
import org.example.expert.domain.todo.Todo
import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode

data class TodoSaveRespDto(
    val id: Long,
    val title: String,
    val contents: String,
    val weather: String,
    val userInfo: UserInfoRespDto
) {
    constructor(todo: Todo): this(
        id = todo.id?: throw CustomApiException(ErrorCode.INVALID_TODO_STATE),
        title = todo.title,
        contents = todo.contents,
        weather = todo.weather,
        userInfo = UserInfoRespDto(todo.user)
    )
}
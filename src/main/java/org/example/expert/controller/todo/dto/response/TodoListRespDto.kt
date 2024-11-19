package org.example.expert.controller.todo.dto.response

import org.example.expert.domain.todo.Todo
import org.springframework.data.domain.Page

data class TodoListRespDto(
    val todoList: List<TodoInfoRespDto> = listOf(),
    val totalElements: Long,
    val pageNumber: Int,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
) {
    constructor(todoPage: Page<Todo>): this(
        todoList = todoPage.content.map { TodoInfoRespDto(it) },
        totalElements = todoPage.totalElements,
        totalPages = todoPage.totalPages,
        pageNumber = todoPage.number,
        hasNext = todoPage.hasNext(),
        hasPrevious = todoPage.hasPrevious()
    )
}
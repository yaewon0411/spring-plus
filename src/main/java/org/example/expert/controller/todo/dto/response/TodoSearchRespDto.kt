package org.example.expert.controller.todo.dto.response

import org.springframework.data.domain.Page
import java.time.LocalDateTime

data class TodoSearchRespDto(
    val todoList: List<TodoRespDto> = listOf(),
    val totalElements: Long,
    val totalPages: Int,
    val pageNumber: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
) {
    constructor(todoPage: Page<TodoRespDto>): this(
        todoList = todoPage.content,
        totalElements = todoPage.totalElements,
        totalPages = todoPage.totalPages,
        pageNumber = todoPage.number,
        hasNext = todoPage.hasNext(),
        hasPrevious = todoPage.hasPrevious()
    )

    data class TodoRespDto(
        val title: String,
        val createdAt: LocalDateTime,
        val totalCommentsCount: Long,
        val totalManagersCount: Long,
        val managerList: List<ManagerRespDto> = listOf()
    ){
        data class ManagerRespDto(
            val userId: Long,
            val managerNickname: String
        )

    }

}
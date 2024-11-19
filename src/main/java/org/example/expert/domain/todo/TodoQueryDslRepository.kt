package org.example.expert.domain.todo

import org.example.expert.controller.todo.dto.request.TodoListReqDto
import org.example.expert.controller.todo.dto.request.TodoSearchReqDto
import org.example.expert.controller.todo.dto.response.TodoSearchRespDto
import org.example.expert.controller.todo.dto.response.TodoSearchRespDto.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface TodoQueryDslRepository {
    fun findTodosByFilter(todoListReqDto: TodoListReqDto, pageable: Pageable): Page<Todo>
    fun findByIdWithUser(todoId: Long): Todo?
    fun searchTodosByFilter(todoSearchReqDto: TodoSearchReqDto, pageable: Pageable): Page<TodoRespDto>
}
package org.example.expert.service

import org.example.expert.client.WeatherClient
import org.example.expert.controller.todo.dto.request.TodoListReqDto
import org.example.expert.controller.todo.dto.request.TodoCreateReqDto
import org.example.expert.controller.todo.dto.request.TodoSearchReqDto
import org.example.expert.controller.todo.dto.response.TodoInfoRespDto
import org.example.expert.controller.todo.dto.response.TodoListRespDto
import org.example.expert.controller.todo.dto.response.TodoSaveRespDto
import org.example.expert.controller.todo.dto.response.TodoSearchRespDto
import org.example.expert.domain.todo.Todo
import org.example.expert.domain.todo.TodoRepository
import org.example.expert.domain.user.User
import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TodoService(
    private val todoRepository: TodoRepository,
    private val weatherClient: WeatherClient
) {
    @Transactional
    fun saveTodo(user: User, todoCreateReqDto: TodoCreateReqDto): TodoSaveRespDto = todoCreateReqDto
            .toEntity(user, weatherClient.getTodayWeather())
            .let { todoRepository.save(it) }
            .let { TodoSaveRespDto(it) }

    fun getTodoList(todoListReqDto: TodoListReqDto): TodoListRespDto {
        val pageable = PageRequest.of(todoListReqDto.page, todoListReqDto.size, Sort.by(Sort.Direction.DESC, "modifiedAt"))
        val todoPage = todoRepository.findTodosByFilter(todoListReqDto, pageable)
        return TodoListRespDto(todoPage)
    }

    fun getTodo(todoId: Long): TodoInfoRespDto =
        todoRepository.findByIdWithUser(todoId)
            ?.let { TodoInfoRespDto(it) }
            ?: throw CustomApiException(ErrorCode.TODO_NOT_FOUND)

    fun findByIdOrFail(todoId: Long): Todo = todoRepository
        .findById(todoId)
        .orElseThrow { CustomApiException(ErrorCode.TODO_NOT_FOUND) }

    fun searchTodoList(todoSearchReqDto: TodoSearchReqDto): TodoSearchRespDto{
        val pageable = PageRequest.of(todoSearchReqDto.page, todoSearchReqDto.size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val todoPage = todoRepository.searchTodosByFilter(todoSearchReqDto, pageable)
        return TodoSearchRespDto(todoPage)
    }

}
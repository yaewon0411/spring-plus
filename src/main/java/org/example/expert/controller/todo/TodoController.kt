package org.example.expert.controller.todo

import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.apache.coyote.Response
import org.example.expert.config.security.loginuser.LoginUser
import org.example.expert.controller.todo.dto.request.TodoListReqDto
import org.example.expert.controller.todo.dto.request.TodoSaveReqDto
import org.example.expert.controller.todo.dto.request.TodoSearchReqDto
import org.example.expert.controller.todo.dto.response.TodoInfoRespDto
import org.example.expert.controller.todo.dto.response.TodoListRespDto
import org.example.expert.controller.todo.dto.response.TodoSaveRespDto
import org.example.expert.controller.todo.dto.response.TodoSearchRespDto
import org.example.expert.service.TodoService
import org.example.expert.util.api.ApiResult
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/todos")
@Validated
class TodoController(
    private val todoService: TodoService
) {
    @PostMapping
    fun saveTodo(@Valid @RequestBody todoSaveReqDto: TodoSaveReqDto,
                 @AuthenticationPrincipal loginUser: LoginUser): ResponseEntity<ApiResult<TodoSaveRespDto>> =
        ResponseEntity.ok(ApiResult.success(todoService.saveTodo(loginUser.user, todoSaveReqDto)))

    @GetMapping
    fun getTodoListByFilter(@Valid todoListReqDto: TodoListReqDto): ResponseEntity<ApiResult<TodoListRespDto>> =
        ResponseEntity.ok(ApiResult.success(todoService.getTodoList(todoListReqDto)))

    @GetMapping("/{todoId}")
    fun getTodo(@PathVariable(value = "todoId") @Positive todoId: Long): ResponseEntity<ApiResult<TodoInfoRespDto>> =
        ResponseEntity.ok(ApiResult.success(todoService.getTodo(todoId)))

    @GetMapping("/search")
    fun searchTodoListByFilter(@Valid todoSearchReqDto: TodoSearchReqDto): ResponseEntity<ApiResult<TodoSearchRespDto>> =
        ResponseEntity.ok(ApiResult.success(todoService.searchTodoList(todoSearchReqDto)))
}
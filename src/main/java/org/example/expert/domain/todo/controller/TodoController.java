package org.example.expert.domain.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.config.security.loginuser.LoginUser;
import org.example.expert.domain.todo.dto.request.TodoSearchReqDto;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.request.TodoListReqDto;
import org.example.expert.domain.todo.dto.response.TodoListRespDto;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchRespDto;
import org.example.expert.domain.todo.service.TodoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todos")
    public ResponseEntity<TodoSaveResponse> saveTodo(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody TodoSaveRequest todoSaveRequest
    ) {
        return ResponseEntity.ok(todoService.saveTodo(loginUser.getUser(), todoSaveRequest));
    }

    @GetMapping("/todos")
    public ResponseEntity<TodoListRespDto> getTodos(@Valid TodoListReqDto todoListReqDto
    ) {
        return ResponseEntity.ok(todoService.getTodos(todoListReqDto));
    }

    @GetMapping("/todos/{todoId}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable(value = "todoId") Long todoId) {
        return ResponseEntity.ok(todoService.getTodo(todoId));
    }

    @GetMapping("/todos/search")
    public ResponseEntity<TodoSearchRespDto> searchTodos(@Valid TodoSearchReqDto todoSearchReqDto){
        return ResponseEntity.ok(todoService.searchTodos(todoSearchReqDto));
    }
}

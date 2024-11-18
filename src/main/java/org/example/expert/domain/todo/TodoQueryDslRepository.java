package org.example.expert.domain.todo;

import org.example.expert.controller.todo.dto.request.TodoListReqDto;
import org.example.expert.controller.todo.dto.request.TodoSearchReqDto;
import org.example.expert.domain.todo.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TodoQueryDslRepository {
    Page<Todo> findTodosByFilter(TodoListReqDto todoListReqDto, Pageable pageable);

    Optional<Todo> findByIdWithUser(Long todoId);

    Page<TodoRespDto> searchTodosByFilter(TodoSearchReqDto todoSearchReqDto, Pageable pageable);
}

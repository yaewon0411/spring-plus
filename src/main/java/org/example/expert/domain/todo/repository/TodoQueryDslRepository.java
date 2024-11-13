package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.request.TodoListReqDto;
import org.example.expert.domain.todo.dto.request.TodoSearchReqDto;
import org.example.expert.domain.todo.dto.response.TodoSearchRespDto;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.example.expert.domain.todo.dto.response.TodoSearchRespDto.*;

public interface TodoQueryDslRepository {
    Page<Todo> findTodosByFilter(TodoListReqDto todoListReqDto, Pageable pageable);

    Optional<Todo> findByIdWithUser(Long todoId);

    Page<TodoRespDto> searchTodosByFilter(TodoSearchReqDto todoSearchReqDto, Pageable pageable);
}

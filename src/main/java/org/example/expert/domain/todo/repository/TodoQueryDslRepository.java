package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.request.TodoSearchReqDto;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoQueryDslRepository {
    Page<Todo> searchTodosByFilter(TodoSearchReqDto todoSearchReqDto, Pageable pageable);
}

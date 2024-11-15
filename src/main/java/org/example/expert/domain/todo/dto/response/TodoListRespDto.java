package org.example.expert.domain.todo.dto.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

@NoArgsConstructor
@Getter
public class TodoListRespDto {

    private List<TodoResponse> todoList;

    private long totalElements;
    private int totalPages;
    private int pageNumber;
    private boolean hasNext;
    private boolean hasPrevious;

    public TodoListRespDto(Page<Todo> todoPageList) {
        this.todoList = todoPageList.getContent()
                .stream()
                .map(TodoResponse::new)
                .toList();
        this.totalElements = todoPageList.getTotalElements();
        this.totalPages = todoPageList.getTotalPages();
        this.pageNumber = todoPageList.getNumber();
        this.hasNext = todoPageList.hasNext();
        this.hasPrevious = todoPageList.hasPrevious();
    }
}

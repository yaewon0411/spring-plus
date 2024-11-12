package org.example.expert.domain.todo.dto.response;

import lombok.Getter;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.dto.response.UserResponse;

import java.time.LocalDateTime;

@Getter
public class TodoResponse {

    private final Long id;
    private final String title;
    private final String contents;
    private final String weather;
    private final UserResponse user;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public TodoResponse(Todo todo) {
        this.id = todo.getId();
        this.title = todo.getTitle();
        this.contents = todo.getContents();
        this.weather = todo.getWeather();
        this.user = new UserResponse(todo.getUser());
        this.createdAt = todo.getCreatedAt();
        this.modifiedAt = todo.getModifiedAt();
    }
}

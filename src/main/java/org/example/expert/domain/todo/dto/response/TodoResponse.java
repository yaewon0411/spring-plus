package org.example.expert.domain.todo.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.dto.response.UserResponse;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TodoResponse {

    private Long id;
    private String title;
    private String contents;
    private String weather;
    private UserResponse user;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

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

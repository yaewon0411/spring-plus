package org.example.expert.controller.todo.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.todo.Todo;
import org.example.expert.controller.user.dto.response.UserInfoRespDto;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TodoResponse {

    private Long id;
    private String title;
    private String contents;
    private String weather;
    private UserInfoRespDto user;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public TodoResponse(Todo todo) {
        this.id = todo.getId();
        this.title = todo.getTitle();
        this.contents = todo.getContents();
        this.weather = todo.getWeather();
        this.user = new UserInfoRespDto(todo.getUser());
        this.createdAt = todo.getCreatedAt();
        this.modifiedAt = todo.getModifiedAt();
    }
}

package org.example.expert.controller.todo.dto.response;

import lombok.Getter;
import org.example.expert.controller.user.dto.response.UserInfoRespDto;

@Getter
public class TodoSaveResponse {

    private final Long id;
    private final String title;
    private final String contents;
    private final String weather;
    private final UserInfoRespDto user;

    public TodoSaveResponse(Long id, String title, String contents, String weather, UserInfoRespDto user) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.weather = weather;
        this.user = user;
    }
}

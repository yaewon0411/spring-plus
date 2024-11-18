package org.example.expert.controller.comment.dto.response;

import lombok.Getter;
import org.example.expert.controller.user.dto.response.UserInfoRespDto;

@Getter
public class CommentSaveResponse {

    private final Long id;
    private final String contents;
    private final UserInfoRespDto user;

    public CommentSaveResponse(Long id, String contents, UserInfoRespDto user) {
        this.id = id;
        this.contents = contents;
        this.user = user;
    }
}

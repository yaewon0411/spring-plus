package org.example.expert.controller.comment.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.comment.Comment;
import org.example.expert.controller.user.dto.response.UserInfoRespDto;

@NoArgsConstructor
@Getter
public class CommentResponse {

    private Long id;
    private String contents;
    private UserInfoRespDto user;

    public CommentResponse(Long id, String contents, UserInfoRespDto user) {
        this.id = id;
        this.contents = contents;
        this.user = user;
    }

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.contents = comment.getContents();
        this.user = new UserInfoRespDto(comment.getUser());
    }
}
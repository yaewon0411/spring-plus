package org.example.expert.domain.comment.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.user.dto.response.UserResponse;

@NoArgsConstructor
@Getter
public class CommentResponse {

    private Long id;
    private String contents;
    private UserResponse user;

    public CommentResponse(Long id, String contents, UserResponse user) {
        this.id = id;
        this.contents = contents;
        this.user = user;
    }

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.contents = comment.getContents();
        this.user = new UserResponse(comment.getUser());
    }
}
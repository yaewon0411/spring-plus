package org.example.expert.domain.comment;

import org.example.expert.controller.comment.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentQueryDslRepository {
    Page<CommentResponse> getCommentsWithUserByTodoId(Long todoId, Pageable pageable);
}

package org.example.expert.domain.comment.repository;

import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentQueryDslRepository {
    Page<CommentResponse> getCommentsWithUserByTodoId(Long todoId, Pageable pageable);
}

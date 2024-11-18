package org.example.expert.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.controller.comment.dto.request.CommentSaveRequest;
import org.example.expert.controller.comment.dto.response.CommentListRespDto;
import org.example.expert.controller.comment.dto.response.CommentResponse;
import org.example.expert.controller.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.Comment;
import org.example.expert.domain.comment.CommentRepository;
import org.example.expert.domain.todo.Todo;
import org.example.expert.controller.user.dto.response.UserInfoRespDto;
import org.example.expert.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final TodoService todoService;

    @Transactional
    public CommentSaveResponse saveComment(User user, Long todoId, CommentSaveRequest commentSaveRequest) {
        Todo todo = todoService.findByIdOrFail(todoId);

        Comment newComment = new Comment(
                commentSaveRequest.getContents(),
                user,
                todo
        );

        Comment savedComment = commentRepository.save(newComment);

        return new CommentSaveResponse(
                savedComment.getId(),
                savedComment.getContents(),
                new UserInfoRespDto(user)
        );
    }

    public CommentListRespDto getComments(Long todoId, int page, int size) {
        todoService.findByIdOrFail(todoId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "modifiedAt"));
        Page<CommentResponse> commentPage = commentRepository.getCommentsWithUserByTodoId(todoId, pageable);
        return new CommentListRespDto(commentPage);
    }
}

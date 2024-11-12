package org.example.expert.domain.comment.service;

import org.example.expert.domain.comment.dto.response.CommentListRespDto;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private TodoService todoService;

    @Mock
    private CommentRepository commentRepository;

    @Test
    @DisplayName("todo 댓글 목록 페이징 조회 성공 테스트")
    void getComments_success(){

        //given
        Long todoId = 1L;
        int page = 0;
        int size = 10;

        Todo todo = new Todo("title","contents","weather", null);
        UserResponse userResponse = new UserResponse(1L, "user123@naver.com");
        List<CommentResponse> commentList = List.of(
                new CommentResponse(1L, "댓글 내용", userResponse),
                new CommentResponse(2L, "댓글 내용", userResponse)
        );

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "modifiedAt"));
        PageImpl<CommentResponse> commentPage = new PageImpl<>(commentList, pageable, commentList.size());

        //when
        when(todoService.findByIdOrFail(todoId)).thenReturn(todo);
        when(commentRepository.getCommentsWithUserByTodoId(todoId, pageable)).thenReturn(commentPage);

        CommentListRespDto result = commentService.getComments(todoId, page, size);

        //then
        assertAll(
                () -> assertThat(result.getCommentList()).hasSize(commentList.size()).containsExactlyElementsOf(commentList),
                () -> assertThat(result.getTotalElements()).isEqualTo(commentList.size()),
                () -> assertThat(result.getPageNumber()).isEqualTo(page),
                () -> assertThat(result.isHasNext()).isFalse(),
                () -> assertThat(result.isHasPrevious()).isFalse()
        );

    }

}
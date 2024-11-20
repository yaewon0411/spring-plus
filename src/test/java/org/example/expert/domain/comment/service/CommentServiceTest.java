package org.example.expert.domain.comment.service;

import org.example.expert.controller.comment.dto.response.CommentInfoRespDto;
import org.example.expert.controller.comment.dto.response.CommentListRespDto;
import org.example.expert.domain.comment.CommentRepository;
import org.example.expert.controller.user.dto.response.UserInfoRespDto;
import org.example.expert.domain.todo.Todo;
import org.example.expert.domain.user.User;
import org.example.expert.domain.user.UserRole;
import org.example.expert.exception.CustomApiException;
import org.example.expert.exception.ErrorCode;
import org.example.expert.service.TodoService;
import org.example.expert.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private TodoService todoService;

    @Mock
    private CommentRepository commentRepository;

    private User user;

    @BeforeEach
    void setUp(){
        user = new User(1L,"test@test.com", UserRole.USER, "nicknameeee");
    }


    @Test
    @DisplayName("todo 댓글 목록 페이징 조회 성공 테스트")
    void getComments_success(){

        //given
        Long todoId = 1L;
        int page = 0;
        int size = 10;

        Todo todo = new Todo("title","contents","weather", user);
        UserInfoRespDto userResponse = new UserInfoRespDto(1L, "user123@naver.com", "nickname");
        List<CommentInfoRespDto> commentList = List.of(
                new CommentInfoRespDto(1L, "댓글 내용", userResponse),
                new CommentInfoRespDto(2L, "댓글 내용", userResponse)
        );

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "modifiedAt"));
        PageImpl<CommentInfoRespDto> commentPage = new PageImpl<>(commentList, pageable, commentList.size());

        //when
        when(todoService.findByIdOrFail(todoId)).thenReturn(todo);
        when(commentRepository.getCommentsWithUserByTodoId(todoId, pageable)).thenReturn(commentPage);

        CommentListRespDto result = commentService.getCommentList(todoId, page, size);

        //then
        assertAll(
                () -> assertThat(result.getCommentList()).hasSize(commentList.size()).containsExactlyElementsOf(commentList),
                () -> assertThat(result.getTotalElements()).isEqualTo(commentList.size()),
                () -> assertThat(result.getPageNumber()).isEqualTo(page),
                () -> assertThat(result.getHasNext()).isFalse(),
                () -> assertThat(result.getHasPrevious()).isFalse()
        );

    }

    @Test
    @DisplayName("todo 댓글 목록 페이징 조회 성공 테스트: 댓글이 없는 경우")
    void getComments_success_noComments(){
        Long todoId = 1L;
        int page = 0;
        int size = 10;

        Todo todo = new Todo("title","contents","weather", user);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "modifiedAt"));
        Page<CommentInfoRespDto> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(todoService.findByIdOrFail(todoId)).thenReturn(todo);
        when(commentRepository.getCommentsWithUserByTodoId(todoId, pageable))
                .thenReturn(emptyPage);

        //when
        CommentListRespDto result = commentService.getCommentList(todoId, page, size);

        //then
        assertAll(
                () -> assertThat(result.getCommentList()).isEmpty(),
                () -> assertThat(result.getTotalElements()).isZero(),
                () -> assertThat(result.getTotalPages()).isZero(),
                () -> assertThat(result.getPageNumber()).isZero(),
                () -> assertThat(result.getHasNext()).isFalse(),
                () -> assertThat(result.getHasPrevious()).isFalse()
        );

    }

    @Test
    @DisplayName("todo 댓글 목록 페이징 조회 성공 테스트: 페이지 번호가 총 페이지 수 초과하면 빈 목록 반환")
    void getComments_success_pageNumberExceeded() {
        Long todoId = 1L;
        int page = 2;
        int size = 10;
        int totalCount = 12;
        Todo todo = new Todo("title","contents","weather", user);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "modifiedAt"));
        Page<CommentInfoRespDto> emptyPage = new PageImpl<>(List.of(), pageable, totalCount);

        when(todoService.findByIdOrFail(todoId)).thenReturn(todo);
        when(commentRepository.getCommentsWithUserByTodoId(todoId, pageable))
                .thenReturn(emptyPage);

        // when
        CommentListRespDto result = commentService.getCommentList(todoId, page, size);

        // then
        assertAll(
                () -> assertThat(result.getCommentList()).isEmpty(),
                () -> assertThat(result.getTotalElements()).isEqualTo(totalCount),
                () -> assertThat(result.getTotalPages()).isEqualTo(2),
                () -> assertThat(result.getPageNumber()).isEqualTo(2),
                () -> assertThat(result.getHasNext()).isFalse(),
                () -> assertThat(result.getHasPrevious()).isTrue()
        );
    }

    @Test
    @DisplayName("todo 댓글 목록 페이징 조회 실패 테스트: 존재하지 않는 할일")
    void getComments_TodoNotFound() {
        Long todoId = 1L;
        int page = 0;
        int size = 10;

        doThrow(new CustomApiException(ErrorCode.TODO_NOT_FOUND)).when(todoService).findByIdOrFail(todoId);

        // when
        assertThatThrownBy(() ->
                commentService.getCommentList(todoId, page, size))
                .isInstanceOf(CustomApiException.class)
                .hasMessage(ErrorCode.TODO_NOT_FOUND.getMessage());

        verify(todoService).findByIdOrFail(todoId);
        verify(commentRepository, never()).getCommentsWithUserByTodoId(anyLong(), any(Pageable.class));
    }



}
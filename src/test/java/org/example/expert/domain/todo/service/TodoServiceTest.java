package org.example.expert.domain.todo.service;

import org.example.expert.controller.todo.dto.request.TodoSearchReqDto;
import org.example.expert.controller.todo.dto.response.TodoSearchRespDto;
import org.example.expert.domain.todo.TodoRepository;
import org.example.expert.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @InjectMocks
    private TodoService todoService;

    @Mock
    private TodoRepository todoRepository;

    @Nested
    @DisplayName("searchTodos 메서드는")
    class SearchTodosTest {

        private TodoSearchReqDto searchReqDto;
        private List<TodoRespDto> todoList;
        private Page<TodoRespDto> todoPage;

        @BeforeEach
        void setUp() {
            searchReqDto = new TodoSearchReqDto();
            searchReqDto.setKeyword("테스트");
            searchReqDto.setStartCreatedAt("2024-01-01");
            searchReqDto.setEndCreatedAt("2024-01-31");
            searchReqDto.setPage(0);
            searchReqDto.setSize(10);

            List<TodoRespDto.ManagerRespDto> managerList = List.of(
                    new TodoRespDto.ManagerRespDto("담당자1", 1L),
                    new TodoRespDto.ManagerRespDto("담당자2", 2L)
            );

            todoList = List.of(
                    new TodoRespDto(
                            "테스트 할일",
                            LocalDateTime.parse("2024-01-15 10:00:00",
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            5L,
                            2L,
                            managerList
                    )
            );

            todoPage = new PageImpl<>(todoList, PageRequest.of(0, 10), 1);
        }

        @Test
        @DisplayName("검색 조건에 맞는 할일 목록을 반환한다")
        void returnsFilteredTodoList() {
            // given
            when(todoRepository.searchTodosByFilter(any(TodoSearchReqDto.class), any(Pageable.class)))
                    .thenReturn(todoPage);

            // when
            TodoSearchRespDto result = todoService.searchTodos(searchReqDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTodoList()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getTotalPages()).isEqualTo(1);

            TodoRespDto firstTodo = result.getTodoList().get(0);
            assertThat(firstTodo.getTitle()).isEqualTo("테스트 할일");
            assertThat(firstTodo.getTotalCommentsCount()).isEqualTo(5L);
            assertThat(firstTodo.getTotalManagersCount()).isEqualTo(2L);
            assertThat(firstTodo.getManagerList()).hasSize(2);

            TodoRespDto.ManagerRespDto firstManager = firstTodo.getManagerList().get(0);
            assertThat(firstManager.getManagerNickname()).isEqualTo("담당자1");
            assertThat(firstManager.getUserId()).isEqualTo(1L);

            verify(todoRepository).searchTodosByFilter(any(TodoSearchReqDto.class), any(Pageable.class));
        }

        @Test
        @DisplayName("검색 결과가 없을 경우 빈 목록을 반환한다")
        void returnsEmptyListWhenNoResults() {
            // given
            Page<TodoRespDto> emptyPage = new PageImpl<>(
                    List.of(),
                    PageRequest.of(0, 10),
                    0
            );
            when(todoRepository.searchTodosByFilter(any(TodoSearchReqDto.class), any(Pageable.class)))
                    .thenReturn(emptyPage);

            // when
            TodoSearchRespDto result = todoService.searchTodos(searchReqDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTodoList()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getTotalPages()).isZero();
            assertThat(result.isHasNext()).isFalse();
            assertThat(result.isHasPrevious()).isFalse();

            verify(todoRepository).searchTodosByFilter(any(TodoSearchReqDto.class), any(Pageable.class));
        }

        @Test
        @DisplayName("날짜 범위 검색이 정상적으로 처리된다")
        void handleDateRangeSearchCorrectly() {
            // given
            TodoSearchReqDto dateRangeReqDto = new TodoSearchReqDto();
            dateRangeReqDto.setStartCreatedAt("2024-01-01");
            dateRangeReqDto.setEndCreatedAt("2024-01-31");
            dateRangeReqDto.setPage(0);
            dateRangeReqDto.setSize(10);

            when(todoRepository.searchTodosByFilter(any(TodoSearchReqDto.class), any(Pageable.class)))
                    .thenReturn(todoPage);

            // when
            TodoSearchRespDto result = todoService.searchTodos(dateRangeReqDto);

            // then
            assertThat(result).isNotNull();
            verify(todoRepository).searchTodosByFilter(any(TodoSearchReqDto.class), any(Pageable.class));


            LocalDateTime startDateTime = dateRangeReqDto.getStartDateTime();
            LocalDateTime endDateTime = dateRangeReqDto.getEndDateTime();

            assertThat(startDateTime).isEqualTo(
                    LocalDateTime.parse("2024-01-01 00:00:00",
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
            assertThat(endDateTime).isEqualTo(
                    LocalDateTime.parse("2024-01-31 23:59:59",
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
        }

        @Test
        @DisplayName("페이지네이션이 정상적으로 처리된다")
        void handlesPaginationCorrectly() {
            TodoSearchReqDto paginationReqDto = new TodoSearchReqDto();
            paginationReqDto.setPage(1);
            paginationReqDto.setSize(5);

            List<TodoRespDto> todoList = List.of(
                    new TodoRespDto("할일 1", LocalDateTime.now(), 1L, 1L, List.of()),
                    new TodoRespDto("할일 2", LocalDateTime.now(), 2L, 2L, List.of())
            );

            Page<TodoRespDto> pageWithMultipleItems =
                    new PageImpl<>(todoList, PageRequest.of(1, 5), 12);

            when(todoRepository.searchTodosByFilter(any(TodoSearchReqDto.class), any(Pageable.class)))
                    .thenReturn(pageWithMultipleItems);

            // when
            TodoSearchRespDto result = todoService.searchTodos(paginationReqDto);

            // then
            assertThat(result.getPageNumber()).isEqualTo(1);
            assertThat(result.getTotalPages()).isEqualTo(3); // 12개 항목, 페이지당 5개 → 3페이지
            assertThat(result.getTotalElements()).isEqualTo(12);
            assertThat(result.isHasNext()).isTrue();
            assertThat(result.isHasPrevious()).isTrue();
        }
    }

}
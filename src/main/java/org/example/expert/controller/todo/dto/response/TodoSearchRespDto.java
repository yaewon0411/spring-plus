package org.example.expert.controller.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class TodoSearchRespDto {
    //일정 제목, 해당 일정의 담당자 수, 해당 일정의 총 댓글 개수
    private List<TodoRespDto> todoList;

    private long totalElements;
    private int totalPages;
    private int pageNumber;
    private boolean hasNext;
    private boolean hasPrevious;

    public TodoSearchRespDto(Page<TodoRespDto>todoPage) {
        this.todoList = todoPage.getContent();
        this.totalElements = todoPage.getTotalElements();
        this.totalPages = todoPage.getTotalPages();
        this.pageNumber = todoPage.getNumber();
        this.hasNext = todoPage.hasNext();
        this.hasPrevious = todoPage.hasPrevious();
    }

    @NoArgsConstructor
    @Getter
    public static class TodoRespDto{

        private String title;
        private LocalDateTime createdAt;
        private Long totalCommentsCount;
        private Long totalManagersCount;
        private List<ManagerRespDto> managerList;

        public TodoRespDto(String title, LocalDateTime createdAt, Long totalCommentsCount, Long totalManagersCount, List<ManagerRespDto> managerList) {
            this.title = title;
            this.createdAt = createdAt;
            this.totalCommentsCount = totalCommentsCount;
            this.totalManagersCount = totalManagersCount;
            this.managerList = managerList;
        }

        @NoArgsConstructor
        @Getter
        public static class ManagerRespDto{
            private String managerNickname;
            private Long userId;

            public ManagerRespDto(String managerNickname, Long userId) {
                this.managerNickname = managerNickname;
                this.userId = userId;
            }
        }
    }
}

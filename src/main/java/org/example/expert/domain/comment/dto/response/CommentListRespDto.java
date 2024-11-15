package org.example.expert.domain.comment.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

@NoArgsConstructor
@Getter
public class CommentListRespDto {


    private List<CommentResponse> commentList;
    private long totalElements;
    private int totalPages;
    private int pageNumber;
    private boolean hasNext;
    private boolean hasPrevious;

    public CommentListRespDto(Page<CommentResponse> commentPage) {
        this.commentList = commentPage.getContent();
        this.totalElements = commentPage.getTotalElements();
        this.totalPages = commentPage.getTotalPages();
        this.pageNumber = commentPage.getNumber();
        this.hasNext = commentPage.hasNext();
        this.hasPrevious = commentPage.hasPrevious();
    }
}

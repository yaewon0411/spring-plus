package org.example.expert.controller.comment.dto.response

import org.springframework.data.domain.Page

data class CommentListRespDto(
    val commentList: List<CommentInfoRespDto>,
    val totalElements: Long,
    val totalPages: Int,
    val pageNumber: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
) {
    constructor(commentPage: Page<CommentInfoRespDto>): this(
        commentList = commentPage.content,
        totalElements = commentPage.totalElements,
        totalPages = commentPage.totalPages,
        pageNumber = commentPage.number,
        hasNext = commentPage.hasNext(),
        hasPrevious = commentPage.hasPrevious()
    )
}
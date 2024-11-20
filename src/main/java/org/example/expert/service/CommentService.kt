package org.example.expert.service

import org.example.expert.controller.comment.dto.request.CommentCreateReqDto
import org.example.expert.controller.comment.dto.response.CommentCreateRespDto
import org.example.expert.controller.comment.dto.response.CommentListRespDto
import org.example.expert.domain.comment.CommentRepository
import org.example.expert.domain.user.User
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CommentService(
    private val commentRepository: CommentRepository,
    private val todoService: TodoService
) {

    @Transactional
    fun createComment(user: User, todoId: Long, commentCreateReqDto: CommentCreateReqDto)
    : CommentCreateRespDto = todoService.findByIdOrFail(todoId)
        .let { todo -> commentRepository.save(commentCreateReqDto.toEntity(user, todo)) }
        .let { comment -> CommentCreateRespDto(user, comment) }


    fun getCommentList(todoId: Long, page: Int, size: Int)
    : CommentListRespDto{
        todoService.findByIdOrFail(todoId)
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "modifiedAt"))
            .let { pageable -> commentRepository.getCommentsWithUserByTodoId(todoId, pageable) }
            .let { commentPage -> CommentListRespDto(commentPage) }
    }
}
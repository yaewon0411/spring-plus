package org.example.expert.controller.comment

import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.apache.coyote.Response
import org.example.expert.config.security.loginuser.LoginUser
import org.example.expert.controller.comment.dto.request.CommentCreateReqDto
import org.example.expert.controller.comment.dto.response.CommentCreateRespDto
import org.example.expert.controller.comment.dto.response.CommentListRespDto
import org.example.expert.service.CommentService
import org.example.expert.util.api.ApiResult
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
@RequestMapping("/todos")
class CommentController(
    private val commentService: CommentService
) {

    @PostMapping("/{todoId}/comments")
    fun createComment(@PathVariable(value = "todoId") todoId: Long,
                      @Valid @RequestBody commentCreateReqDto: CommentCreateReqDto,
                      @AuthenticationPrincipal loginUser: LoginUser)
    : ResponseEntity<ApiResult<CommentCreateRespDto>>
    = ResponseEntity.ok(ApiResult.success(commentService.createComment(loginUser.user, todoId, commentCreateReqDto)))

    @GetMapping("/{todoId}/comments")
    fun getCommentList(@PathVariable(value = "todoId") todoId: Long,
                       @RequestParam(value = "page", defaultValue = "0", required = false) @PositiveOrZero page: Int,
                       @RequestParam(value = "size", defaultValue = "10", required = false) @Positive size: Int)
    : ResponseEntity<ApiResult<CommentListRespDto>>
    = ResponseEntity.ok(ApiResult.success(commentService.getCommentList(todoId, page, size)))

}
package org.example.expert.domain.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.example.expert.config.security.loginuser.LoginUser;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentListRespDto;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/todos/{todoId}/comments")
    public ResponseEntity<CommentSaveResponse> saveComment(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable long todoId,
            @Valid @RequestBody CommentSaveRequest commentSaveRequest
    ) {
        return ResponseEntity.ok(commentService.saveComment(loginUser.getUser(), todoId, commentSaveRequest));
    }

    @GetMapping("/todos/{todoId}/comments")
    public ResponseEntity<CommentListRespDto> getComments(@PathVariable(value = "todoId") Long todoId,
                                                          @RequestParam(value = "page", defaultValue = "0", required = false) @PositiveOrZero Integer page,
                                                          @RequestParam(value = "size", defaultValue = "10", required = false) @Positive Integer size) {
        return ResponseEntity.ok(commentService.getComments(todoId, page, size));
    }
}

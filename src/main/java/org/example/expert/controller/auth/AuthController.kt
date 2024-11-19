package org.example.expert.controller.auth

import jakarta.validation.Valid
import org.example.expert.controller.auth.dto.request.JoinReqDto
import org.example.expert.controller.auth.dto.response.JoinRespDto
import org.example.expert.service.AuthService
import org.example.expert.util.api.ApiResult
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/join")
    fun join(@Valid @RequestBody joinReqDto: JoinReqDto): ResponseEntity<ApiResult<JoinRespDto>> =
        ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.success(authService.join(joinReqDto)))

}
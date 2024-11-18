package org.example.expert.controller.user

import jakarta.validation.Valid
import org.example.expert.config.security.loginuser.LoginUser
import org.example.expert.controller.user.dto.request.PasswordChangeReqDto
import org.example.expert.controller.user.dto.response.UserInfoRespDto
import org.example.expert.service.UserService
import org.example.expert.util.api.ApiResult
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping
    fun getUserInfo(@AuthenticationPrincipal loginUser: LoginUser): ResponseEntity<ApiResult<UserInfoRespDto>> =
        ResponseEntity.ok(ApiResult.success(userService.getUserInfo(loginUser.userId)))

    @PutMapping("/password")
    fun changePassword(@Valid @RequestBody passwordChangeReqDto: PasswordChangeReqDto,
                       @AuthenticationPrincipal loginUser: LoginUser) : ResponseEntity<ApiResult<String>> {
        userService.changePassword(loginUser.userId, passwordChangeReqDto)
        return ResponseEntity.ok(ApiResult.success("비밀번호가 성공적으로 변경되었습니다"))
    }

}
package org.example.expert.controller.user

import jakarta.validation.Valid
import org.example.expert.config.security.loginuser.LoginUser
import org.example.expert.controller.user.dto.request.UserRoleChangeReqDto
import org.example.expert.service.UserService
import org.example.expert.util.api.ApiResult
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminUserController(
    private val userService: UserService
) {
    @PatchMapping("/users")
    fun changeUserRole(@Valid @RequestBody userRoleChangeReqDto: UserRoleChangeReqDto,
                       @AuthenticationPrincipal loginUser: LoginUser): ResponseEntity<ApiResult<String>>{
        userService.changeUserRole(loginUser.userId, userRoleChangeReqDto)
        return ResponseEntity.ok(ApiResult.success("사용자 권한이 성공적으로 변경되었습니다"))
    }
}
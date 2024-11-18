package org.example.expert.controller.user.dto.request

import jakarta.validation.constraints.NotBlank
import org.example.expert.controller.auth.dto.request.valid.userRole.ValidUserRole

data class UserRoleChangeReqDto(
    @field: NotBlank(message = "userRole을 입력해주세요")
    @ValidUserRole
    val userRole: String = ""
) {
}
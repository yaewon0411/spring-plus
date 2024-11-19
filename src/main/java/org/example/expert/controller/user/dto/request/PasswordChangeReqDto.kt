package org.example.expert.controller.user.dto.request

import jakarta.validation.constraints.NotBlank
import org.example.expert.controller.auth.dto.request.valid.password.ValidPassword
import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode

data class PasswordChangeReqDto(
    @field: NotBlank(message = "기존 비밀번호를 입력해주세요")
    @ValidPassword
    val oldPassword: String = "",

    @field: NotBlank(message = "새 비밀번호를 입력해주세요")
    @ValidPassword
    val newPassword: String = ""
) {
    fun validatePasswordChange(){
        require(oldPassword != newPassword){
            throw CustomApiException(ErrorCode.SAME_PASSWORD)
        }
    }
}
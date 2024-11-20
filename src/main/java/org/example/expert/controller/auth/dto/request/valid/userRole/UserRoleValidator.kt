package org.example.expert.controller.auth.dto.request.valid.userRole

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.example.expert.domain.user.UserRole
import org.example.expert.exception.CustomApiException

class UserRoleValidator: ConstraintValidator<ValidUserRole, String> {

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if(value == null){
            return true //notblank에서 처리하도록
        }

        return try{
            UserRole.of(value)
            true
        }catch (e: CustomApiException){
            false
        }
    }
}

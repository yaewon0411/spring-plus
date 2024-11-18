package org.example.expert.domain.auth.dto.request.valid.userRole

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.example.expert.domain.user.enums.UserRole
import org.example.expert.exception.InvalidRequestException

class UserRoleValidator: ConstraintValidator<ValidUserRole, String> {

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if(value == null){
            return true //notblank에서 처리하도록
        }

        return try{
            UserRole.of(value)
            true
        }catch (e: InvalidRequestException){
            false
        }
    }
}

package org.example.expert.domain.auth.dto.request.valid.password

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PasswordValidator: ConstraintValidator<ValidPassword, String> {

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if(value == null){
            return true
        }

        return value.length >= 8 &&
                value.any{it.isDigit()} &&
                value.any { it.isUpperCase() } &&
                value.any { it.isLowerCase() }
    }
}
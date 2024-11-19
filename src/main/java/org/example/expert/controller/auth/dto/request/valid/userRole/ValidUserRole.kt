package org.example.expert.controller.auth.dto.request.valid.userRole

import jakarta.validation.Constraint
import jakarta.validation.Payload
import org.example.expert.domain.user.UserRole
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UserRoleValidator::class])
annotation class ValidUserRole(
    val enumClass: KClass<out Enum<*>> = UserRole::class,
    val message: String = "유효한 UserRole 형식이 아닙니다",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
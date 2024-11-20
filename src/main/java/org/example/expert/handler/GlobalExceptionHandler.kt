package org.example.expert.handler

import jakarta.validation.ConstraintViolationException
import org.apache.coyote.Response
import org.example.expert.exception.CustomApiException
import org.example.expert.util.api.ApiError
import org.example.expert.util.api.ApiResult
import org.example.expert.util.api.ApiResult.Companion.error
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.function.Consumer

@RestControllerAdvice
class GlobalExceptionHandler {

    companion object{
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException): ResponseEntity<ApiResult<Map<String, String>>>{
        val errorMap = e.constraintViolations
            .associate { violation ->
                val propertyPath = violation.propertyPath.toString()
                val fieldName = propertyPath.substringAfterLast('.')
                fieldName to (violation.message?: "알 수 없는 오류")
            }
        return ResponseEntity
            .badRequest()
            .body(error(HttpStatus.BAD_REQUEST.value(), "요청 파라미터 검증 실패", errorMap))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ApiResult<Map<String, String>>> {
        val errorMap = e.bindingResult.fieldErrors
            .associate { error -> error.field to (error.defaultMessage ?: "알 수 없는 오류") }
        return ResponseEntity
            .badRequest()
            .body(error(HttpStatus.BAD_REQUEST.value(), "유효성 검증 실패", errorMap))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(e: Exception): ResponseEntity<ApiResult<String>>{
        log.error("예기치 못한 내부 오류 발생: {}", e.message, e)
        return ResponseEntity
            .internalServerError()
            .body(error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요",
                e.message ?: "알 수 없는 내부 오류"))
    }

    @ExceptionHandler(CustomApiException::class)
    fun handleCustomApiException(e: CustomApiException): ResponseEntity<ApiResult<ApiError>>
    = ResponseEntity
        .status(e.errorCode.status)
        .body(error(e.errorCode.status, e.message ?: "알 수 없는 오류"))


}
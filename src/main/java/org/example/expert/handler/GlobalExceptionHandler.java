//package org.example.expert.handler;
//
//import jakarta.validation.ConstraintViolationException;
//import lombok.extern.slf4j.Slf4j;
//import org.example.expert.exception.CustomApiException;
//import org.example.expert.exception.InvalidRequestException;
//import org.example.expert.exception.ServerException;
//import org.example.expert.util.api.ApiError;
//import org.example.expert.util.api.ApiResult;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestControllerAdvice
//@Slf4j
//public class GlobalExceptionHandler {
//
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResult<Map<String, String>>> validationException(MethodArgumentNotValidException e) {
//        Map<String, String> errorMap = new HashMap<>();
//        e.getBindingResult().getFieldErrors().forEach(error ->
//                errorMap.put(error.getField(), error.getDefaultMessage())
//        );
//        return new ResponseEntity<>(ApiResult.Companion.error(HttpStatus.BAD_REQUEST.value(),"유효성 검사 실패", errorMap), HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception e){
//        log.error("예기치 못한 내부 오류 발생: {}", e.getMessage(), e);
//        return getErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
//    }
//
//    @ExceptionHandler(InvalidRequestException.class)
//    public ResponseEntity<ApiResult<ApiError>> invalidRequestExceptionException(InvalidRequestException ex) {
//        return new ResponseEntity<>(ApiResult.Companion.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(CustomApiException.class)
//    public ResponseEntity<ApiResult<ApiError>> handleCustomApiException(CustomApiException e){
//        return new ResponseEntity<>(ApiResult.Companion.error(e.getErrorCode().getStatus(), e.getMessage()), HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(ServerException.class)
//    public ResponseEntity<Map<String, Object>> handleServerException(ServerException ex) {
//        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
//        return getErrorResponse(status, ex.getMsg());
//    }
//
//    public ResponseEntity<Map<String, Object>> getErrorResponse(HttpStatus status, Object message) {
//        Map<String, Object> errorResponse = new HashMap<>();
//        errorResponse.put("status", status.name());
//        errorResponse.put("code", status.value());
//        errorResponse.put("message", message);
//
//        return new ResponseEntity<>(errorResponse, status);
//    }
//
//}
//

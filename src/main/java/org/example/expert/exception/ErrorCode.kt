package org.example.expert.exception

import org.example.expert.domain.log.manager.ManagerLogMessage
import org.springframework.http.HttpStatus

enum class ErrorCode(
    val message: String,
    val status: Int
) {
    INVALID_USER_STATE("사용자 정보가 유효하지 않습니다", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    ALREADY_EXISTS_EMAIL("사용 중인 이메일입니다", HttpStatus.BAD_REQUEST.value()),
    USER_NOT_FOUND("존재하지 않는 사용자입니다", HttpStatus.NOT_FOUND.value()),
    SAME_PASSWORD("새 비밀번호는 기존 비밀번호와 달라야 합니다", HttpStatus.BAD_REQUEST.value()),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST.value()),
    INVALID_AUTHENTICATION_STATE("인증된 사용자 정보가 유효하지 않습니다", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    INVALID_USER_ROLE("유효하지 않은 USER_ROLE 입니다", HttpStatus.BAD_REQUEST.value()),
    INVALID_TODO_STATE("일정 정보가 유효하지 않습니다", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    TODO_NOT_FOUND("존재하지 않는 일정입니다", HttpStatus.NOT_FOUND.value()),
    INVALID_MANAGER_STATE("담당자 정보가 유효하지 않습니다", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    TARGET_USER_NOT_FOUND("등록하려는 사용자가 존재하지 않습니다", HttpStatus.NOT_FOUND.value()),
    ALREADY_ASSIGNED_USER("이미 해당 일정에 배정된 사용자입니다", HttpStatus.BAD_REQUEST.value()),
    MANAGER_NOT_FOUND("존재하지 않는 일정 담당자입니다", HttpStatus.NOT_FOUND.value()),
    INVALID_COMMENT_STATE("댓글 정보가 유효하지 않습니다", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    MANAGER_NOT_IN_TODO("해당 일정에 등록된 담당자가 아닙니다", HttpStatus.BAD_REQUEST.value()),
    FORBIDDEN_TODO_ACCESS("해당 일정에 권한이 없습니다", HttpStatus.FORBIDDEN.value()),
    AUTHOR_CANNOT_BE_MANAGER("일정 작정자는 본인을 담당자로 등록할 수 없습니다", HttpStatus.BAD_REQUEST.value()),
    FAIL_TO_GET_WEATHER("날씨 데이터를 가져오는데 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    WEATHER_NOT_FOUND("날씨 데이터가 존재하지 않습니다", HttpStatus.INTERNAL_SERVER_ERROR.value()),



}
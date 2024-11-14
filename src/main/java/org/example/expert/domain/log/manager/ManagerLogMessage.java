package org.example.expert.domain.log.manager;

import lombok.Getter;
import org.example.expert.aop.dto.ManagerLogContext;

@Getter
public enum ManagerLogMessage {

    ASSIGNED_SUCCESS("담당자 배정 완료: (todoId: %d, targetUserId: %d)"),
    ASSIGNED_FAIL("담당자 배정 실패: %s (todoId: %d, targetUserId: %d)"),
    INTERNAL_FAIL("담당자 배정 중 오류 발생: %s (todoId: %d, targetUserId: %d)"),

    //로그 저장
    LOG_SAVE_FAIL_WITH_SUCCESS("로그 저장 실패. 담당자 배정은 정상 처리 완료. 실패 이유: {}"),
    LOG_SAVE_FAIL_WITH_ERROR("로그 저장 실패. 원본 에러: {}. 로그 저장 실패 이유: {}"),

    //ManagerLogContext 검증
    INVALIDATE_ARGS_COUNT("담당자 배정 로그를 남기기 위한 인수 개수가 부적절합니다. 필요: %d, 실제: %d"),
    INVALIDATE_USER_TYPE("첫 번째 인수는 User 타입이어야 합니다 (들어온 타입: %s)"),
    INVALIDATE_TODO_ID_TYPE("두 번째 인수는 Long 타입이어야 합니다 (들어온 타입: %s)"),
    INVALIDATE_REQUEST_TYPE("세 번째 인수는 ManagerSaveRequest 타입이어야 합니다 (들어온 타입: %s)");

    private final String format;

    ManagerLogMessage(String format) {
        this.format = format;
    }

    public String format(Object... args) {
        return String.format(format, args);
    }

}

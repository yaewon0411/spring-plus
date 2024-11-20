package org.example.expert.domain.log.manager

enum class ManagerLogMessage(
    val format: String
) {

    ASSIGNED_SUCCESS("담당자 배정 완료: (todoId: %d, targetUserId: %d)"),
    ASSIGNED_FAIL("담당자 배정 실패: %s (todoId: %d, targetUserId: %d)"),
    INTERNAL_FAIL("담당자 배정 중 오류 발생: %s (todoId: %d, targetUserId: %d)"),

    //로그 저장
    LOG_SAVE_FAIL_WITH_SUCCESS("로그 저장 실패. 담당자 배정은 정상 처리 완료. 실패 이유: %s"),
    LOG_SAVE_FAIL_WITH_ERROR("로그 저장 실패. 원본 에러: %s. 로그 저장 실패 이유: %s"),

    //ManagerLogContext 검증
    INVALIDATE_ARGS_COUNT("담당자 배정 로그를 남기기 위한 인수 개수가 부적절합니다. 필요: %d, 실제: %d"),
    INVALIDATE_ARGS_TYPE("%d 번째 인수는 %s 타입이어야 합니다 (들어온 타입: %s)");


    fun format(vararg args: Any): String {
        return String.format(format, *args)
    }

}
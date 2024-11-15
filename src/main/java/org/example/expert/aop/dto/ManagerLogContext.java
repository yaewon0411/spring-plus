package org.example.expert.aop.dto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.exception.ServerException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.log.manager.ManagerLogMessage;
import org.example.expert.domain.user.entity.User;


@Getter
@Slf4j
public class ManagerLogContext {
    public static final int ARGS_COUNT = 3;
    private final User user;
    private final Long todoId;
    private final ManagerSaveRequest request;

    private ManagerLogContext(User user, Long todoId, ManagerSaveRequest request) {
        this.user = user;
        this.todoId = todoId;
        this.request = request;
    }

    public static ManagerLogContext validateAndCreate(Object[] args){
        validateArgs(args);
        return new ManagerLogContext(
                (User) args[0],
                (Long) args[1],
                (ManagerSaveRequest) args[2]
        );
    }

    private static void validateArgs(Object[] args) {
        validateArgsCount(args);
        validateArgsType(args);
    }

    private static void validateArgsCount(Object[] args) {
        if (args.length != ARGS_COUNT) {
            String errorMsg = ManagerLogMessage.INVALIDATE_ARGS_COUNT.format(ARGS_COUNT, args.length);
            log.error("ManagerLogContext 생성 실패: {}", errorMsg);
            throw new ServerException(errorMsg);
        }
    }

    private static void validateArgsType(Object[] args) {
        if (!(args[0] instanceof User)) {
            String errorMsg = ManagerLogMessage.INVALIDATE_USER_TYPE.format(args[0]!= null? args[0].getClass().getName(): "null");
            log.error("ManagerLogContext 생성 실패: {}", errorMsg);
            throw new ServerException(errorMsg);
        }
        if (!(args[1] instanceof Long)) {
            String errorMsg = ManagerLogMessage.INVALIDATE_TODO_ID_TYPE.format(args[1] != null ? args[1].getClass().getName() : "null");
            log.error("ManagerLogContext 생성 실패: {}", errorMsg);
            throw new ServerException(errorMsg);
        }
        if (!(args[2] instanceof ManagerSaveRequest)) {
            String errorMsg = ManagerLogMessage.INVALIDATE_REQUEST_TYPE.format(args[2] != null ? args[2].getClass().getName() : "null");
            log.error("ManagerLogContext 생성 실패: {}", errorMsg);
            throw new ServerException(errorMsg);
        }
    }
}

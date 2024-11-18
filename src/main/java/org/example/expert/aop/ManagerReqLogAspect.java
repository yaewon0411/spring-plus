package org.example.expert.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.expert.aop.dto.ManagerLogContext;
import org.example.expert.exception.InvalidRequestException;
import org.example.expert.domain.log.manager.ManagerLogMessage;
import org.example.expert.domain.log.manager.ManagerReqStatus;
import org.example.expert.service.ManagerReqLogService;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ManagerReqLogAspect {
    private static final String MANAGER_SAVE_POINTCUT =
            "execution(* org.example.expert.domain.manager.service.ManagerService.saveManager(..))";

    private final ManagerReqLogService logService;

    @Around(MANAGER_SAVE_POINTCUT)
    public Object logManagerAssignment(ProceedingJoinPoint joinPoint) throws Throwable{
        ManagerLogContext managerLogContext = ManagerLogContext.validateAndCreate(joinPoint.getArgs());
        try{
            Object result = joinPoint.proceed();
            String message = ManagerLogMessage.ASSIGNED_SUCCESS.format(managerLogContext.getRequest().getTargetUserId(), managerLogContext.getTodoId());
            saveLog(managerLogContext, message, ManagerReqStatus.SUCCESS, null);
            return result;
        }catch (InvalidRequestException e) {
            String message = ManagerLogMessage.ASSIGNED_FAIL.format(e.getMessage(), managerLogContext.getTodoId(), managerLogContext.getRequest().getTargetUserId());
            saveLog(managerLogContext, message, ManagerReqStatus.FAIL, e);
            throw e;
        }catch(Exception e){
            String message = ManagerLogMessage.INTERNAL_FAIL.format(e.getMessage(), managerLogContext.getTodoId(), managerLogContext.getRequest().getTargetUserId());
            saveLog(managerLogContext, message, ManagerReqStatus.FAIL, e);
            throw e;
        }
    }

    private void saveLog (ManagerLogContext context, String message, ManagerReqStatus status, Exception originalException) {
        try {
            logService.saveLog(context.getRequest(), status, context.getUser(), context.getTodoId(), message);
        } catch (Exception logError) {
            if (originalException == null) {
                //비즈니스는 성공적으로 수행됐으나 로그 저장에 실패한 경우
                log.warn(ManagerLogMessage.LOG_SAVE_FAIL_WITH_ERROR.getFormat(), logError.getMessage(), logError);
            } else {
                log.warn(ManagerLogMessage.LOG_SAVE_FAIL_WITH_ERROR.getFormat(), originalException.getMessage(), logError.getMessage(), logError);
            }
        }
    }
}

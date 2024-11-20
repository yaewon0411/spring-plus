package org.example.expert.aop

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.example.expert.aop.dto.ManagerLogContext
import org.example.expert.domain.log.manager.ManagerLogMessage
import org.example.expert.domain.log.manager.ManagerReqStatus
import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode
import org.example.expert.service.ManagerReqLogService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class ManagerReqLogAspect(
    private val logService: ManagerReqLogService
) {
    companion object{
        private val log = LoggerFactory.getLogger(this::class.java)
        private const val MANAGER_LOG_POINTCUT: String =
            "execution(* org.example.expert.service.ManagerService.saveManager(..))"
    }
    @Around(MANAGER_LOG_POINTCUT)
    fun logManagerAssignment(joinPoint: ProceedingJoinPoint): Any {
        val managerLogContext = ManagerLogContext.validateAndCreate(joinPoint.args)
        try {
            val result = joinPoint.proceed()
            val message = ManagerLogMessage.ASSIGNED_SUCCESS.format(
                managerLogContext.todoId,
                managerLogContext.request.targetUserId
            )
            saveLog(managerLogContext, message, ManagerReqStatus.SUCCESS, null)
            return result
        } catch (e: CustomApiException){
            val message =
                ManagerLogMessage.ASSIGNED_FAIL.format(
                    e.errorCode.message,
                    managerLogContext.todoId,
                    managerLogContext.request.targetUserId)
            saveLog(managerLogContext, message, ManagerReqStatus.FAIL, e)
            throw e
        } catch (e: Exception){
            val message = ManagerLogMessage.INTERNAL_FAIL.format(
                e.message?: "알 수 없는 오류",
                managerLogContext.todoId,
                managerLogContext.request.targetUserId
            )
            saveLog(managerLogContext, message, ManagerReqStatus.FAIL, e)
            throw e
        }
    }

    private fun saveLog(context: ManagerLogContext,
                        message: String,
                        status: ManagerReqStatus,
                        originalException: Exception?){
        try{
            logService.saveLog(
                managerCreateReqDto = context.request,
                status = status,
                user = context.user,
                todoId = context.todoId,
                message = message
            )
        }catch (saveError: Exception){
            if(originalException == null){
                log.warn(
                    ManagerLogMessage.LOG_SAVE_FAIL_WITH_SUCCESS.format(
                        saveError.message?: "알 수 없는 오류"
                    ),
                    saveError)
            } else{
                log.warn(
                    ManagerLogMessage.LOG_SAVE_FAIL_WITH_ERROR.format(
                        originalException.message?: "알 수 없는 오류",
                        saveError.message?: "알 수 없는 오류"
                    ),
                    saveError)
            }
        }
    }
}
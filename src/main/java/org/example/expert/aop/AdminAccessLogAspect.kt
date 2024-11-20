package org.example.expert.aop

import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.example.expert.aop.dto.AdminAccessLogInfo
import org.example.expert.config.security.loginuser.LoginUser
import org.example.expert.domain.log.admin.AdminLogMessage
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Aspect
@Component
class AdminAccessLogAspect(
    private val request: HttpServletRequest
) {
    companion object{
        private val log = LoggerFactory.getLogger(this::class.java)
        private const val ADMIN_LOG_POINTCUT =
            "execution(* org.example.expert.controller.user.AdminUserController.changeUserRole(..))"
    }
    @Before(ADMIN_LOG_POINTCUT)
    fun logBeforeChangeUserRole(joinPoint: JoinPoint) {
        val loginUser = SecurityContextHolder.getContext().authentication.principal as LoginUser
        AdminAccessLogInfo.from(loginUser, request, joinPoint)
            .also { logInfo ->log.info(AdminLogMessage.ACCESS_LOG.format(logInfo))  }
    }
}


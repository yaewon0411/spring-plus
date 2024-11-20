package org.example.expert.aop.dto

import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.JoinPoint
import org.example.expert.config.security.loginuser.LoginUser
import org.example.expert.exception.ErrorCode
import java.time.LocalDateTime

data class AdminAccessLogInfo(
    val userId: String,
    val requestTime: LocalDateTime,
    val requestUrl: String,
    val handlerMethod: String,
    val httpMethod: String
){
    companion object{
        fun from(loginUser: LoginUser, request: HttpServletRequest, joinPoint: JoinPoint) = AdminAccessLogInfo(
            userId = loginUser.user.id?.toString() ?:ErrorCode.INVALID_USER_STATE.message,
            requestTime = LocalDateTime.now(),
            requestUrl = request.requestURI,
            handlerMethod = joinPoint.signature.name,
            httpMethod = request.method
        )
    }
}

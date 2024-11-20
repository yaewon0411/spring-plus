package org.example.expert.domain.log.admin

import org.example.expert.aop.dto.AdminAccessLogInfo

enum class AdminLogMessage(
    val format: String
) {
    ACCESS_LOG("Admin Access Log - User ID: %s, Request Time: %s, Request URL: %s, Handler Method: %s, Http Method: %s");

    fun format(logInfo: AdminAccessLogInfo): String{
        return String.format(format, logInfo.userId, logInfo.requestTime, logInfo.requestUrl, logInfo.handlerMethod, logInfo.httpMethod )
    }
}
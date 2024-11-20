package org.example.expert.exception

import org.apache.catalina.Manager
import org.example.expert.domain.log.manager.ManagerLogMessage

sealed class LogContextValidationException(message: String): RuntimeException(message) {

    data class InvalidManagerContextArgsCount(
        val required: Int,
        val actual: Int
    ): LogContextValidationException(
        ManagerLogMessage.INVALIDATE_ARGS_COUNT.format(required, actual)
    )

    data class InvalidManagerContextArgumentType(
        val position: Int,
        val expectedType: String,
        val actualType: String
    ): LogContextValidationException(
        ManagerLogMessage.INVALIDATE_ARGS_TYPE.format(position, expectedType, actualType)
    )
}
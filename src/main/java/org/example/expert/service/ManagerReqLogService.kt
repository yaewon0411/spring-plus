package org.example.expert.service

import org.example.expert.controller.manager.dto.request.ManagerCreateReqDto
import org.example.expert.domain.log.ManagerReqLog
import org.example.expert.domain.log.manager.ManagerReqLogRepository
import org.example.expert.domain.log.manager.ManagerReqStatus
import org.example.expert.domain.user.User
import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ManagerReqLogService(
    private val logRepository: ManagerReqLogRepository
) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveLog(managerCreateReqDto: ManagerCreateReqDto,
                  status: ManagerReqStatus,
                  user: User,
                  todoId: Long, message: String) {
        val log = ManagerReqLog(
            requestUserId = user.id ?: throw CustomApiException(ErrorCode.INVALID_USER_STATE),
            targetUserId = managerCreateReqDto.targetUserId,
            todoId = todoId,
            status = status,
            message = message
        )
        logRepository.save(log)
    }
}
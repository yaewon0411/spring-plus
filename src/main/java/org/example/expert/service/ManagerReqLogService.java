package org.example.expert.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.log.ManagerReqLog;
import org.example.expert.controller.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.log.manager.ManagerReqStatus;
import org.example.expert.domain.log.manager.ManagerReqLogRepository;
import org.example.expert.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerReqLogService {

    private final ManagerReqLogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(ManagerSaveRequest managerSaveRequest, ManagerReqStatus status, User user, Long todoId, String message){
        ManagerReqLog log = new ManagerReqLog(
                user.getId(),
                managerSaveRequest.getTargetUserId(),
                todoId,
                status,
                message
        );
        logRepository.save(log);
    }
}

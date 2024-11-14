package org.example.expert.domain.log.manager.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.log.manager.ManagerReqLog;
import org.example.expert.domain.log.manager.ManagerReqStatus;
import org.example.expert.domain.log.manager.ManagerReqLogRepository;
import org.example.expert.domain.user.entity.User;
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
        ManagerReqLog log = ManagerReqLog.builder()
                .requestUserId(user.getId())
                .targetUserId(managerSaveRequest.getManagerUserId())
                .todoId(todoId)
                .status(status)
                .message(message)
                .build();
        logRepository.save(log);
    }
}

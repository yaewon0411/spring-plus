package org.example.expert.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.controller.manager.dto.request.ManagerCreateReqDto;
import org.example.expert.domain.log.ManagerReqLog;
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
    public void saveLog(ManagerCreateReqDto managerCreateReqDto, ManagerReqStatus status, User user, Long todoId, String message){
        ManagerReqLog log = new ManagerReqLog(
                user.getId(),
                managerCreateReqDto.getTargetUserId(),
                todoId,
                status,
                message
        );
        logRepository.save(log);
    }
}

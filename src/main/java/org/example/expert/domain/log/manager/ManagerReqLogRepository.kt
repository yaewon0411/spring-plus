package org.example.expert.domain.log.manager

import lombok.extern.java.Log
import org.example.expert.domain.log.ManagerReqLog
import org.springframework.data.jpa.repository.JpaRepository

interface ManagerReqLogRepository: JpaRepository<ManagerReqLog, Long> {
}
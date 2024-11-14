package org.example.expert.domain.log.manager;

import org.example.expert.domain.log.manager.ManagerReqLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerReqLogRepository extends JpaRepository<ManagerReqLog, Long> {
}

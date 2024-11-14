package org.example.expert.domain.log.manager;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.entity.Timestamped;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "log")
public class ManagerReqLog extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long requestUserId;  // 요청한 유저

    @Column(nullable = false)
    private Long targetUserId;   // 담당자로 지정하려는 유저

    @Column(nullable = false)
    private Long todoId;         // 할일

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ManagerReqStatus status;       // 요청 처리 상태 (성공/실패)

    @Column(nullable = false)
    private String message;      // 상세 메시지 (성공/실패 사유 등....)

    @Builder
    public ManagerReqLog(Long requestUserId, Long targetUserId, Long todoId, ManagerReqStatus status, String message) {
        this.requestUserId = requestUserId;
        this.targetUserId = targetUserId;
        this.todoId = todoId;
        this.status = status;
        this.message = message;
    }
}

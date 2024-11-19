package org.example.expert.domain.log

import jakarta.persistence.*
import org.example.expert.domain.base.BaseEntity
import org.example.expert.domain.log.manager.ManagerReqStatus

@Entity
@Table(name = "log")
class ManagerReqLog protected constructor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var requestUserId: Long,

    @Column(nullable = false)
    var targetUserId: Long,

    @Column(nullable = false)
    var todoId: Long,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: ManagerReqStatus,

    @Column(nullable = false)
    var message: String

): BaseEntity() {

    constructor(requestUserId: Long, targetUserId: Long, todoId: Long, status: ManagerReqStatus, message: String): this(
        id = null,
        requestUserId = requestUserId,
        targetUserId = targetUserId,
        todoId = todoId,
        status = status,
        message = message
    )
}
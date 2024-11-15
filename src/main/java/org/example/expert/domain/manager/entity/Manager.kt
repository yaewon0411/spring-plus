package org.example.expert.domain.manager.entity

import jakarta.persistence.*
import org.example.expert.domain.common.entity.KTimestamped
import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.todo.entity.Todo
import org.example.expert.domain.user.entity.User

@Entity
@Table(name = "managers")
class Manager protected constructor(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false)
    var todo: Todo,
): KTimestamped(){

    constructor(user: User, todo: Todo) :this(
        id = null,
        user = user,
        todo = todo
    )

    fun isAssigned(todoId: Long) {
        if (todo.id != todoId) {
            throw InvalidRequestException("해당 일정에 등록된 담당자가 아닙니다.")
        }
    }

}
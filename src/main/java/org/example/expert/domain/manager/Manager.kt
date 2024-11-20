package org.example.expert.domain.manager

import jakarta.persistence.*
import org.example.expert.domain.base.BaseEntity
import org.example.expert.domain.todo.Todo
import org.example.expert.domain.user.User
import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode

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
): BaseEntity(){

    constructor(user: User, todo: Todo) :this(
        id = null,
        user = user,
        todo = todo
    )

    fun isAssigned(todoId: Long) {
        if (todo.id != todoId) {
            throw CustomApiException(ErrorCode.MANAGER_NOT_IN_TODO)
        }
    }

}
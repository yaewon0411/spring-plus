package org.example.expert.domain.comment

import jakarta.persistence.*
import org.example.expert.domain.base.BaseEntity
import org.example.expert.domain.todo.Todo
import org.example.expert.domain.user.User

@Entity
@Table(name = "comments")
class Comment protected constructor(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var contents: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false)
    var todo: Todo
): BaseEntity() {

    constructor(contents: String, user: User, todo: Todo) :this(
        id = null,
        contents = contents,
        user = user,
        todo = todo
    )

}
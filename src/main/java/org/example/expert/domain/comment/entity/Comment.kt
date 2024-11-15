package org.example.expert.domain.comment.entity

import jakarta.persistence.*
import org.example.expert.domain.common.entity.Timestamped
import org.example.expert.domain.todo.entity.Todo
import org.example.expert.domain.user.entity.User

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
): Timestamped() {

    constructor(contents: String, user: User, todo: Todo) :this(
        id = null,
        contents = contents,
        user = user,
        todo = todo
    )

}
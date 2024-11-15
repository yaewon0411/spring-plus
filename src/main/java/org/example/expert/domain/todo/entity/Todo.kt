package org.example.expert.domain.todo.entity

import jakarta.persistence.*
import org.example.expert.domain.comment.entity.Comment
import org.example.expert.domain.common.entity.Timestamped
import org.example.expert.domain.common.exception.InvalidRequestException
import org.example.expert.domain.manager.entity.Manager
import org.example.expert.domain.user.entity.User

@Entity
@Table(name = "todos")
class Todo protected constructor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var title: String,
    var contents: String,
    var weather: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @OneToMany(mappedBy = "todo", cascade = [CascadeType.REMOVE])
    var comments: MutableList<Comment> = mutableListOf(),

    @OneToMany(mappedBy = "todo", cascade = [CascadeType.PERSIST])
    var managers: MutableList<Manager> = mutableListOf()

): Timestamped() {

    init {
        managers.add(Manager(user, this))
    }

    constructor(title: String, contents: String, weather: String, user: User):this(
        id = null,
        title = title,
        contents = contents,
        weather = weather,
        user = user
    )

    fun isOwner(user: User){
        if(this.user.id != user.id){
            throw InvalidRequestException("해당 일정을 만든 유저여야 합니다")
        }
    }

    fun validateManagerAssignment(targetUser: User){
        if (user.id == targetUser.id) {
            throw InvalidRequestException("일정 작성자는 본인을 담당자로 등록할 수 없습니다")
        }
    }
}
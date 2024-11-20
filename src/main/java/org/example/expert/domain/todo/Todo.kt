package org.example.expert.domain.todo

import jakarta.persistence.*
import org.example.expert.domain.comment.Comment
import org.example.expert.domain.base.BaseEntity
import org.example.expert.domain.manager.Manager
import org.example.expert.domain.user.User
import org.example.expert.exception.CustomApiException
import org.example.expert.exception.ErrorCode
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "todos")
class Todo protected constructor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(length = 20, nullable = false)
    var title: String,
    @Column(length = 255, nullable = false)
    var contents: String,
    var weather: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @OneToMany(mappedBy = "todo", cascade = [CascadeType.REMOVE])
    var comments: MutableList<Comment> = mutableListOf(),

    @OneToMany(mappedBy = "todo", cascade = [CascadeType.PERSIST])
    var managers: MutableList<Manager> = mutableListOf()

): BaseEntity() {


    constructor(title: String, contents: String, weather: String, user: User):this(
        id = null,
        title = title,
        contents = contents,
        weather = weather,
        user = user
    ){
        managers.add(Manager(user, this))
    }


    fun isOwner(user: User){
        if(this.user.id != user.id){
            throw CustomApiException(ErrorCode.FORBIDDEN_TODO_ACCESS)
        }
    }

    fun validateManagerAssignment(targetUser: User){
        if (user.id == targetUser.id) {
            throw CustomApiException(ErrorCode.AUTHOR_CANNOT_BE_MANAGER)
        }
    }

    companion object{
        fun createForTest(id: Long, title: String, contents: String, weather: String, user: User) =
            Todo(id, title, contents, weather, user)
    }
}
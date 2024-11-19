package org.example.expert.domain.manager

import org.example.expert.domain.todo.Todo
import org.example.expert.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ManagerRepository: JpaRepository<Manager, Long> {

    @Query("select m from Manager m join fetch m.user where m.todo = :todo")
    fun findByTodoWithUser(@Param("todo") todo: Todo): List<Manager>

    fun findByUserAndTodo(@Param("user") user: User, @Param("todo") todo: Todo): Manager?
}
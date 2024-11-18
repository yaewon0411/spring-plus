package org.example.expert.domain.todo

import org.springframework.data.jpa.repository.JpaRepository

interface TodoRepository: JpaRepository<Todo, Long>, TodoQueryDslRepository {

}
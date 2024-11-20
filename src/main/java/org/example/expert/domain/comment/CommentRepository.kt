package org.example.expert.domain.comment

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CommentRepository: JpaRepository<Comment, Long>, CommentQueryDslRepository {

}
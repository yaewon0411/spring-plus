package org.example.expert.domain.comment

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.example.expert.controller.comment.dto.response.CommentInfoRespDto
import org.example.expert.controller.user.dto.response.UserInfoRespDto
import org.example.expert.domain.comment.entity.QComment
import org.example.expert.domain.comment.entity.QComment.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class CommentQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory
): CommentQueryDslRepository {

    override fun getCommentsWithUserByTodoId(todoId: Long, pageable: Pageable): Page<CommentInfoRespDto> {
        val commentList = queryFactory
            .select(
                Projections.constructor(
                    CommentInfoRespDto::class.java,
                    comment.id,
                    comment.contents,
                    Projections.constructor(
                        UserInfoRespDto::class.java,
                        comment.user.id,
                        comment.user.email,
                        comment.user.nickname
                    )

                )
            )
            .from(comment)
            .leftJoin(comment.user)
            .where(comment.todo.id.eq(todoId))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageImpl(commentList, pageable, getTotalCount(todoId))
    }

    private fun getTotalCount(todoId: Long): Long =
        queryFactory
            .select(comment.count())
            .from(comment)
            .where(comment.todo.id.eq(todoId))
            .fetchOne()?: 0L
}
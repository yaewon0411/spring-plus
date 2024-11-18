package org.example.expert.domain.todo

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Expression
import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPAExpressions.select
import com.querydsl.jpa.impl.JPAQueryFactory
import org.example.expert.controller.todo.dto.request.TodoListReqDto
import org.example.expert.controller.todo.dto.request.TodoSearchReqDto
import org.example.expert.controller.todo.dto.response.TodoSearchRespDto.TodoRespDto
import org.example.expert.controller.todo.dto.response.TodoSearchRespDto.TodoRespDto.ManagerRespDto
import org.example.expert.domain.comment.entity.QComment.comment
import org.example.expert.domain.manager.Manager
import org.example.expert.domain.manager.entity.QManager.manager
import org.example.expert.domain.todo.entity.QTodo.todo
import org.example.expert.domain.user.User
import org.example.expert.domain.user.entity.QUser.user
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
@Transactional(readOnly = true)
class TodoQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory
): TodoQueryDslRepository {
    override fun findTodosByFilter(todoListReqDto: TodoListReqDto, pageable: Pageable): Page<Todo> {
        val todoList = queryFactory
            .selectFrom(todo)
            .leftJoin(todo.user, user).fetchJoin()
            .where(createFindFilter(todoListReqDto))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(todo.modifiedAt.desc())
            .fetch()

        val totalCount = getTotalCountWithFindFilter(todoListReqDto)
        return PageImpl(todoList, pageable, totalCount)
    }

    override fun findByIdWithUser(todoId: Long): Todo? =
        queryFactory
            .selectFrom(todo)
            .leftJoin(todo.user).fetchJoin()
            .where(todo.id.eq(todoId))
            .fetchFirst()

    override fun searchTodosByFilter(
        todoSearchReqDto: TodoSearchReqDto,
        pageable: Pageable
    ): Page<TodoRespDto> {
        val commentsCount = getCommentsCount("totalCommentsCount")
        val managersCount = getManagersCount("totalManagersCount")

        //일정 조회
        val todoTuple = queryFactory
            .select(
                todo.id,
                todo.title,
                todo.createdAt,
                commentsCount,
                managersCount
            )
            .from(todo)
            .leftJoin(todo.managers, manager)
            .leftJoin(manager.user, user)
            .where(createSearchFilter(todoSearchReqDto))
            .groupBy(todo.id)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(todo.createdAt.desc())
            .fetch()

        val todoIdList = todoTuple.mapNotNull { it.get(todo.id) }.toList()
        //일정에 배정된 담당자 조회
        val managerMap = getManagerMap(todoIdList)
        val todoList =  todoTuple.mapNotNull { tuple ->
            tuple.get(todo.id)?.let { id ->
                TodoRespDto(
                    title = tuple.get(todo.title)!!,
                    createdAt = tuple.get(todo.createdAt)!!,
                    totalCommentsCount = tuple.get(commentsCount)!!,
                    totalManagersCount = tuple.get(managersCount)!!,
                    managerList = managerMap.getOrDefault(id, emptyList())
                )
            }
        }
        val totalCount = getTotalCountWithSearchFilter(todoSearchReqDto)
        return PageImpl(todoList, pageable, totalCount)
    }

    private fun createSearchFilter(todoSearchReqDto: TodoSearchReqDto): BooleanBuilder{
        val builder = BooleanBuilder()

        todoSearchReqDto.keyword?.let { builder.and(containsTitle(it).or(containsNickname(it))) }

        betweenCreatedAt(
            todoSearchReqDto.startDateTime,
            todoSearchReqDto.endDateTime
        )?.let { builder.and(it) }

        return builder
    }

    private fun getCommentsCount(alias: String): Expression<Long> =
        ExpressionUtils.`as`(select(comment.id.count())
                .from(comment)
                .where(comment.todo.eq(todo)), alias)

    private fun getManagersCount(alias: String): Expression<Long> =
        ExpressionUtils.`as`(select(manager.id.count())
            .from(manager)
            .where(manager.todo.eq(todo)), alias)

    private fun getTotalCountWithSearchFilter(todoSearchReqDto: TodoSearchReqDto): Long =
        queryFactory
            .select(todo.countDistinct())
            .from(todo)
            .leftJoin(todo.managers, manager)
            .leftJoin(manager.user, user)
            .where(createSearchFilter(todoSearchReqDto))
            .fetchOne()?:0L

    private fun getManagerMap(todoIdList: List<Long>): Map<Long, List<ManagerRespDto>>{
        val managerTuple = queryFactory
            .select(
                manager.todo.id,
                Projections.constructor(
                    ManagerRespDto::class.java,
                    user.id.`as`("userId"),
                    user.nickname.`as`("managerNickname")
                )
            )
            .from(manager)
            .leftJoin(manager.user, user)
            .where(manager.todo.id.`in`(todoIdList))
            .fetch()

        return managerTuple.groupBy(
            {tuple -> tuple.get(manager.todo.id)!!},
            {tuple -> tuple.get(1, ManagerRespDto::class.java)!!}
        )
    }

    private fun createFindFilter(todoListReqDto: TodoListReqDto): BooleanBuilder{
        val builder = BooleanBuilder()

        //날씨 검색
        todoListReqDto.weather?.let { builder.and(containsWeather(it)) }

        //수정일 기간 검색
        betweenModifiedAt(todoListReqDto.startDateTime, todoListReqDto.endDateTime)
            ?.let { builder.and(it) }

        return builder
    }

    private fun getTotalCountWithFindFilter(todoListReqDto: TodoListReqDto): Long =
            queryFactory
                .select(todo.count())
                .from(todo)
                .where(createFindFilter(todoListReqDto))
                .fetchOne()?: 0L


    private fun containsWeather(keyword: String): BooleanExpression =
        todo.weather.contains(keyword)

    private fun containsTitle(keyword: String): BooleanExpression =
        todo.title.contains(keyword)

    private fun containsNickname(keyword: String): BooleanExpression =
        user.nickname.contains(keyword)

    private fun betweenCreatedAt(startDate: LocalDateTime?, endDate: LocalDateTime?) =
        combinePredicates(
            startDate?.let { todo.createdAt.goe(startDate) },
            endDate?.let { todo.createdAt.loe(endDate) }
        )

    private fun betweenModifiedAt(startDate: LocalDateTime?, endDate: LocalDateTime?) =
        combinePredicates(
            startDate?.let { todo.modifiedAt.goe(startDate) },
            endDate?.let { todo.modifiedAt.loe(endDate) }
        )

    private fun combinePredicates(vararg expressions: BooleanExpression?) =
        expressions.filterNotNull()
            .reduceOrNull{acc, expression -> acc.and(expression)}
}
package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.request.TodoListReqDto;
import org.example.expert.domain.todo.dto.request.TodoSearchReqDto;
import org.example.expert.domain.todo.dto.response.TodoListRespDto;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.querydsl.core.types.ExpressionUtils.count;
import static com.querydsl.jpa.JPAExpressions.*;
import static org.example.expert.domain.comment.entity.QComment.*;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.dto.response.TodoSearchRespDto.*;
import static org.example.expert.domain.todo.dto.response.TodoSearchRespDto.TodoRespDto.*;
import static org.example.expert.domain.todo.entity.QTodo.*;
import static org.example.expert.domain.user.entity.QUser.*;

@RequiredArgsConstructor
@Repository
@Transactional(readOnly = true)
public class TodoQueryDslRepositoryImpl implements TodoQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 검색 조건에 따라 Todo 목록을 페이징하여 조회합니다
     * 검색된 Todo의 댓글 수, 매니저 수, 매니저 정보를 포함한 DTO를 반환합니다
     *
     * @param todoSearchReqDto 검색 조건 (키워드, 생성일 범위)
     * @param pageable 페이징 정보
     * @return 검색된 Todo 목록과 페이징 정보가 포함된 Page 객체
     */
    @Override
    public Page<TodoRespDto> searchTodosByFilter(TodoSearchReqDto todoSearchReqDto, Pageable pageable) {

        //일정 댓글 수
        Expression<Long> commentsCount = getCommentsCount("totalCommentsCount");

        //일정에 배정된 매니저 수
        Expression<Long> managersCount = getManagersCount("totalManagersCount");

        //일정 조회
        List<Tuple> todoTuple = queryFactory
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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(todo.createdAt.desc())
                .fetch();

        List<Long> todoIdList = todoTuple.stream()
                .map(tuple -> tuple.get(todo.id))
                .toList();

        //일정의 매니저 조회
        Map<Long, List<ManagerRespDto>> managerMap = getManagerMap(todoIdList);

        //최종 반환할 응답
        List<TodoRespDto> todoList = todoTuple.stream()
                .map(tuple -> new TodoRespDto(
                        tuple.get(todo.title),
                        tuple.get(todo.createdAt),
                        tuple.get(commentsCount),
                        tuple.get(managersCount),
                        managerMap.getOrDefault(tuple.get(todo.id), new ArrayList<>())
                ))
                .toList();

        Long totalCount = getTotalCountWithSearchFilter(todoSearchReqDto);

        return new PageImpl<>(todoList, pageable, totalCount);
    }

    /**
     * 필터 조건에 따라 Todo 엔티티 목록을 페이징하여 조회합니다
     * 조회 시 Todo 작성자 정보를 함께 fetch join으로 가져옵니다
     *
     * @param todoListReqDto 필터 조건 (날씨, 수정일 범위)
     * @param pageable 페이징 정보
     * @return 조회된 Todo 엔티티 목록과 페이징 정보가 포함된 Page 객체
     */
    public Page<Todo> findTodosByFilter(TodoListReqDto todoListReqDto, Pageable pageable){
        List<Todo> todoList = queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(createFindFilter(todoListReqDto))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(todo.modifiedAt.desc())
                .fetch();

        Long totalCount = getTotalCountWithFindFilter(todoListReqDto);

        return new PageImpl<>(todoList, pageable, totalCount);
    }

    private BooleanExpression containsWeather(String keyword){
        return todo.weather.contains(keyword);
    }

    private BooleanBuilder createSearchFilter(TodoSearchReqDto todoSearchReqDto){

        BooleanBuilder builder = new BooleanBuilder();

        //일정 제목 & 담당자 닉네임
        Optional.ofNullable(todoSearchReqDto.getKeyword())
                .filter(StringUtils::hasText)
                .ifPresent(keyword ->{
                    builder.and(containsNickname(keyword).or(containsTitle(keyword)));
                });

        //생성일 기간 검색
        Optional.ofNullable(betweenCreatedAt(
                todoSearchReqDto.getStartDateTime(),
                todoSearchReqDto.getEndDateTime()
        )).ifPresent(builder::and);

        return builder;
    }


    private BooleanBuilder createFindFilter(TodoListReqDto todoListReqDto){
        BooleanBuilder builder = new BooleanBuilder();

        //날씨 검색
        Optional.ofNullable(todoListReqDto.getWeather())
                .filter(StringUtils::hasText)
                .ifPresent(weather -> builder.and(containsWeather(weather)));

        //수정일 기간 검색
        Optional.ofNullable(betweenModifiedAt(
                todoListReqDto.getStartDateTime(),
                todoListReqDto.getEndDateTime()
        )).ifPresent(builder::and);

        return builder;
    }


    private Long getTotalCountWithFindFilter(TodoListReqDto todoListReqDto) {
        return Optional.ofNullable(queryFactory
                        .select(todo.count())
                        .from(todo)
                        .where(createFindFilter(todoListReqDto))
                        .fetchOne())
                        .orElse(0L);
    }


    public Optional<Todo> findByIdWithUser(Long todoId){
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(todo)
                        .leftJoin(todo.user).fetchJoin()
                        .where(todo.id.eq(todoId))
                        .fetchFirst()
        );
    }


    private Long getTotalCountWithSearchFilter(TodoSearchReqDto todoSearchReqDto){
        return Optional.ofNullable(queryFactory
                        .select(todo.countDistinct())
                        .from(todo)
                        .leftJoin(todo.managers, manager)
                        .leftJoin(manager.user, user)
                        .where(createSearchFilter(todoSearchReqDto))
                        .fetchOne())
                .orElse(0L);
    }


    private BooleanExpression containsTitle(String keyword){
        return todo.title.contains(keyword);
    }


    private BooleanExpression containsNickname(String keyword){
        return user.nickname.contains(keyword);
    }


    private BooleanExpression betweenCreatedAt(LocalDateTime startDate, LocalDateTime endDate){
        return combinePredicates(
                startDate != null? todo.createdAt.goe(startDate):null,
                endDate != null? todo.createdAt.loe(endDate):null
        );
    }


    private BooleanExpression betweenModifiedAt(LocalDateTime startDate, LocalDateTime endDate){
        return combinePredicates(
                startDate != null? todo.modifiedAt.goe(startDate):null,
                endDate != null? todo.modifiedAt.loe(endDate):null
        );
    }


    private BooleanExpression combinePredicates(BooleanExpression... expressions){
        return Arrays.stream(expressions)
                .filter(Objects::nonNull)
                .reduce(BooleanExpression::and)
                .orElse(null);
    }


    private Map<Long, List<ManagerRespDto>> getManagerMap(List<Long> todoIdList){
        List<Tuple> managerTuple = queryFactory
                .select(manager.todo.id,
                        Projections.constructor(ManagerRespDto.class,
                                user.nickname.as("managerNickname"),
                                user.id.as("userId")))
                .from(manager)
                .leftJoin(manager.user, user)
                .where(manager.todo.id.in(todoIdList))
                .fetch();

        return managerTuple.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(manager.todo.id),
                        Collectors.mapping(
                                tuple -> tuple.get(1, ManagerRespDto.class),
                                Collectors.toList()
                        )));
    }


    private Expression<Long> getCommentsCount(String alias){
        return ExpressionUtils.as(select(count(comment.id))
                .from(comment)
                .where(comment.todo.eq(todo)), alias);
    }


    private Expression<Long> getManagersCount(String alias){
        return ExpressionUtils.as(select(count(manager.id))
                .from(manager)
                .where(manager.todo.eq(todo)), alias);
    }


}

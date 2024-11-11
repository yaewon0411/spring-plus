package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.request.TodoSearchReqDto;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.todo.entity.QTodo.*;
import static org.example.expert.domain.user.entity.QUser.*;

@RequiredArgsConstructor
@Repository
@Transactional(readOnly = true)
public class TodoQueryDslRepositoryImpl implements TodoQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Todo> searchTodosByFilter(TodoSearchReqDto todoSearchReqDto, Pageable pageable){
        BooleanBuilder booleanBuilder = createSearchFilter(todoSearchReqDto);

        List<Todo> todoList = queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(booleanBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(todo.modifiedAt.desc())
                .fetch();

        long totalCount = getTotalCount(booleanBuilder);

        return new PageImpl<>(todoList, pageable, totalCount);
    }

    private BooleanBuilder createSearchFilter(TodoSearchReqDto todoSearchReqDto){
        BooleanBuilder builder = new BooleanBuilder();

        //날씨 검색
        Optional.ofNullable(todoSearchReqDto.getWeather())
                .filter(StringUtils::hasText)
                .ifPresent(weather -> builder.and(todo.weather.contains(weather)));

        //수정일 기간 검색
        Optional.ofNullable(todoSearchReqDto.getStartDateTime())
                .ifPresent(startDate -> builder.and(todo.modifiedAt.goe(startDate)));
        Optional.ofNullable(todoSearchReqDto.getEndDateTime())
                .ifPresent(endDate -> builder.and(todo.modifiedAt.loe(endDate)));

        return builder;
    }

    private long getTotalCount(BooleanBuilder builder) {
        return queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user, user)
                .where(builder)
                .fetchCount();
    }



}

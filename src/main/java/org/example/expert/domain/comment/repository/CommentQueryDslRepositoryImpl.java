package org.example.expert.domain.comment.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.expression.spel.ast.Projection;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.example.expert.domain.comment.entity.QComment.*;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryDslRepositoryImpl implements CommentQueryDslRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CommentResponse> getCommentsWithUserByTodoId(Long todoId, Pageable pageable) {
        List<CommentResponse> commentList = queryFactory
                .select(Projections.constructor(CommentResponse.class,
                        comment.id,
                        comment.contents,
                        Projections.constructor(UserResponse.class,
                                comment.user.id,
                                comment.user.email)
                ))
                .from(comment)
                .leftJoin(comment.user)
                .where(comment.todo.id.eq(todoId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = getTotalCount(todoId);

        return new PageImpl<>(commentList, pageable, totalCount);
    }

    private Long getTotalCount(Long todoId){
        return queryFactory
                        .select(comment.count())
                        .from(comment)
                        .where(comment.todo.id.eq(todoId))
                        .fetchOne();
    }
}

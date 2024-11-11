### 검색 동적 쿼리 작성을 위한 querydsl 레포 추가
- 검색 시 추가된 요구 조건
    - 일정의 weather로 검색할 수 있어야 함
    - 일정 수정일 구간으로 검색할 수 있어야 함

1) 
우선 검색 조건과 페이징 값을 TodoSearchReqDto 객체를 통해 받도록 수정

2)
검색 조건을 동적으로 적용해야 하고 페치 조인과 페이징이 들어가야 함

TypeQuery를 통해 구현하면 코드가 복잡해지고 이로 인한 가독성과 유지 보수성이 떨어져서
queryDSL을 사용한 검색용 레포를 추가 생성해 다음과 같이 구현
```java
@RequiredArgsConstructor
@Repository
@Transactional(readOnly = true)
public class TodoSearchRepository {

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

```

3)
기존 페이지 메타 데이터를 전부 내보내고 있던 것을 중요 정보만 내보내도록 TodoListRespDto를 생성해 선택적으로 값이 나가도록 수정


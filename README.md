<details>
<summary><b>일정 검색 기능: queryDsl</b></summary>

### 기능 설명
- 키워드(일정 제목, 일정 담당자 닉네임)와 생성일 구간으로 일정 검색
- 페이징 처리 적용
- 각 일정의 댓글 수와 담당자 수 집계
- 담당자 상세 정보(id, 닉네임) 함께 조회


### 구현 상세
#### 1. 메인 쿼리(searchTodosByFilter)
- 일정 기본 정보와 함께 댓글 수, 담당자 수 서브쿼리로 조회
- 일정 id로 중복 제거
- 매니저와 유저 조인하여 검색 필터링
- 생성일 기준 내림차순 정렬
#### 2. 담당자 정보 조회(getManagerMap)
- 조회된 일정 id 목록 기반으로 담당자 정보 별도 조회 (전역 batch size 설정)
- 담당자 id와 닉네임 정보 반환
- 일정 id를 key로 하는 Map으로 그룹화해서 최종 응답 생성 시 매핑에 사용
#### 3. 검색 조건 처리(createSearchFilter)
- 키워드 검색: 일정 제목 또는 담당자 닉네임에 포함된 문자열 검색
- 기간 검색: 시작일시와 종료일시 범위 내 일정 검색
- null값 처리


</details>


<details>
<summary><b>자체 구현 인증/인가 처리 방식에서 Spring Security로 전환</b></summary>

- 기존 커스텀 JWT 필터 기반 인증 체계에서 Spring Security 프레임워크로 전환
  - 기존 관련 파일들 /legacy 디렉터리로 이동

### Spring Security 설정
- JWT 기반의 Stateless 인증 구현
- 주요 보안 설정:
    - CORS 설정 활성화
    - CSRF 보호 비활성화
    - Session 미사용 (STATELESS)
    - Form 로그인 비활성화
    - HTTP Basic 인증 비활성화

### API 접근 권한
- 인증 없이 접근 가능: `/auth/**`
- 관리자 전용: `/admin/**`
- 그 외 엔드포인트: 인증 필요

### Custom Filters
1. JwtExceptionFilter: JWT 관련 예외 처리
2. JwtAuthenticationFilter: 로그인 및 JWT 토큰 발급
3. JwtAuthorizationFilter: JWT 토큰 검증 및 인가 처리

### 보안 예외 처리
- CustomAuthenticationEntryPoint: 인증 실패 처리
- CustomAccessDeniedHandler: 인가 실패 처리

</details>




<details>
<summary><b>일정 단건 조회 시의 N+1 문제 처리: queryDsl</b></summary>

  - 기존 코드
    -  Todo 조회할 때 User를 페치 조인으로 가져오지 않아 Todo.getUser().getXXX()를 수행할 때 추가 쿼리 발생
  - 개선
    - querydsl을 사용해 Todo 조인 시 User 페치 조인

```java
    public Optional<Todo> findByIdWithUser(Long todoId){
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(todo)
                        .leftJoin(todo.user).fetchJoin()
                        .where(todo.id.eq(todoId))
                        .fetchFirst()
        );
    }
```



</details>



<details>
     <summary><b>댓글 목록 조회 시의 N+1 문제 처리: queryDsl</b></summary>

**- 기존 코드**
  - Todo가 존재하는지 여부 검증 안하고 바로 Todo의 댓글 목록 조회
  - Comment를 조회할 때 User를 페치 조인으로 가져오지 않아 Comment.getUser().getXXX()를 수행할 때 추가 쿼리가 나가고 있음
  - Comment 페이징 처리 없이 전체 반환하고 있음

**- 개선**
  - todo가 존재하는지 검증하는 코드 추가 
  - queryDSL 사용하여 프로젝션과 페이징 수행
  - 페이징 정보와 전체 댓글 목록 반환 위해 CommentListRespDto 추가
  
**- 단위 테스트 진행**
  - todo 댓글 목록 페이징 조회 성공 테스트
  - todo 댓글 목록 페이징 조회 성공 테스트: 댓글이 없는 경우
  - todo 댓글 목록 페이징 조회 성공 테스트: 페이지 번호가 총 페이지 수 초과하면 빈 목록 반환
  - todo 댓글 목록 페이징 조회 실패 테스트: 존재하지 않는 할일

```java
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
```





</details>






<details>
     <summary><b>cascade를 사용한 Todo 생성 시 Manager 생성</b></summary>
 - cascade 옵션을 PERSIST로 지정하여 Todo가 생성될 때 managers 컬렉션에 있는 Manager 엔티티도 함께 저장되도록 수정

```java
    @OneToMany(mappedBy = "todo", cascade = CascadeType.PERSIST)
    private List<Manager> managers = new ArrayList<>();

    public Todo(String title, String contents, String weather, User user) {
        this.title = title;
        this.contents = contents;
        this.weather = weather;
        this.user = user;
        this.managers.add(new Manager(user, this));
    }
```
</details>

<details>
    <summary><b>검색 동적 쿼리 작성을 위한 querydsl 레포 추가</b></summary>
    
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

    public Page<Todo> searchTodosByFilter(TodoSearchReqDto todoListReqDto, Pageable pageable){
        BooleanBuilder booleanBuilder = createSearchFilter(todoListReqDto);

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

    private BooleanBuilder createSearchFilter(TodoSearchReqDto todoListReqDto){
        BooleanBuilder builder = new BooleanBuilder();

        //날씨 검색
        Optional.ofNullable(todoListReqDto.getWeather())
                .filter(StringUtils::hasText)
                .ifPresent(weather -> builder.and(todo.weather.contains(weather)));

        //수정일 기간 검색
        Optional.ofNullable(todoListReqDto.getStartDateTime())
                .ifPresent(startDate -> builder.and(todo.modifiedAt.goe(startDate)));
        Optional.ofNullable(todoListReqDto.getEndDateTime())
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
기존 페이지 메타 데이터를 전부 내보내고 있던 것을 TodoListRespDto를 통해 선택적으로 값이 나가도록 수정
</details>

<details>
    <summary><b>todo 단건 조회 실패 컨트롤러 테스트</b></summary>

- 기존 코드
```java
    @Test
    void todo_단건_조회_시_todo가_존재하지_않아_예외가_발생한다() throws Exception {
        // given
        long todoId = 1L;

        // when
        when(todoService.getTodo(todoId))
                .thenThrow(new InvalidRequestException("Todo not found"));

        // then
        mockMvc.perform(get("/todos/{todoId}", todoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Todo not found"));
    }
```

핸들러에서 InvalidRequestException이 발생했을 때 상태 코드를 BadRequest로 고정해서 응답을 반환하고 있음에 따라 기존 테스트 코드에서 200을 기대하고 있는 것을 400으로 수정

- 수정된 코드
```java
    @Test
    void todo_단건_조회_시_todo가_존재하지_않아_예외가_발생한다() throws Exception {
        // given
        long todoId = 1L;

        // when
        when(todoService.getTodo(todoId))
                .thenThrow(new InvalidRequestException("Todo not found"));

        // then
        mockMvc.perform(get("/todos/{todoId}", todoId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Todo not found"));
    }
```
</details>

<details>
    <summary><b>관리자 로그</b></summary>

- 기존 코드
```java
    @After("execution(* org.example.expert.domain.user.controller.UserController.getUser(..))")
    public void logAfterChangeUserRole(JoinPoint joinPoint) {
        String userId = String.valueOf(request.getAttribute("userId"));
        String requestUrl = request.getRequestURI();
        LocalDateTime requestTime = LocalDateTime.now();

        log.info("Admin Access Log - User ID: {}, Request Time: {}, Request URL: {}, Method: {}",
                userId, requestTime, requestUrl, joinPoint.getSignature().getName());
    }
```

UserAdminController 클래스의 changeUserRole() 메소드가 실행 전 동작해야하므로 아래와 같이 수정
- 메서드명을 logAfterChangeUserRole에서 logBeforeChangeUserRole로 변경
- @Before 어노테이션으로 변경하고 해당 메서드 경로 지정

```java
    @Before("execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
    public void logBeforeChangeUserRole(JoinPoint joinPoint) {
        String userId = String.valueOf(request.getAttribute("userId"));
        String requestUrl = request.getRequestURI();
        LocalDateTime requestTime = LocalDateTime.now();

        log.info("Admin Access Log - User ID: {}, Request Time: {}, Request URL: {}, Method: {}",
                userId, requestTime, requestUrl, joinPoint.getSignature().getName());
    }
```
</details>

<details>
    <summary><b>TodoService의 saveTodo() 에서의 오류</b></summary>

- 기존 코드

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail())
        );
    }
    //....
}
```
여기서 `[Connection is read-only. Queries leading to data modification are not allowed] [insert into todos (contents,created_at,modified_at,title,user_id,weather) values (?,?,?,?,?,?)]` 오류가 발생한다 하던데<br>

트랜잭션이 읽기 전용으로 시작되어도 SimpleJpaRepository.class의 save()를 호출할 때 save()에 달려있는 트랜잭션에 의해 쓰기 작업으로 재정의되기 때문에 커밋 후 쓰기 작업이 DB에 정상적으로 반영된다고 생각합니다<br>
혹시 yaml에서 db 커넥션을 읽기 전용으로 가져오도록 지정한 상황에서 위 오류가 발생한 것인지를 확인하기 위해 `hikari.read-only=true` 상황에서 테스트를 해보았으나 쓰기 작업이 성공함을 확인했습니다<br>

우선 명시적으로 메서드 레벨에서 readOnly를 재정의하도록 @Transactional을 추가해 코드 의도가 들어나게만 개선합니다
</details>

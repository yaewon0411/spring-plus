
### cascade를 사용한 Todo 생성 시 Manager 생성
cascade 옵션을 PERSIST로 지정하여 Todo가 생성될 때 managers 컬렉션에 있는 Manager 엔티티도 함께 저장되도록 수정

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



3)
기존 페이지 메타 데이터를 전부 내보내고 있던 것을 중요 정보만 내보내도록 TodoListRespDto를 생성해 선택적으로 값이 나가도록 수정


### todo 단건 조회 실패 컨트롤러 테스트
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



### 관리자 로그

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


### 1. TodoService의 saveTodo() 에서의 오류

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


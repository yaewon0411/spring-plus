<details>
<summary><b>서비스랑 컨트롤러 코틀린으로 적용한 주요 변경 사항 기재하기</b></summary>

</details>
<details>
<summary><b>엔티티 코틀린으로 전환: 주요 변경사항</b></summary>

- @Getter 제거: 코틀린의 프로퍼티는 기본적으로 getter 자동 생성
- 클래스 참조: ``::class`` 사용
- 프로퍼티 선언
  - pk(id)는 val로 선언 가능: jpa 구현체가 reflection을 통해 값을 할당하므로 불변으로 선언해도 됨
  - createdAt은 var로 선언: val로 선언하면 처음 클래스 생성자가 올라올 떄 null로 설정되므로 var로 설정해야 한다. 어차피 updateable = false에 의해 수정 막아진다
  - static 메서드: companion opbject 안에 구현
  - 기본값 처리: 널이 불가능한 컬럼은 생성자에서 기본값 지정 필요
  - 컬렉션 처리
    - 초기화: jpa에서는 변경 가능한 컬렉션이 필요하므로 MutuableList 사용
    - 빈 컬렉션: emptyList() 대신 mutubalListOf() 사용
  - 어노테이션 관련
    - cascade 타입 지정: 어노테이션의 배열 파라미터를 []로 표현
  - 생성자 처리
    - protected 생성자: @NoArgs(access = PROTECTED) 대신 protected consturctor 사용
      - kotlin-jpa 플러그인 사용 시 protected consturctor에 필드를 포함하더라도 jpa가 필요로 하는 빈 protected 생성자를 자동 생성해줌
      - 플러그인 없다면 별도로 no-args 생성자 명시적 구현 필요

</details>

<details>
<summary><b>담당자 등록 요청 기록 로깅 AOP 구현</b></summary>

일정에 담당자 등록 요청이 들어올 때 해당 내용에 대한 로그를 기록하게 되었다

담당자 등록과는 별개로 로그 테이블에는 항상 요청 로그가 남아야 한다

우선 다음과 같은 로깅 요구사항이 있다:

- 담당자 등록 요청이 실패해도 해당 로그가 남아야 한다
- 로그 생성 시간이 같이 저장되어야 한다
- 상세 메시지가 들어가야 한다

로깅 기능에서 발생 가능한 오류 수준은 다음과 같이 정의했다

먼저 시스템에서 로그는 핵심 기능이 아니라 부가 기능으로 판단했다

- **로그 저장에 실패** → 부가적인 문제이므로 warn 로그만 남기도록 한다
- **로그 컨텍스트 생성 실패** → 이 경우는 시스템이 예상하는 기본 전제 조건 검증에 실패한 경우이다. 즉 AOP 설정이나 메서드 시그니처가 예상과 다르게 변경되었다는 것이고 AOP가 동작하기 위한 시스템 구조에 문제가 발생했다는 것이므로 error로그를 남기고 오류를 던지도록 한다

로그 데이터의 특성 상 비즈니스 로직의 성공/실패 여부와 관계없이 반드시 저장되어야 한다

즉 로그 저장 실패가 비즈니스 로직 실행에 영향을 주면 안된다

따라서 비즈니스 트랜잭션과 로그 저장 트랜잭션을 분리해서 로깅이 이루어지도록 한다

담당자 등록 요청 메서드가 호출될 때 전후로 로깅 AOP가 작동하도록 설계하려 한다

요청이 들어오면  ‘AOP 프록시 → 트랜잭션 프록시 → 비즈니스 로직’ 이런 프록시 체인을 타게 할 것이다

이에 대한 상세 실행 순서는 다음과 같다:

1. **@Around 어드바이스 시작**
2. **@Transactional 트랜잭션 시작 (ManagerService.saveManager())**
3. **joinPoint.proceed() 실행**
4. **로그 저장 메서드 호출 (REQUIRES_NEW로 새 트랜잭션 생성)**
5. **@Transacional 트랜잭션 종료 (ManagerService.saveManager())**
6. **@Around 어드바이스 종료**


로그 저장 메서드는 아래와 같이 생성했다

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerReqLogService {

    private final ManagerReqLogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(ManagerSaveRequest managerSaveRequest, ManagerReqStatus status, User user, Long todoId, String message){
        ManagerReqLog log = ManagerReqLog.builder()
                .requestUserId(user.getId())
                .targetUserId(managerSaveRequest.getManagerUserId())
                .todoId(todoId)
                .status(status)
                .message(message)
                .build();
        logRepository.save(log);
    }
}
```



로그 저장 시 상세 내용을 같이 넣어줄 것이다

**ManagerLogMessage** ENUM을 만들어서 비즈니스 성공/실패와 발생 가능한 오류 상황에 대한 상세 메시지를 관리하도록 했다



AOP에서 사용할 데이터는 별도의 객체로 만들어서 관리하도록 한다

이렇게 별도로 분리하지 않으면 AOP가 로깅에 필요한 모든 세부 사항을 관리해야 하고 로깅 요구 사항이 변경됐을 때 유연성이 떨어지게 된다

**ManagerLogContext**라는 이름으로 다음과 같이 생성했다

여기서 캐스팅, 유효성 검증과 에러 처리를 수행하도록 한다

```java
@Getter
@Slf4j
public class ManagerLogContext {
    public static final int ARGS_COUNT = 3;
    private final User user;
    private final Long todoId;
    private final ManagerSaveRequest request;

    private ManagerLogContext(User user, Long todoId, ManagerSaveRequest request) {
        this.user = user;
        this.todoId = todoId;
        this.request = request;
    }

    public static ManagerLogContext validateAndCreate(Object[] args){
        validateArgs(args);
        return new ManagerLogContext(
                (User) args[0],
                (Long) args[1],
                (ManagerSaveRequest) args[2]
        );
    }

    private static void validateArgs(Object[] args) {
        validateArgsCount(args);
        validateArgsType(args);
    }

    private static void validateArgsCount(Object[] args) {
        if (args.length != ARGS_COUNT) {
            String errorMsg = ManagerLogMessage.INVALIDATE_ARGS_COUNT.format(ARGS_COUNT, args.length);
            log.error("ManagerLogContext 생성 실패: {}", errorMsg);
            throw new ServerException(errorMsg);
        }
    }

    private static void validateArgsType(Object[] args) {
        if (!(args[0] instanceof User)) {
            String errorMsg = ManagerLogMessage.INVALIDATE_USER_TYPE.format(args[0]!= null? args[0].getClass().getName(): "null");
            log.error("ManagerLogContext 생성 실패: {}", errorMsg);
            throw new ServerException(errorMsg);
        }
        if (!(args[1] instanceof Long)) {
            String errorMsg = ManagerLogMessage.INVALIDATE_TODO_ID_TYPE.format(args[1] != null ? args[1].getClass().getName() : "null");
            log.error("ManagerLogContext 생성 실패: {}", errorMsg);
            throw new ServerException(errorMsg);
        }
        if (!(args[2] instanceof ManagerSaveRequest)) {
            String errorMsg = ManagerLogMessage.INVALIDATE_REQUEST_TYPE.format(args[2] != null ? args[2].getClass().getName() : "null");
            log.error("ManagerLogContext 생성 실패: {}", errorMsg);
            throw new ServerException(errorMsg);
        }
    }
}
```

이제 담당자 저장 메서드를 포인트컷으로 하는 @Around 어드바이스를 구현해준다

여기서 @After가 아니라 @Around로 하는 이유는 다음과 같다

- **메서드 실행 결과에 따라 처리를 다르게 해야 함**
    - **성공 시** → SUCCESS 상태로 로그 저장
    - **실패 시** → FAIL 상태로 로그 저장
- **예외 종류에 따라 다르게 처리해야 함**
    - 비즈니스 검증 실패
    - 그 외 Exception → 내부 오류로 처리한다
- **예외를 처리하되 다시 발생한 오류를 던져야 한다**
    - 로그 저장 후에도 원래 발생한 예외를 내보내야 하기 때문이다

```java
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ManagerReqLogAspect {
    private static final String MANAGER_SAVE_POINTCUT =
            "execution(* org.example.expert.domain.manager.service.ManagerService.saveManager(..))";

    private final ManagerReqLogService logService;

    @Around(MANAGER_SAVE_POINTCUT)
    public Object logManagerAssignment(ProceedingJoinPoint joinPoint) throws Throwable{
        ManagerLogContext managerLogContext = ManagerLogContext.validateAndCreate(joinPoint.getArgs());
        try{
            Object result = joinPoint.proceed();
            String message = ManagerLogMessage.ASSIGNED_SUCCESS.format(managerLogContext.getRequest().getManagerUserId(), managerLogContext.getTodoId());
            saveLog(managerLogContext, message, ManagerReqStatus.SUCCESS, null);
            return result;
        }catch (InvalidRequestException e) {
            String message = ManagerLogMessage.ASSIGNED_FAIL.format(e.getMessage(), managerLogContext.getTodoId(), managerLogContext.getRequest().getManagerUserId());
            saveLog(managerLogContext, message, ManagerReqStatus.FAIL, e);
            throw e;
        }catch(Exception e){
            String message = ManagerLogMessage.INTERNAL_FAIL.format(e.getMessage(), managerLogContext.getTodoId(), managerLogContext.getRequest().getManagerUserId());
            saveLog(managerLogContext, message, ManagerReqStatus.FAIL, e);
            throw e;
        }
    }

    private void saveLog (ManagerLogContext context, String message, ManagerReqStatus status, Exception originalError) {
        try {
            logService.saveLog(context.getRequest(), status, context.getUser(), context.getTodoId(), message);
        } catch (Exception logError) {
            if (originalError == null) {
                log.error(ManagerLogMessage.LOG_SAVE_FAIL_WITH_ERROR.getFormat(), logError.getMessage(), logError);
            } else {
                log.error(ManagerLogMessage.LOG_SAVE_FAIL_WITH_ERROR.getFormat(), originalError.getMessage(), logError.getMessage(), logError);
            }
        }
    }
}
```

ManagerService의 saveManager()가 실행되면 포인트 컷이 트리거 되면서 **`logManagerAssignment()`**가 실행된다

먼저 AOP 진입점에서 로깅에 필요한 컨텍스트를 준비한다

이 시점에서 모든 필요한 파라미터의 유효성 검증이 이루어진다

```java
ManagerLogContext managerLogContext = ManagerLogContext.validateAndCreate(joinPoint.getArgs());
```

이후 saveManager()를 진행하고 try-catch 내에서 결과에 따른 메시지를 생성한다

예외 처리는 세 가지 케이스로 구분했다

- **비즈니스 정상 실행**
- **비즈니스 로직 검증 실패**
- **예상치 못한 오류**

각 상황 별로 적절한 메시지 템플릿을 사용해서 컨텍스트에서 필요한 정보를 추출한 후 메시지를 포맷팅한다

```java
    @Around(MANAGER_SAVE_POINTCUT)
    public Object logManagerAssignment(ProceedingJoinPoint joinPoint) throws Throwable{
        ManagerLogContext managerLogContext = ManagerLogContext.validateAndCreate(joinPoint.getArgs());
        try{
            Object result = joinPoint.proceed();
            String message = ManagerLogMessage.ASSIGNED_SUCCESS.format(managerLogContext.getRequest().getManagerUserId(), managerLogContext.getTodoId());
            saveLog(managerLogContext, message, ManagerReqStatus.SUCCESS, null);
            return result;
        }catch (InvalidRequestException e) {
            String message = ManagerLogMessage.ASSIGNED_FAIL.format(e.getMessage(), managerLogContext.getTodoId(), managerLogContext.getRequest().getManagerUserId());
            saveLog(managerLogContext, message, ManagerReqStatus.FAIL, e);
            throw e;
        }catch(Exception e){
            String message = ManagerLogMessage.INTERNAL_FAIL.format(e.getMessage(), managerLogContext.getTodoId(), managerLogContext.getRequest().getManagerUserId());
            saveLog(managerLogContext, message, ManagerReqStatus.FAIL, e);
            throw e;
        }
    }
```

**`saveLog()`**에서 로그를 저장하는 기능을 수행한다

```java
    private void saveLog (ManagerLogContext context, String message, ManagerReqStatus status, Exception originalError) {
        try {
            logService.saveLog(context.getRequest(), status, context.getUser(), context.getTodoId(), message);
        } catch (Exception logError) {
            if (originalError == null) {
                log.error(ManagerLogMessage.LOG_SAVE_FAIL_WITH_ERROR.getFormat(), logError.getMessage(), logError);
            } else {
                log.error(ManagerLogMessage.LOG_SAVE_FAIL_WITH_ERROR.getFormat(), originalError.getMessage(), logError.getMessage(), logError);
            }
        }
    }
```

앞서 언급했던 것처럼 비즈니스 로직과 로그 저장이 별도의 트랜잭션으로 분리되어 있어서 REQUIRES_NEW를 통해 로그 저장의 독립성이 보장된다

로그 저장에 실패한 경우 에러 로깅을 남기도록 했다

에러 로깅은 정상 케이스(비즈니스)에서 로그 저장에 실패한 경우와 또 다른 에러 상황에서 로그 저장 실패한 경우로 분기 된다

이렇게 간단히 요구사항에 따른 로깅 기능을 구현해봤다

현재는 동기적으로 로그를 저장하고 있지만 시스템 규모가 커지면 로그 저장을 비동기로 처리하거나 배치 처리로 한꺼번에 처리하는 방식을 고려해볼 수 있을 거 같다





</details>

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
public class TodoSearchRepository {

    private final JPAQueryFactory queryFactory;

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

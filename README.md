
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


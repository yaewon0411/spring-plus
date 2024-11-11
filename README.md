
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
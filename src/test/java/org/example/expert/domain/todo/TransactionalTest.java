package org.example.expert.domain.todo;

import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class TransactionalTest {

    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    @Rollback
    @DisplayName(value = "hikari.read-only=true 상황에서 쓰기 작업 시도")
    public void 쓰기작업테스트(){
        User user = new User("test@naver.com","12312312312", UserRole.USER,"nickname");
        userRepository.save(user);

        Todo todo = todoRepository.save(new Todo("test", "test", "test", user));
        assertThat(todo).isNotNull();
    }
}

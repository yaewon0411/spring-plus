package org.example.expert.domain.todo.controller;

import org.example.expert.config.security.SecurityConfig;
import org.example.expert.config.security.loginuser.LoginUser;
import org.example.expert.config.security.loginuser.LoginUserService;
import org.example.expert.controller.todo.TodoController;
import org.example.expert.controller.todo.dto.response.TodoInfoRespDto;
import org.example.expert.exception.CustomApiException;
import org.example.expert.exception.ErrorCode;
import org.example.expert.domain.todo.Todo;
import org.example.expert.service.TodoService;
import org.example.expert.domain.user.User;
import org.example.expert.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Test
    @WithMockUser(username = "test@test.com", roles = "USER")
    void todo_단건_조회에_성공한다() throws Exception {
        // given
        Long todoId = 1L;
        User user = new User(1L, "email", UserRole.USER, "nicknameee");
        Todo todo = Todo.Companion.createForTest(1L, "title","contents","Sunny",user);
        TodoInfoRespDto response = new TodoInfoRespDto(todo);
        // when
        when(todoService.getTodo(todoId)).thenReturn(response);

        // then
        mockMvc.perform(get("/todos/{todoId}", todoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.contents").value(todo.getContents()))
                .andExpect(jsonPath("$.data.title").value(todo.getTitle()));
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = "USER")
    void todo_단건_조회_시_todo가_존재하지_않아_예외가_발생한다() throws Exception {
        // given
        long todoId = 1L;

        // when
        when(todoService.getTodo(todoId))
                .thenThrow(new CustomApiException(ErrorCode.TODO_NOT_FOUND));

        // then
        mockMvc.perform(get("/todos/{todoId}", todoId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.apiError.message").value(ErrorCode.TODO_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.apiError.status").value(ErrorCode.TODO_NOT_FOUND.getStatus()));
    }
}

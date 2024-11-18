//package org.example.expert.service;
//
//import lombok.RequiredArgsConstructor;
//import org.example.expert.client.WeatherClient;
//import org.example.expert.controller.user.dto.response.UserInfoRespDto;
//import org.example.expert.exception.InvalidRequestException;
//import org.example.expert.controller.todo.dto.request.TodoSaveRequest;
//import org.example.expert.controller.todo.dto.request.TodoListReqDto;
//import org.example.expert.controller.todo.dto.request.TodoSearchReqDto;
//import org.example.expert.controller.todo.dto.response.TodoListRespDto;
//import org.example.expert.controller.todo.dto.response.TodoResponse;
//import org.example.expert.controller.todo.dto.response.TodoSaveResponse;
//import org.example.expert.controller.todo.dto.response.TodoSearchRespDto;
//import org.example.expert.domain.todo.Todo;
//import org.example.expert.domain.todo.TodoRepository;
//import org.example.expert.domain.user.User;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.example.expert.controller.todo.dto.response.TodoSearchRespDto.*;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class TodoService {
//
//    private final TodoRepository todoRepository;
//    private final WeatherClient weatherClient;
//
//    @Transactional
//    public TodoSaveResponse saveTodo(User user, TodoSaveRequest todoSaveRequest) {
//        String weather = weatherClient.getTodayWeather();
//
//        Todo newTodo = new Todo(
//                todoSaveRequest.getTitle(),
//                todoSaveRequest.getContents(),
//                weather,
//                user
//        );
//        Todo savedTodo = todoRepository.save(newTodo);
//
//        return new TodoSaveResponse(
//                savedTodo.getId(),
//                savedTodo.getTitle(),
//                savedTodo.getContents(),
//                weather,
//                new UserInfoRespDto(user)
//        );
//    }
//
//    public TodoListRespDto getTodos(TodoListReqDto todoListReqDto) {
//        Pageable pageable = PageRequest.of(todoListReqDto.getPage(), todoListReqDto.getSize(), Sort.by(Sort.Direction.DESC, "modifiedAt"));
//        Page<Todo> todoPageList = todoRepository.findTodosByFilter(todoListReqDto, pageable);
//        return new TodoListRespDto(todoPageList);
//    }
//
//    public TodoResponse getTodo(Long todoId) {
//        return todoRepository.findByIdWithUser(todoId)
//                .map(TodoResponse::new)
//                .orElseThrow(() -> new InvalidRequestException("Todo Not found"));
//    }
//
//    public Todo findByIdOrFail(Long todoId){
//        return todoRepository.findById(todoId)
//                .orElseThrow(() -> new InvalidRequestException("Todo Not Found"));
//    }
//
//    public TodoSearchRespDto searchTodos(TodoSearchReqDto todoSearchReqDto) {
//        Pageable pageable = PageRequest.of(todoSearchReqDto.getPage(), todoSearchReqDto.getSize(), Sort.by(Sort.Direction.DESC, "createdAt"));
//        Page<TodoRespDto> todoPageList = todoRepository.searchTodosByFilter(todoSearchReqDto, pageable);
//        return new TodoSearchRespDto(todoPageList);
//    }
//}

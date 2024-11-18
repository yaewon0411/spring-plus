package org.example.expert.domain.manager.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final TodoService todoService;

    @Transactional
    public ManagerSaveResponse saveManager(User user, Long todoId, ManagerSaveRequest managerSaveRequest) {
        User targetUser = userRepository.findById(managerSaveRequest.getTargetUserId())
                .orElseThrow(() -> new InvalidRequestException("등록하려고 하는 유저가 존재하지 않습니다"));
        Todo todo = todoService.findByIdOrFail(todoId);
        todo.isOwner(user);
        validateManagerAssignment(targetUser, todo);
        Manager manager = managerRepository.save(new Manager(targetUser, todo));

        return new ManagerSaveResponse(manager);
    }


    private User validateManagerAssignment(User targetUser, Todo todo){
        //이미 배정된 유저인지 확인
        if (managerRepository.findByUserAndTodo(targetUser, todo).isPresent()) {
            throw new InvalidRequestException("이미 해당 일정에 담당된 유저입니다");
        }
        todo.validateManagerAssignment(targetUser);
        return targetUser;
    }


    public List<ManagerResponse> getManagers(Long todoId) {
        Todo todo = todoService.findByIdOrFail(todoId);
        return managerRepository.findByTodoIdWithUser(todo.getId())
                .stream()
                .map(manager -> new ManagerResponse(manager.getId(), new UserResponse(manager.getUser())))
                .toList();
    }


    @Transactional
    public void deleteManager(User user, Long todoId, Long managerId) {
        Todo todo = todoService.findByIdOrFail(todoId);
        todo.isOwner(user);

        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new InvalidRequestException("Manager not found"));

        manager.isAssigned(todoId);
        managerRepository.delete(manager);
    }
}

package org.example.expert.domain.manager.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.config.security.loginuser.LoginUser;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
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
        Todo todo = todoService.findByIdOrFail(todoId);
        todo.isOwner(user.getId());
        User managerUser = validateManagerAssignment(managerSaveRequest, user);
        Manager savedManagerUser = managerRepository.save(new Manager(managerUser, todo));

        return new ManagerSaveResponse(
                savedManagerUser.getId(),
                new UserResponse(managerUser)
        );
    }

    private User validateManagerAssignment(ManagerSaveRequest managerSaveRequest, User user){
        User managerUser = userRepository.findById(managerSaveRequest.getManagerUserId())
                .orElseThrow(() -> new InvalidRequestException("등록하려고 하는 담당자 유저가 존재하지 않습니다."));

        if (ObjectUtils.nullSafeEquals(user.getId(), managerUser.getId())) {
            throw new InvalidRequestException("일정 작성자는 본인을 담당자로 등록할 수 없습니다.");
        }
        return managerUser;
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
        todo.isOwner(user.getId());

        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new InvalidRequestException("Manager not found"));

        manager.isAssigned(todoId);
        managerRepository.delete(manager);
    }
}

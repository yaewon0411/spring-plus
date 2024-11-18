package org.example.expert.controller.manager;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.config.security.loginuser.LoginUser;
import org.example.expert.controller.manager.dto.request.ManagerSaveRequest;
import org.example.expert.controller.manager.dto.response.ManagerResponse;
import org.example.expert.controller.manager.dto.response.ManagerSaveResponse;
import org.example.expert.service.ManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @PostMapping("/todos/{todoId}/managers")
    public ResponseEntity<ManagerSaveResponse> saveManager(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable(value = "todoId") Long todoId,
            @Valid @RequestBody ManagerSaveRequest managerSaveRequest
    ) {
        return ResponseEntity.ok(managerService.saveManager(loginUser.getUser(), todoId, managerSaveRequest));
    }

    @GetMapping("/todos/{todoId}/managers")
    public ResponseEntity<List<ManagerResponse>> getMembers(@PathVariable(value = "todoId") Long todoId) {
        return ResponseEntity.ok(managerService.getManagers(todoId));
    }

    @DeleteMapping("/todos/{todoId}/managers/{managerId}")
    public void deleteManager(
            @AuthenticationPrincipal LoginUser loginUser,
            @PathVariable(value = "todoId") Long todoId,
            @PathVariable(value = "managerId") Long managerId
    ) {
        managerService.deleteManager(loginUser.getUser(), todoId, managerId);
    }
}

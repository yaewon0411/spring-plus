package org.example.expert.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.config.security.loginuser.LoginUser;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal LoginUser loginUser) {
        return ResponseEntity.ok(userService.getUser(loginUser.getUser().getId()));
    }

    @PutMapping("/users")
    public void changePassword(@AuthenticationPrincipal LoginUser loginUser, @RequestBody @Valid UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(loginUser.getUser().getId(), userChangePasswordRequest);
    }
}

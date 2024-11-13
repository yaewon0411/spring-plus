package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.config.security.loginuser.LoginUser;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.service.UserAdminService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;

    @PatchMapping("/admin/users")
    public void changeUserRole(@AuthenticationPrincipal LoginUser loginUser, @RequestBody UserRoleChangeRequest userRoleChangeRequest) {
        userAdminService.changeUserRole(loginUser.getUser().getId(), userRoleChangeRequest);
    }
}

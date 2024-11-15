package org.example.expert.domain.manager.dto.response;

import lombok.Getter;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.user.dto.response.UserResponse;

@Getter
public class ManagerSaveResponse {

    private final Long id;
    private final UserResponse userInfo;

    public ManagerSaveResponse(Manager manager) {
        this.id = manager.getId();
        this.userInfo = new UserResponse(manager.getUser());
    }
}

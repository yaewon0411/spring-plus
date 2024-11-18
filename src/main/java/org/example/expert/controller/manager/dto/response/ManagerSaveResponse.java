package org.example.expert.controller.manager.dto.response;

import lombok.Getter;
import org.example.expert.domain.manager.Manager;
import org.example.expert.controller.user.dto.response.UserInfoRespDto;

@Getter
public class ManagerSaveResponse {

    private final Long id;
    private final UserInfoRespDto userInfo;

    public ManagerSaveResponse(Manager manager) {
        this.id = manager.getId();
        this.userInfo = new UserInfoRespDto(manager.getUser());
    }
}

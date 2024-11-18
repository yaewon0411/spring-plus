package org.example.expert.controller.manager.dto.response;

import lombok.Getter;
import org.example.expert.controller.user.dto.response.UserInfoRespDto;

@Getter
public class ManagerResponse {

    private final Long id;
    private final UserInfoRespDto user;

    public ManagerResponse(Long id, UserInfoRespDto user) {
        this.id = id;
        this.user = user;
    }
}

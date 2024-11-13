package org.example.expert.domain.auth.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.user.entity.User;

@Getter
@NoArgsConstructor
public class SignupResponse {
    private static final String WELCOME_MESSAGE = "회원 가입을 축하합니다🤍";
    private Long id;


    public SignupResponse(User user) {
        this.id = user.getId();
    }
}

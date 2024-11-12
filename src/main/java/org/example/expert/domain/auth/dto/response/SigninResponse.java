package org.example.expert.domain.auth.dto.response;

import lombok.Getter;
import org.example.expert.domain.user.entity.User;

@Getter
public class SigninResponse {

   private Long id;
   private String email;

    public SigninResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
    }
}

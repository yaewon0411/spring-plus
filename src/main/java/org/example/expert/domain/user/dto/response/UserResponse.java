package org.example.expert.domain.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.user.entity.User;

@Getter
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String email;

    public UserResponse(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
    }
}

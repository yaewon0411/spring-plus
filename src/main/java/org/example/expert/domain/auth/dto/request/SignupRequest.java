package org.example.expert.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String userRole;

    @NotBlank(message = "닉네임을 입력해주세요")
    @Size(min = 1, max = 12, message = "닉네임은 1자 이상 12자 이하여야 합니다")
    private String nickname;

    public User toEntity(PasswordEncoder passwordEncoder){
        return User.builder()
                .nickname(this.nickname)
                .password(passwordEncoder.encode(this.password))
                .email(this.email)
                .userRole(UserRole.valueOf(this.userRole))
                .build();
    }
}

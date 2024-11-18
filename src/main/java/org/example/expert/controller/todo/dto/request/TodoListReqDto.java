package org.example.expert.controller.todo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class TodoListReqDto {

    @Min(value = 0, message = "0 이상의 정수를 입력해야 합니다")
    private Integer page = 0;
    @Min(value = 1, message = "1 이상의 정수를 입력해야 합니다")
    private Integer size = 10;
    private String weather;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다")
    private String startModifiedAt;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다")
    private String endModifiedAt;

    public LocalDateTime getStartDateTime() {
        return startModifiedAt == null ? null
                : LocalDateTime.parse(startModifiedAt + " 00:00:00",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public LocalDateTime getEndDateTime() {
        return endModifiedAt == null ? null
                : LocalDateTime.parse(endModifiedAt + " 23:59:59",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}

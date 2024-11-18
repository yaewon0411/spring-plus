package org.example.expert.controller.todo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@Getter
@Setter
public class TodoSearchReqDto {
    //제목, 담당자 닉네임
    private String keyword;
    //일정 생성일 범위
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다")
    private String startCreatedAt;
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "유효하지 않은 날짜 형식입니다")
    private String endCreatedAt;

    @Min(value = 0, message = "0 이상의 정수를 입력해야 합니다")
    private Integer page = 0;
    @Min(value = 1, message = "1 이상의 정수를 입력해야 합니다")
    private Integer size = 10;

    public LocalDateTime getStartDateTime() {
        return startCreatedAt == null ? null
                : LocalDateTime.parse(startCreatedAt + " 00:00:00",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public LocalDateTime getEndDateTime() {
        return endCreatedAt == null ? null
                : LocalDateTime.parse(endCreatedAt + " 23:59:59",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}

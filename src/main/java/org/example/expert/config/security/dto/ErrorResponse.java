package org.example.expert.config.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final String msg;
    private final int status;

    public static ErrorResponse of (String msg, int status){
        return new ErrorResponse(msg, status);
    }
}

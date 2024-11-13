package org.example.expert.domain.common.exception;

import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {

    private String msg;

    public ServerException(String message) {
        super(message);
        this.msg = message;
    }
}

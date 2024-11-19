package org.example.expert.exception;

import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {

    private String msg;

    public ServerException(String message) {
        super(message);
        this.msg = message;
    }
}

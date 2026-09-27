package com.example.Payment_Service.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final int status;

    public BusinessException(String message) {
        super(message);
        this.status = 400;
    }

    public BusinessException(String message, int status) {
        super(message);
        this.status = status;
    }

}
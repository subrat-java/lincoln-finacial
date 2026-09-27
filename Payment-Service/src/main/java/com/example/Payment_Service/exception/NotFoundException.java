package com.example.Payment_Service.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final Long id;

    public NotFoundException(String message) {
        super(message);
        this.id = null;
    }

    public NotFoundException(String message, Long id) {
        super(message);
        this.id = id;
    }

}
package com.unitel.fms.backend.exceptions.customize;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CommonException {
    public NotFoundException(String message) {
        super(message);
        this.setHttpStatus(HttpStatus.NOT_FOUND);
    }
}

package com.sourceflag.framework.auth.core.exception;

import lombok.Getter;

@Getter
public class ValidationException extends AbstractException {

    public ValidationException(String message, Object... params) {
        this(401, message, params);
    }

    public ValidationException(Integer code, String message, Object... params) {
        super(code, message, params);
    }
}
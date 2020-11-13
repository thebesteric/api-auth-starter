package com.sourceflag.framework.auth.core.exception;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * AbstractException
 *
 * @author Eric Joe
 * @version 1.0
 * @date 2020-11-11 21:39
 * @since 1.0
 */
@Getter
public abstract class AbstractException extends RuntimeException {

    protected final Integer code;
    protected final String message;
    protected final Object[] params;
    protected final long timestamp;

    public AbstractException(Integer code, String message, Object... params) {
        super(code == null ? String.format("ERR_MESSAGE: %s", message) : String.format("ERR_CODE: %d, ERR_MESSAGE: %s", code, message));
        this.code = code;
        this.message = message;
        this.params = params;
        this.timestamp = System.currentTimeMillis();
    }

    public AbstractException(String message, Object... params) {
        this(null, message, params);
    }

    public Object getSimpleInfo() {
        Map<String, Object> object = new HashMap<>();
        object.put("code", code);
        object.put("message", message);
        object.put("timestamp", timestamp);
        return object;
    }

    public Object[] getParams() {
        return Arrays.copyOf(params, params.length);
    }
}

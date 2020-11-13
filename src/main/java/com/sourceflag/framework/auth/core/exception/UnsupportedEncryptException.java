package com.sourceflag.framework.auth.core.exception;

/**
 * UnsupportedEncryptException
 *
 * @author Eric Joe
 * @version 1.0
 * @date 2020-11-11 21:41
 * @since 1.0
 */
public class UnsupportedEncryptException extends AbstractException {
    public UnsupportedEncryptException(String message, Object... params) {
        super(message, params);
    }
}

package com.sourceflag.framework.auth.core.processor.encrypt;

import com.sourceflag.framework.auth.core.exception.ValidationException;
import com.sourceflag.framework.auth.core.processor.EncryptAuthProcessor;
import com.sourceflag.framework.auth.core.wrapper.AuthInfo;
import com.sourceflag.framework.auth.starter.ApiAuthProperties;
import com.sourceflag.framework.auth.utils.EncryptUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * MD5AuthProcessor
 *
 * @author Eric Joe
 * @version 1.0
 * @date 2020-11-11 23:55
 * @since 1.0
 */
@Slf4j
public class MD5EncryptAuthProcessor implements EncryptAuthProcessor {
    @Override
    public void processor(AuthInfo authInfo, ApiAuthProperties properties) throws ValidationException {
        if (!this.validateTimeout(authInfo, properties)) {
            throw new ValidationException("Token has expired");
        }
        String sortedUrl = this.validateRule(authInfo);
        String serverToken = EncryptUtils.md5Hex(sortedUrl);
        if (!authInfo.getToken().getValue().equalsIgnoreCase(serverToken)) {
            throw new ValidationException("Token validation error by MD5");
        }
    }
}

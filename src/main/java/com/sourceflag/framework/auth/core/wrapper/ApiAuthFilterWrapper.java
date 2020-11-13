package com.sourceflag.framework.auth.core.wrapper;

import com.sourceflag.framework.auth.core.processor.EncryptAuthProcessor;
import com.sourceflag.framework.auth.core.processor.encrypt.MD5EncryptAuthProcessor;
import com.sourceflag.framework.auth.core.processor.encrypt.SHA1EncryptAuthProcessor;
import com.sourceflag.framework.auth.core.processor.encrypt.SHA256EncryptAuthProcessor;
import com.sourceflag.framework.auth.starter.ApiAuthProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import javax.servlet.Filter;

/**
 * SwitchLoggerFilterWrapper
 *
 * @author Eric Joe
 * @version 1.0
 * @date 2020-10-01 00:35
 * @since 1.0
 */
public abstract class ApiAuthFilterWrapper implements Filter {

    protected ApiAuthProperties properties;

    protected GenericApplicationContext applicationContext;

    protected EncryptAuthProcessor defaultEncryptAuthProcessor;

    public ApiAuthFilterWrapper(ApiAuthProperties properties, ApplicationContext applicationContext, boolean determine) {
        this.properties = properties;
        this.applicationContext = (GenericApplicationContext) applicationContext;
        if (determine) {
            determineEncryptAuthProcessor();
        }
    }

    private void determineEncryptAuthProcessor() {
        if (ApiAuthProperties.EncryptType.SHA1.name().equalsIgnoreCase(properties.getEncrypt())) {
            defaultEncryptAuthProcessor = new SHA1EncryptAuthProcessor();
        } else if (ApiAuthProperties.EncryptType.SHA256.name().equalsIgnoreCase(properties.getEncrypt())) {
            defaultEncryptAuthProcessor = new SHA256EncryptAuthProcessor();
        } else if (ApiAuthProperties.EncryptType.MD5.name().equalsIgnoreCase(properties.getEncrypt())) {
            defaultEncryptAuthProcessor = new MD5EncryptAuthProcessor();
        }
    }

}

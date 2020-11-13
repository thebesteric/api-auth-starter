package com.sourceflag.framework.auth.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ApiAuthProperties
 *
 * @author Eric Joe
 * @version 1.0
 * @date 2020-11-11 18:20
 * @since 1.0
 */
@Data
@ConfigurationProperties(prefix = "sourceflag.api.auth")
public class ApiAuthProperties {

    public enum EncryptType {
        MD5, SHA1, SHA256
    }

    private Filter filter = new Filter();

    private boolean enable = true;

    private boolean global = true;

    private String name = "token";

    private String encrypt = EncryptType.MD5.name();

    private int timeout = 300;

    @Data
    public static class Filter {
        private String name = "ApiAuthFilter";
        private int order = 1;
        private String[] urlPatterns = {"/*"};

    }

}

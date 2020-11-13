package com.sourceflag.framework.auth.core.wrapper;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.xml.bind.ValidationException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * RequestInfo
 *
 * @author Eric Joe
 * @version 1.0
 * @date 2020-11-11 21:43
 * @since 1.0
 */
@Getter
@Setter
public class AuthInfo {

    private String uri;
    private String url;
    private String body;
    private Map<String, String> params;
    private Long timestamp;
    private Token token;

    public AuthInfo(String tokenKey, ApiAuthRequestWrapper requestWrapper) throws ValidationException {
        this.uri = requestWrapper.getRequestURI();
        this.url = requestWrapper.getRequestURL().toString();
        this.body = requestWrapper.getBody().toString();
        this.params = this.getRequestParams(requestWrapper);
        this.timestamp = params.get("timestamp") == null ? null : Long.parseLong(params.get("timestamp"));
        this.token = new Token(tokenKey, this.params);

    }

    private Map<String, String> getRequestParams(ApiAuthRequestWrapper requestWrapper) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> parameterNames = requestWrapper.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            String parameterValue = requestWrapper.getParameter(parameterName);
            params.put(parameterName, parameterValue);
        }
        return params;
    }

    @Data
    public static class Token {
        private String key;
        private String value;

        public Token(String tokenKey, Map<String, String> params) throws ValidationException {
            if (StringUtils.isEmpty(tokenKey)) {
                throw new ValidationException(tokenKey + " does not exist");
            }
            this.key = tokenKey;
            this.value = params.get(tokenKey);
        }

    }

}

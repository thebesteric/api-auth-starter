package com.sourceflag.framework.auth.core.processor;

import com.sourceflag.framework.auth.core.exception.ValidationException;
import com.sourceflag.framework.auth.core.wrapper.AuthInfo;
import com.sourceflag.framework.auth.starter.ApiAuthProperties;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;

public interface EncryptAuthProcessor {

    void processor(AuthInfo authInfo, ApiAuthProperties properties) throws ValidationException;

    default boolean validateTimeout(AuthInfo authInfo, ApiAuthProperties properties) {
        long currentTimeMillis = System.currentTimeMillis();
        if ((authInfo.getTimestamp() != null && authInfo.getTimestamp() > currentTimeMillis) || (properties.getTimeout() > 0 && authInfo.getTimestamp() == null)) {
            throw new ValidationException("Invalid timestamp, please check timestamp or timeout property");
        }
        return properties.getTimeout() <= 0 || authInfo.getTimestamp() + properties.getTimeout() * 1000 >= currentTimeMillis;
    }

    default String validateRule(AuthInfo authInfo) throws ValidationException {
        String subUrl = authInfo.getUrl().split("\\?")[0];
        Map<String, String> requestParams = authInfo.getParams();
        String body = authInfo.getBody();
        String tokenKey = authInfo.getToken().getKey();

        StringBuilder sortedParams = new StringBuilder();
        if (requestParams != null && requestParams.size() > 0) {
            if (!requestParams.containsKey(tokenKey)) {
                throw new ValidationException(tokenKey + " does not exist");
            }

            // sort by alphabetical order
            String[] keys = new String[requestParams.size()];
            int i = 0;
            for (String key : requestParams.keySet()) {
                keys[i++] = key;
            }
            Arrays.sort(keys);

            // assembly sorted url
            i = 0;
            for (String key : keys) {
                i++;
                if (!authInfo.getToken().getKey().equalsIgnoreCase(key)) {
                    sortedParams.append(key).append("=").append(requestParams.get(key));
                    if (i < keys.length - 1)
                        sortedParams.append("&");
                }
            }

            // check request if has body
            if (!StringUtils.isEmpty(body)) {
                sortedParams.append(body);
            }
        }
        return subUrl + "?" + sortedParams.toString();
    }

}

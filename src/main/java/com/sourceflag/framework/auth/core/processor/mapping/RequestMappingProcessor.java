package com.sourceflag.framework.auth.core.processor.mapping;

import com.sourceflag.framework.auth.core.ApiAuthInitialization;
import com.sourceflag.framework.auth.core.processor.MappingProcessor;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

/**
 * RequestMappingProcessor
 *
 * @author Eric Joe
 * @version 1.0
 * @date 2020-10-01 02:25
 * @since 1.0
 */
public class RequestMappingProcessor implements MappingProcessor {

    private Method method;

    @Override
    public boolean supports(Method method) {
        this.method = method;
        return method.getAnnotation(RequestMapping.class) != null;
    }

    @Override
    public void processor(String[] classRequestMappingUrls) {
        if (classRequestMappingUrls != null && classRequestMappingUrls.length > 0) {
            for (String classRequestMappingUrl : classRequestMappingUrls) {
                String[] methodRequestMappingUrls = method.getAnnotation(RequestMapping.class).value();
                for (String methodRequestMappingUrl : methodRequestMappingUrls) {
                    String url = classRequestMappingUrl + methodRequestMappingUrl;
                    ApiAuthInitialization.URL_MAPPING.put(url, method);
                }
            }
        }
    }
}

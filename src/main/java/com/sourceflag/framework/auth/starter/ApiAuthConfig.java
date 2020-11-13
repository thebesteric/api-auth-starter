package com.sourceflag.framework.auth.starter;

import com.sourceflag.framework.auth.core.ApiAuthFilter;
import com.sourceflag.framework.auth.core.ApiAuthInitialization;
import com.sourceflag.framework.auth.core.processor.EncryptAuthProcessor;
import com.sourceflag.framework.auth.core.processor.MappingProcessor;
import com.sourceflag.framework.auth.core.processor.mapping.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * ApiAuthConfig
 *
 * @author Eric Joe
 * @version 1.0
 * @date 2020-11-11 23:58
 * @since 1.0
 */
@Configuration
@Import(ApiAuthInitialization.class)
@EnableAsync
@ConditionalOnBean(ApiAuthMarker.class)
@EnableConfigurationProperties(ApiAuthProperties.class)
public class ApiAuthConfig {

    @Bean(name = "apiAuthFilterRegister")
    @SuppressWarnings({"unchecked", "rawtypes"})
    public FilterRegistrationBean filterRegister(ApiAuthProperties properties, ApplicationContext applicationContext, @Nullable EncryptAuthProcessor encryptAuthProcessor) {
        FilterRegistrationBean frBean = new FilterRegistrationBean();
        frBean.setName(properties.getFilter().getName());
        frBean.setOrder(properties.getFilter().getOrder());
        frBean.addUrlPatterns(properties.getFilter().getUrlPatterns());
        if (encryptAuthProcessor == null) {
            frBean.setFilter(new ApiAuthFilter(properties, applicationContext));
        } else {
            frBean.setFilter(new ApiAuthFilter(properties, applicationContext, encryptAuthProcessor));
        }
        return frBean;
    }

    // MappingProcessor

    @Bean(name = "apiAuthRequestMappingProcessor")
    public MappingProcessor requestMappingProcessor() {
        return new RequestMappingProcessor();
    }

    @Bean(name = "apiAuthDeleteMappingProcessor")
    public MappingProcessor deleteMappingProcessor() {
        return new DeleteMappingProcessor();
    }

    @Bean(name = "apiAuthGetMappingProcessor")
    public MappingProcessor getMappingProcessor() {
        return new GetMappingProcessor();
    }

    @Bean(name = "apiAuthPatchMappingProcessor")
    public MappingProcessor patchMappingProcessor() {
        return new PatchMappingProcessor();
    }

    @Bean(name = "apiAuthPostMappingProcessor")
    public MappingProcessor postMappingProcessor() {
        return new PostMappingProcessor();
    }

    @Bean(name = "apiAuthPutMappingProcessor")
    public PutMappingProcessor putMappingProcessor() {
        return new PutMappingProcessor();
    }

}

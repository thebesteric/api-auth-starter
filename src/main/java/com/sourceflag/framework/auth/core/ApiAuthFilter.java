package com.sourceflag.framework.auth.core;

import com.sourceflag.framework.auth.core.exception.UnsupportedEncryptException;
import com.sourceflag.framework.auth.core.exception.ValidationException;
import com.sourceflag.framework.auth.core.processor.EncryptAuthProcessor;
import com.sourceflag.framework.auth.core.wrapper.ApiAuthFilterWrapper;
import com.sourceflag.framework.auth.core.wrapper.ApiAuthRequestWrapper;
import com.sourceflag.framework.auth.core.wrapper.ApiAuthResponseWrapper;
import com.sourceflag.framework.auth.core.wrapper.AuthInfo;
import com.sourceflag.framework.auth.starter.ApiAuthProperties;
import com.sourceflag.framework.auth.utils.JsonUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * SwitchLoggerFilter
 *
 * @author Eric Joe
 * @version 1.0
 * @date 2020-09-29 23:15
 * @since 1.0
 */
@Slf4j
public class ApiAuthFilter extends ApiAuthFilterWrapper {

    private EncryptAuthProcessor encryptAuthProcessor;

    public ApiAuthFilter(ApiAuthProperties properties, ApplicationContext applicationContext) {
        super(properties, applicationContext, true);
    }

    public ApiAuthFilter(ApiAuthProperties properties, ApplicationContext applicationContext, EncryptAuthProcessor encryptAuthProcessor) {
        super(properties, applicationContext, false);
        this.encryptAuthProcessor = encryptAuthProcessor;
    }

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // check enable
        if (!properties.isEnable()) {
            filterChain.doFilter(request, response);
            return;
        }

        // check ignore uri
        String requestURI = ((HttpServletRequest) request).getRequestURI();
        for (String ignoreUri : ApiAuthInitialization.IGNORE_URIS) {
            if (requestURI.startsWith(ignoreUri)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // wrapper request & response
        ApiAuthRequestWrapper requestWrapper = new ApiAuthRequestWrapper((HttpServletRequest) request);
        ApiAuthResponseWrapper responseWrapper = new ApiAuthResponseWrapper((HttpServletResponse) response);

        // package AuthInfo
        AuthInfo authInfo = new AuthInfo(properties.getName(), requestWrapper);

        // 判断鉴权是否通过，没有通过则抛出异常并写回浏览器，否则执行过滤器链
        String errMsg = null;
        try {
            encryptAuthProcessor = defaultEncryptAuthProcessor != null ? defaultEncryptAuthProcessor : encryptAuthProcessor;
            encryptAuthProcessor.processor(authInfo, properties);
            filterChain.doFilter(request, response);
        } catch (ValidationException ex) {
            errMsg = JsonUtils.mapper.writeValueAsString(ex.getSimpleInfo());
        }
        ServletOutputStream out = response.getOutputStream();
        out.write(errMsg != null ? errMsg.getBytes() : responseWrapper.getByteArray());
        out.flush();
    }

}

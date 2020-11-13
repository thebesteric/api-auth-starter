package com.sourceflag.framework.auth.core.wrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sourceflag.framework.auth.utils.JsonUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ApiAuthRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    public ApiAuthRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        try (BufferedReader reader = request.getReader()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            body = sb.toString().getBytes();
        }
    }

    public Object getBody() {
        if (this.body != null) {
            String content = new String(this.body, StandardCharsets.UTF_8).trim();
            try {
                return JsonUtils.mapper.readValue(content, Map.class);
            } catch (JsonProcessingException e) {
                return content;
            }
        }
        return null;
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }
}

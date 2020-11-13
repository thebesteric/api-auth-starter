package com.sourceflag.framework.auth.core.wrapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ApiAuthResponseWrapper extends HttpServletResponseWrapper {

    private ByteArrayOutputStream buffer;
    private ServletOutputStream out;

    public ApiAuthResponseWrapper(HttpServletResponse response) {
        super(response);
        buffer = new ByteArrayOutputStream();
        out = new WrapperOutputStream(buffer);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return out;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (out != null) out.flush();
    }

    public void setBuffer(String message) throws IOException {
        buffer.reset();
        buffer.write(message.getBytes());
    }

    public byte[] getByteArray() throws IOException {
        flushBuffer();
        return buffer.toByteArray();
    }

    private static class WrapperOutputStream extends ServletOutputStream {

        private ByteArrayOutputStream byteArrayOutputStream;

        public WrapperOutputStream(ByteArrayOutputStream byteArrayOutputStream) {
            this.byteArrayOutputStream = byteArrayOutputStream;
        }

        @Override
        public void write(int b) throws IOException {
            byteArrayOutputStream.write(b);
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }
}

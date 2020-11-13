package com.sourceflag.framework.auth.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * EncryptUtils
 *
 * @author Eric Joe
 * @version 1.0
 * @date 2020-11-12 16:26
 * @since 1.0
 */
public class EncryptUtils {

    public static String base64Encode(String data) {
        return Base64.encodeBase64String(data.getBytes());
    }

    public static String base64Decode(String data) {
        return new String(Base64.decodeBase64(data.getBytes()), StandardCharsets.UTF_8);
    }

    public static String md5Hex(String data) {
        return DigestUtils.md5Hex(data);
    }

    public static String sha1Hex(String data) {
        return DigestUtils.sha1Hex(data);
    }

    public static String sha256Hex(String data) {
        return DigestUtils.sha256Hex(data);
    }

}

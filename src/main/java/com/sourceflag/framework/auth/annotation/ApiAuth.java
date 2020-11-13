package com.sourceflag.framework.auth.annotation;

import org.springframework.core.annotation.AliasFor;

public @interface ApiAuth {

    @AliasFor("required")
    boolean value() default true;

    @AliasFor("value")
    boolean required() default true;

    String encrypt() default "MD5";

    int timeout() default 300;

}

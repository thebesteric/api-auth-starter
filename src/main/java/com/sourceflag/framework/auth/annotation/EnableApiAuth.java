package com.sourceflag.framework.auth.annotation;

import com.sourceflag.framework.auth.starter.ApiAuthMarker;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ApiAuthMarker.class)
public @interface EnableApiAuth {
}

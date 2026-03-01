package org.musicservice.demo.support.factory.it.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserPrincipalSecurityContextFactory.class)
public @interface WithMockUserPrincipal {
    long userId() default 1L;
    String username() default "username";
    String password() default "defaultPassword";
    boolean accountNonLocked() default true;
    boolean accountEnabled() default true;
}

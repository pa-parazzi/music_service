package org.musicservice.demo.support.factory.it.security;

import org.musicservice.demo.entity.user.Authority;
import org.musicservice.demo.security.userDetails.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithMockUserPrincipalSecurityContextFactory implements WithSecurityContextFactory<WithMockUserPrincipal> {

    @Override
    public SecurityContext createSecurityContext(WithMockUserPrincipal annotation) {
        UserPrincipal principal = new UserPrincipal(
                annotation.userId(),
                annotation.username(),
                annotation.password(),
                annotation.accountNonLocked(),
                annotation.accountEnabled(),
                List.of(Authority.USER)
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        return securityContext;
    }
}

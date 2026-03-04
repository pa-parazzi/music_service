package org.musicservice.demo.support.factory.it.security;

import org.musicservice.demo.entity.user.Authority;
import org.musicservice.demo.security.userDetails.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class WithMockUserPrincipalSecurityContextFactory implements WithSecurityContextFactory<WithMockUserPrincipal> {

    @Override
    public SecurityContext createSecurityContext(WithMockUserPrincipal annotation) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(annotation.userId(), null, List.of(Authority.USER));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        return securityContext;
    }
}

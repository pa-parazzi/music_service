package org.musicservice.demo.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.musicservice.demo.security.jwt.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    @Autowired
    public JwtFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal
            (@Nonnull HttpServletRequest request,
             @Nonnull HttpServletResponse response,
             @Nonnull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authHeader!=null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")){
            String jwtToken = authHeader.substring(7);
            if(!jwtToken.isBlank()){
                try{
                    DecodedJWT jwt = jwtTokenService.validateToken(jwtToken);
                    Long userId = Long.valueOf(jwt.getSubject());
                    List<SimpleGrantedAuthority> authorities =
                            jwt.getClaim("roles").asList(String.class).stream().map(SimpleGrantedAuthority::new).toList();
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }catch (JWTVerificationException e){
                    SecurityContextHolder.clearContext();
                }
            }
        } else if(request.getRequestURI().equals("/api/auth/refresh")){
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }
}

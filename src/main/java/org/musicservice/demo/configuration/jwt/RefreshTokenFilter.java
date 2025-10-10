package org.musicservice.demo.configuration.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.musicservice.demo.model.user.RefreshToken;
import org.musicservice.demo.security.jwtAuthentication.cookie.CookieUtil;
import org.musicservice.demo.security.jwtAuthentication.jwt.JWTUtil;
import org.musicservice.demo.security.jwtAuthentication.refreshToken.RefreshTokenUtil;
import org.musicservice.demo.service.security.RefreshTokenService;
import org.musicservice.demo.service.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public RefreshTokenFilter(JWTUtil jwtUtil, RefreshTokenService refreshTokenService, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String refreshTokenByCookie = CookieUtil.getRefreshTokenByCookie(request);
        if(refreshTokenByCookie==null){
            filterChain.doFilter(request, response);
            return;
        }
        Optional<RefreshToken> foundToken = refreshTokenService.getOptTokenByHash(RefreshTokenUtil.hash(refreshTokenByCookie));
        if(foundToken.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }
        String username = foundToken.get().getUser().getUsername();
        String jwtToken = jwtUtil.generateToken(username);
        System.out.println(jwtToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        if(SecurityContextHolder.getContext().getAuthentication()==null){
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        response.setHeader("Authorization", "Bearer " + jwtToken);
        filterChain.doFilter(request, response);
    }
}

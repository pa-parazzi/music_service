package org.musicservice.demo.security.filter;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.musicservice.demo.security.cookie.CookieUtil;
import org.musicservice.demo.security.dto.TokenSubject;
import org.musicservice.demo.security.jwt.JwtTokenService;
import org.musicservice.demo.security.refreshToken.RefreshTokenProjection;
import org.musicservice.demo.security.refreshToken.RefreshTokenService;
import org.musicservice.demo.security.refreshToken.RefreshTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public RefreshTokenFilter(JwtTokenService jwtTokenService, RefreshTokenService refreshTokenService) {
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().equals("/api/auth/refresh")){
            String refreshTokenByCookie = CookieUtil.getRefreshTokenByCookie(request);
            if(refreshTokenByCookie!=null){
                String hash = RefreshTokenUtil.hash(refreshTokenByCookie);
                Optional<RefreshTokenProjection> foundToken = refreshTokenService.getOptTokenByHash(hash);
                if(foundToken.isPresent()){
                    RefreshTokenProjection refreshToken = foundToken.get();
                    if(refreshTokenService.isExpired(refreshToken.getExpiryDate())){
                        refreshTokenService.delete(request, response);
                        refreshTokenService.create(response, refreshToken.getUserId(), refreshToken.getRole());
                    }
                    TokenSubject tokenSubject = new TokenSubject(refreshToken.getUserId(), List.of(refreshToken.getRole().getAuthority()));
                    String jwtToken = jwtTokenService.generateToken(tokenSubject);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"accessToken\": \"" + jwtToken + "\"}");
                    response.getWriter().flush();
                    return;
                }
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"invalid refresh token\"}");
            response.getWriter().flush();
        }
        filterChain.doFilter(request, response);
    }
}

package org.musicservice.demo.configuration.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.musicservice.demo.security.jwtAuthentication.jwt.JWTUtil;
import org.musicservice.demo.service.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwt;
    private final UserDetailsServiceImpl service;

    @Autowired
    public JWTFilter(JWTUtil jwt, UserDetailsServiceImpl service) {
        this.jwt = jwt;
        this.service = service;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if(authHeader!=null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")){
            String jwtToken = authHeader.substring(7);
            if(jwtToken.isBlank()){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Не верный формат JWT-токена");
            }else{
                try{
                    String username = jwt.validateToken(jwtToken);
                    UserDetails userDetails = service.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    if(SecurityContextHolder.getContext().getAuthentication()==null){
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }catch (JWTVerificationException e){
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Не валидный формат токена");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}

package org.musicservice.demo.security.config;

import org.musicservice.demo.security.filter.JWTFilter;
import org.musicservice.demo.security.filter.RefreshTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // конфиг класс для spring security
public class SecurityConfig {


    private final JWTFilter jwtFilter;
    private final RefreshTokenFilter refreshTokenFilter;

    @Autowired
    public SecurityConfig(JWTFilter jwtFilter, RefreshTokenFilter refreshTokenFilter) {
        this.jwtFilter = jwtFilter;
        this.refreshTokenFilter = refreshTokenFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth-> auth
                        .requestMatchers("/css/**", "/js/**", "/auth/**", "/music/**").permitAll()
                        .requestMatchers("/admin/main", "/admin/upload", "/admin/main.html").permitAll()
                        .requestMatchers("/api/auth/refresh").permitAll()
                        .requestMatchers("/album/**", "/api/album/**", "/artist/**", "/api/artist/**", "/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                        //.requestMatchers("/api/auth/activate").permitAll()
                        .requestMatchers("/album/like/create", "/album/like/delete", "/album/like/get").permitAll()
                        .requestMatchers("/sound/like/create", "/sound/like/delete", "/sound/like/get").permitAll()
                        .requestMatchers("/collection/tracks", "/collection/albums").permitAll()
                        .requestMatchers("/lk/profile").authenticated()
                        .anyRequest().denyAll())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(refreshTokenFilter, JWTFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}

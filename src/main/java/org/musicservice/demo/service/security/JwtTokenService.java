package org.musicservice.demo.service.security;

import org.musicservice.demo.model.user.User;
import org.musicservice.demo.security.jwtAuthentication.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class JwtTokenService {

    private final JWTUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public JwtTokenService(JWTUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    public String generateAccess(String username){
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtUtil.generateToken(userDetails);
    }

    public String generateAccessForUser(User user){
        return generateAccess(user.getUsername());
    }
}

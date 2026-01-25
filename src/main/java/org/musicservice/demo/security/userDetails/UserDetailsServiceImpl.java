package org.musicservice.demo.security.userDetails;

import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.security.util.UserPrincipalMapper;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService, UserPrincipalService {

    private final UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.searchByUsernameWithAvatar(username);
        return UserPrincipalMapper.from(user);
    }

    @Override
    public UserPrincipal loadPrincipalById(Long id) {
        User user = userService.searchByIdWithAvatar(id);
        return UserPrincipalMapper.from(user);
    }
}

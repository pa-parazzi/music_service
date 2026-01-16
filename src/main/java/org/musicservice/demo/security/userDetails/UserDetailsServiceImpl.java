package org.musicservice.demo.security.userDetails;

import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.util.UserPrincipalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService, UserPrincipalService {

    private final UserRepository repository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.searchByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return UserPrincipalMapper.from(user);
    }

    @Override
    public UserPrincipal loadPrincipalById(Long id) {
        User user = repository.searchById(id).orElseThrow(() -> new UsernameNotFoundException("Not found user with id= " + id));
        return UserPrincipalMapper.from(user);
    }
}

package org.musicservice.demo.service.security;

import org.musicservice.demo.security.DetailsForUser;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DetailsServiceForUser implements UserDetailsService {

    private final UserRepository repository;

    @Autowired
    public DetailsServiceForUser(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repository.searchByUsername(username);
        if(user.isEmpty()){
            throw new UsernameNotFoundException("Пользователя с таким логином не существует");
        }
        return new DetailsForUser(user.get());
    }

}

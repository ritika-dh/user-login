package org.paf.user_login.service;

import org.paf.user_login.model.User;
import org.paf.user_login.model.UserPrincipal;
import org.paf.user_login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findById(username).orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
        return new UserPrincipal(user);
    }
}

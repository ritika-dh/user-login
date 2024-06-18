package org.paf.user_login.service;

import org.paf.user_login.model.User;
import org.paf.user_login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository repository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    public void register(String userName, String password) {
        System.out.println("inside register method");
        User user = new User(userName, bCryptPasswordEncoder.encode(password));
        repository.save(user);
    }
}

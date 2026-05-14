package com.Rentals.app.service;

import com.Rentals.app.model.User;
import com.Rentals.app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncorder;
    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncorder = passwordEncoder;
    }
    public void register(String username, String email, String rawPassword){
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncorder.encode(rawPassword));
        user.setRole("USER");
        userRepository.save(user);

    }
}

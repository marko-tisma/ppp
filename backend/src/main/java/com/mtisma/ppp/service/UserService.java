package com.mtisma.ppp.service;

import com.mtisma.ppp.model.RegisterRequest;
import com.mtisma.ppp.model.Role;
import com.mtisma.ppp.model.User;
import com.mtisma.ppp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> register(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return Optional.empty();
        }
        User user = User.builder()
            .username(registerRequest.getUsername())
            .password(passwordEncoder.encode(registerRequest.getPassword()))
            .enabled(true)
            .role(Role.ROLE_USER)
            .build();

        return Optional.of(userRepository.save(user));
    }
}

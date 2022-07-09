package com.mtisma.ppp.service;

import com.mtisma.ppp.config.JwtTokenUtil;
import com.mtisma.ppp.model.LoginRequest;
import com.mtisma.ppp.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;

    public LoginService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public Optional<User> login(LoginRequest loginRequest) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            User user = (User) authenticate.getPrincipal();
            user.setJwtToken(JwtTokenUtil.generateToken(user));
            return Optional.of(user);
        } catch (BadCredentialsException ex) {
            return Optional.empty();
        }
    }
}

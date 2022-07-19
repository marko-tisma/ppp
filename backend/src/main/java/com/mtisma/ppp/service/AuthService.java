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
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public Optional<User> login(LoginRequest loginRequest) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            User user = (User) authenticate.getPrincipal();
            user.setJwtToken(jwtTokenUtil.generateToken(user));
            return Optional.of(user);
        } catch (BadCredentialsException ex) {
            return Optional.empty();
        }
    }

    public Optional<String> refreshToken(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            return Optional.of(jwtTokenUtil.generateToken(user));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

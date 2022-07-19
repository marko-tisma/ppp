package com.mtisma.ppp.controller;

import com.mtisma.ppp.model.LoginRequest;
import com.mtisma.ppp.model.User;
import com.mtisma.ppp.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api/v1/login")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService loginService) {
        this.authService = loginService;
    }

    @PostMapping
    public User login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    @GetMapping("/refresh")
    public String refreshToken(Authentication authentication) {
        return authService.refreshToken(authentication)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

}

package com.mtisma.ppp.controller;

import com.mtisma.ppp.model.LoginRequest;
import com.mtisma.ppp.model.User;
import com.mtisma.ppp.service.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("api/v1/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    public User login(@RequestBody LoginRequest loginRequest) {
        return loginService.login(loginRequest)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

}

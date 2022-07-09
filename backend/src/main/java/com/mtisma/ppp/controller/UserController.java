package com.mtisma.ppp.controller;

import com.mtisma.ppp.model.LoginRequest;
import com.mtisma.ppp.model.RegisterRequest;
import com.mtisma.ppp.model.User;
import com.mtisma.ppp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User register(@RequestBody RegisterRequest registerRequest) {
        Optional<User> user = this.userService.register(registerRequest);
        return user
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }

}

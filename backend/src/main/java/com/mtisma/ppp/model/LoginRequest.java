package com.mtisma.ppp.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {

    @NotBlank(message = "Username must be provided")
    private String username;

    @NotBlank(message = "Password must be provided")
    private String password;
}

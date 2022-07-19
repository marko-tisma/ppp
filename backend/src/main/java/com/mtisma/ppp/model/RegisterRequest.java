package com.mtisma.ppp.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username must be provided")
    @Size(max = 20)
    private String username;

    @NotBlank(message = "Password must be provided")
    private String password;
}


package com.shop.Main.payload.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
public class RegistrationRequest {

    @NotNull
    @Size(min = 3, max = 50, message = "Username must be at least 3 chars length")
    private String username;

    @NotBlank
    @Size(min = 3, max = 100, message = "Email must be at least 3 chars length")
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 3 chars length")
    private String password;

    private Set<String> roles;
}

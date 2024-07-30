package com.ynshb.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {
    @NotEmpty(message = "Firstname is required")
    private String firstname;
    @NotEmpty(message = "Lastname is required")
    private String lastname;
    @NotEmpty(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    @NotEmpty(message = "Password is required")
    @Size(min = 8, max = 24, message = "Password must be at least 8 characters long")
    private String password;
}

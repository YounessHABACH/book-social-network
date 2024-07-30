package com.ynshb.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Firstname must be not blank")
    private String firstname;
    @NotEmpty(message = "Lastname is required")
    @NotBlank(message = "Lastname must be not blank")
    private String lastname;
    @NotEmpty(message = "Email is required")
    @NotBlank(message = "Email must be not blank")
    @Email(message = "Email must be valid")
    private String email;
    @NotEmpty(message = "Password is required")
    @NotBlank(message = "Password must be not blank")
    @Size(min = 8, max = 24, message = "Password must be at least 8 characters long")
    private String password;
}

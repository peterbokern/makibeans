package com.makibeans.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO used to request a password reset link via email.
 */
@Data
public class PasswordResetRequestDTO {

    @Email(message = "Email must be valid.")
    @NotBlank(message = "Email cannot be blank.")
    private String email;
}

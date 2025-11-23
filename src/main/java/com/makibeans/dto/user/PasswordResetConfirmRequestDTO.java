package com.makibeans.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO used to confirm a password reset by providing a valid token.
 */
@Data
public class PasswordResetConfirmRequestDTO {

    @NotBlank(message = "Token cannot be blank.")
    private String token;

    @NotBlank(message = "New password cannot be blank.")
    private String newPassword;
}

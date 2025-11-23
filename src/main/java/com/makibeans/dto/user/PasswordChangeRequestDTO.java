package com.makibeans.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO used by a user to change their own password.
 */
@Data
public class PasswordChangeRequestDTO {

    @NotBlank(message = "Current password cannot be blank.")
    private String currentPassword;

    @NotBlank(message = "New password cannot be blank.")
    private String newPassword;
}

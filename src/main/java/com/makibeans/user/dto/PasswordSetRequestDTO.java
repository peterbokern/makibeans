package com.makibeans.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO used by admins to set or reset a user's password directly.
 */
@Data
public class PasswordSetRequestDTO {

    @NotBlank(message = "Password cannot be blank.")
    private String password;
}

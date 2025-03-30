package com.makibeans.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user updates.
 * Contains the username, email, and password.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters.")
    private String username;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must be at most 255 characters long.")
    private String email;

    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters.")
    private String password;
}

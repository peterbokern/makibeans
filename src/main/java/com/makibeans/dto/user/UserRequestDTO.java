package com.makibeans.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user requests.
 * Contains the username, email, and password.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "Username cannot be blank.")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters.")
    private String username;

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must be at most 255 characters long.")
    private String email;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters.")
    private String password;
}

package com.makibeans.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user login responses.
 * Contains the username, email, and JWT token.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String username;
    private String email;
    private String token;
}

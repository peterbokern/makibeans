package com.makibeans.user.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.makibeans.audit.dto.AuditableResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO for user responses.
 * Contains the id, username, email, and roles.
 */

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "username", "email", "roles", "createdBy", "createdAt", "updatedBy", "updatedAt"})
public class UserResponseDTO extends AuditableResponseDTO {
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
}

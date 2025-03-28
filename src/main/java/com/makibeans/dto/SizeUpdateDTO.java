package com.makibeans.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for updating size information.
 * Contains the name of the size.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SizeUpdateDTO {
    @Size(min = 1, max = 50, message = "Size must be between 1 and 50 characters.")
    private String name;
}

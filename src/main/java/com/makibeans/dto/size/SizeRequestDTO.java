package com.makibeans.dto.size;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Data Transfer Object for Size requests.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SizeRequestDTO {
    @NotBlank(message = "Size cannot be blank.")
    @Size(min = 1, max = 50, message = "Size must be between 1 and 50 characters.")
    private String name;
}

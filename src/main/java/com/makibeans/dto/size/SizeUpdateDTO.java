package com.makibeans.dto.size;

import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Data Transfer Object for updating a Size.
 * Contains a field for the size name.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SizeUpdateDTO {
    @Size(min = 1, max = 50, message = "Size must be between 1 and 50 characters.")
    private String name;
}

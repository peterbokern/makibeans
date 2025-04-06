package com.makibeans.dto.size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Size responses.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SizeResponseDTO {
    Long id;
    String name;
}

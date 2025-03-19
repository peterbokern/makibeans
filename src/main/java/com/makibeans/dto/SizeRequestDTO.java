package com.makibeans.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SizeRequestDTO {
    @NotBlank(message = "Size cannot be blank.")
    private String name;
}

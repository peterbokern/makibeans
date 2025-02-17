package com.makibeans.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SizeUpdateDTO {
    @NotBlank(message = "Size cannot be blank.")
    private String name;
}

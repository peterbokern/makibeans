package com.makibeans.dto;

import lombok.Builder;

@Builder
public record ImageUploadResponseDTO(String message, String originalFilename, String fileType) {}


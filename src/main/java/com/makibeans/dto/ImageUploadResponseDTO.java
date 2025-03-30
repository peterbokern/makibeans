package com.makibeans.dto;

import lombok.Builder;

/**
 * Data Transfer Object for ImageUploadResponse.
 */

@Builder
public record ImageUploadResponseDTO(String message, String originalFilename, String fileType) {}


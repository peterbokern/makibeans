package com.makibeans.util;

import com.makibeans.config.ImageConfig;
import com.makibeans.exceptions.ImageProcessingException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
public class ImageUtils {

    private final List<String> supportedImageTypes;

    public ImageUtils(ImageConfig imageConfig) {
        this.supportedImageTypes = imageConfig.getSupportedImageTypes();
    }

    /**
     * Validates the provided image file and extracts its byte array.
     *
     * @param image the MultipartFile representing the image to be validated and extracted
     * @return a byte array containing the image data
     * @throws ImageProcessingException if the image is null, empty, unsupported type, or fails to be read
     */

    public byte[] validateAndExtractImageBytes(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ImageProcessingException("Image file is empty or null.");
        }

        String contentType = image.getContentType();
        if (contentType == null || !supportedImageTypes.contains(contentType)) {
            throw new ImageProcessingException("Unsupported image type: " + contentType +
                    ". Supported types: " + supportedImageTypes);
        }

        try {
            return image.getBytes();
        } catch (IOException e) {
            throw new ImageProcessingException("Failed to read image file '" + image.getOriginalFilename() + "'", e);
        }
    }
}

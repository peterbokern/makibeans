package com.makibeans.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for image-related properties.
 * This class loads the supported image types from the application properties.
 */

@Configuration
@Getter
public class ImageConfig {

    @Value("#{'${supported.image.types}'.split(',')}")
    private List<String> supportedImageTypes;

}
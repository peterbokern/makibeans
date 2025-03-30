package com.makibeans.config;

import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Web configuration class for customizing the DispatcherServlet and enabling multipart support.
 * This configuration is made custom instead of using the default to:
 * 1. Enable throwing exceptions if no handler is found.
 * 2. Configure multipart support for file uploads with specific size limits.
 */

@Configuration
public class WebConfig {

    @Bean
    public ServletRegistrationBean<DispatcherServlet> dispatcherServletRegistration(DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean<DispatcherServlet> registration = new ServletRegistrationBean<>(dispatcherServlet);
        registration.addInitParameter("throwExceptionIfNoHandlerFound", "true");

        // multipart support - needed for file uploads
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(10));
        factory.setMaxRequestSize(DataSize.ofMegabytes(10));
        registration.setMultipartConfig(factory.createMultipartConfig());

        return registration;
    }
    @Bean
    public DispatcherServletPath dispatcherServletPath() {
        return () -> "/";
    }
}

package com.makibeans.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Entry point for handling unauthorized access attempts.
 * This class is used to send a JSON response with an error message
 * when an authentication exception occurs.
 */

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Handles the commencement of an authentication scheme.
     * This method is called when an authentication exception is thrown.
     *
     * @param request       the HttpServletRequest being processed
     * @param response      the HttpServletResponse being created
     * @param authException the exception that caused the invocation
     * @throws IOException      if an input or output error occurs
     * @throws ServletException if a servlet-specific error occurs
     */

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final String message;
        final String tokenHeader = request.getHeader("Authorization");

        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            message = "Missing or invalid Authorization header.";
        } else {
            message = "Invalid or expired token.";
        }

        PrintWriter writer = response.getWriter();
        writer.println("{ \"error\": \"" + message + "\" }");
    }
}

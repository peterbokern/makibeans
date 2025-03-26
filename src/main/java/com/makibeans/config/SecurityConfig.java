package com.makibeans.config;

import com.makibeans.security.JwtAuthenticationEntryPoint;
import com.makibeans.security.JwtAuthenticationFilter;
import com.makibeans.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class for setting up Spring Security.
 * Configures authentication, authorization, and security filters.
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl userDetailService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, @Lazy UserDetailsServiceImpl userDetailService, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailService = userDetailService; //lazy loaded to prevent circular reference
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    /**
     * Bean for password encoding using BCrypt.
     *
     * @return PasswordEncoder instance
     */

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean for configuring the AuthenticationManager.
     *
     * @param http HttpSecurity instance
     * @return AuthenticationManager instance
     * @throws Exception if an error occurs during configuration
     */

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder
                .userDetailsService(userDetailService)
                .passwordEncoder(passwordEncoder());
        return authManagerBuilder.build();
    }

    /**
     * Configures the security filter chain.
     *
     * @param http HttpSecurity instance
     * @return SecurityFilterChain instance
     * @throws Exception if an error occurs during configuration
     */

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults()) //use GlobalCorsConfiguration
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        // Publicly accessible endpoints
                        .requestMatchers(HttpMethod.GET,
                                "/products/**",
                                "/categories/**",
                                "/attribute-templates/**",
                                "/attribute-values/**",
                                "/product-variants/**",
                                "/product-attributes",
                                "/sizes/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST, "/users").permitAll()

                        // Auth endpoints
                        .requestMatchers("/auth/**").permitAll()

                        // Admin-only for modifying resources
                        .requestMatchers(
                                "/users/admin/**", // Admin registration
                                "/categories/**",
                                "/attribute-templates/**",
                                "/attribute-values/**",
                                "/product-variants/**",
                                "/sizes/**",
                                "/products/**",
                                "/users/**"
                        ).hasRole("ADMIN")

                        // User endpoints
                        .requestMatchers("/users/me", "/users/{id}").authenticated()

                        .anyRequest().denyAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

package com.makibeans.service;

import com.makibeans.exceptions.ResourceNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service implementation for loading user-specific data for authentication.
 * Used by Spring Security to fetch user details from the database.
 */

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    /**
     * Constructs a new UserDetailsServiceImpl with the given UserRepository.
     *
     * @param userService the service to retrieve user data from
     */

    public UserDetailsServiceImpl(@Lazy UserService userService) {
        this.userService = userService;
    }

    /**
     * Loads a user by username. This method is called by Spring Security during authentication.
     *
     * @param username the username of the user to load
     * @return the UserDetails containing user information and authorities
     * @throws UsernameNotFoundException if the user with the given username is not found
     */

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return userService.findByUserName(username);
        } catch (ResourceNotFoundException ex) {
            throw new UsernameNotFoundException(ex.getMessage());
        }
    }
}

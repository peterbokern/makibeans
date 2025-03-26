package com.makibeans.service;

import com.makibeans.dto.*;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.filter.SearchFilter;
import com.makibeans.mapper.UserMapper;
import com.makibeans.model.Role;
import com.makibeans.model.Size;
import com.makibeans.model.User;
import com.makibeans.repository.UserRepository;
import com.makibeans.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Service class for managing User entities.
 * Provides methods to perform CRUD operations and custom queries on User data.
 */

@Service
public class UserService extends AbstractCrudService<User, Long> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleService roleService;

    @Autowired
    public UserService(JpaRepository<User, Long> repository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder, UserMapper userMapper, RoleService roleService, JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsServiceImpl) {
        super(repository);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.roleService = roleService;
    }

    /**
     * Retrieves a User by their username.
     *
     * @param userName the username of the user to retrieve.
     * @return the User entity.
     * @throws ResourceNotFoundException if the user with the given username does not exist.
     */

    @Transactional(readOnly = true)
    public User findByUserName(String userName) {
        return userRepository.findByUsername(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + userName + " not found."));
    }

    /**
     * Retrieves a User by their ID.
     *
     * @param id the ID of the user to retrieve.
     * @return the UserResponseDTO representing the user.
     * @throws ResourceNotFoundException if the user with the given ID does not exist.
     */

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = findById(id);
        return userMapper.toResponseDTO(user);
    }

    /**
     * Retrieves all users.
     *
     * @return a list of UserResponseDTO representing all users.
     */

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    /**
     * Retrieves a list of users based on the provided search parameters.
     * Searchable fields: name, username, email, role.
     *
     * @param searchParams a map of search parameters to filter the users.
     * @return a list of UserResponseDTO representing the matched users.
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findBySearchQuery(Map<String, String> searchParams) {

        Map<String, Function<User, String>> searchFields = Map.of(
                "name", User::getUsername,
                "username", User::getUsername,
                "email", User::getEmail,
                "role", user -> user.getRoles().toString());


        Map<String, Comparator<User>> sortFields = Map.of(
                "id", Comparator.comparing(User::getId, Comparator.nullsLast(Comparator.naturalOrder())),
                "userName", Comparator.comparing(User::getUsername, String.CASE_INSENSITIVE_ORDER),
                "email", Comparator.comparing(User::getEmail, String.CASE_INSENSITIVE_ORDER),
                "role", Comparator.comparing(User::getUsername, String.CASE_INSENSITIVE_ORDER));

        List<User> matchedUsers = SearchFilter.apply(
                findAll(),
                searchParams,
                searchFields,
                sortFields);

        return matchedUsers.stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    /**
     * Checks if a User with the given username exists.
     *
     * @param username the username to check for existence.
     * @return true if a User with the given username exists, false otherwise.
     */

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Registers a new admin user.
     *
     * @param userRequestDTO the UserRequestDTO containing admin user details
     * @return a UserResponseDTO containing the created admin user details
     */


    @Transactional
    public UserResponseDTO registerAdmin(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return registerUserWithRole(userRequestDTO, "ROLE_ADMIN");
    }

    /**
     * Registers a new user with the default role of "ROLE_USER".
     *
     * @param userRequestDTO the UserRequestDTO containing user details
     * @return a UserResponseDTO containing the created user details
     */

    @Transactional
    public UserResponseDTO registerUser(@Valid UserRequestDTO userRequestDTO) {
        return registerUserWithRole(userRequestDTO, "ROLE_USER");
    }

    /**
     * Registers a new user with the specified role.
     *
     * @param userRequestDTO the UserRequestDTO containing user details
     * @param roleName       the name of the role to assign to the user
     * @return a UserResponseDTO containing the created user details
     * @throws DuplicateResourceException if a user with the given username or email already exists
     */

    @Transactional
    public UserResponseDTO registerUserWithRole(UserRequestDTO userRequestDTO, String roleName) {
        User user = userMapper.toEntity(userRequestDTO);

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException("User with username " + user.getUsername() + " already exists.");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("User with email " + user.getEmail() + " already exists.");
        }

        String encryptedPassword = encodePassword(user.getPassword());
        user.setPassword(encryptedPassword);

        Role userRole = roleService.findByName(roleName);
        user.addRole(userRole);

        User createdUser = create(user);

        logger.info("User {} created with role {}", user.getUsername(), roleName);

        return userMapper.toResponseDTO(createdUser);
    }

    /**
     * Deletes a User by their ID.
     *
     * @param id the ID of the user to delete.
     */

    @Transactional
    public void deleteUser(Long id) {
        delete(id);
    }

    /**
     * Updates an existing User with the provided details.
     *
     * @param id            the ID of the user to update.
     * @param userUpdateDTO the new details for the user.
     * @return the updated User entity as a UserResponseDTO.
     * @throws DuplicateResourceException if a user with the given username or email already exists.
     * @throws ResourceNotFoundException  if the user with the given ID does not exist.
     */

    @Transactional
    public UserResponseDTO updateUser(Long id, @Valid UserUpdateDTO userUpdateDTO) {
        User user = findById(id);

        String userNameToUpdate = userUpdateDTO.getUsername();
        if (userNameToUpdate != null && !userNameToUpdate.isBlank() && !user.getUsername().equals(userNameToUpdate)) {
            if (userRepository.existsByUsername(userNameToUpdate)) {
                throw new DuplicateResourceException("User with username " + userNameToUpdate + " already exists.");
            }
            user.setUsername(userNameToUpdate);
        }

        String emailToUpdate = userUpdateDTO.getEmail();
        if (emailToUpdate != null && !emailToUpdate.isBlank() && !user.getEmail().equalsIgnoreCase(emailToUpdate)) {
            if (userRepository.existsByEmail(emailToUpdate)) {
                throw new DuplicateResourceException("User with email " + emailToUpdate + " already exists.");
            }
            user.setEmail(emailToUpdate);
        }

        String passwordToUpdate = userUpdateDTO.getPassword();
        if (passwordToUpdate != null && !passwordToUpdate.isBlank()) {
            if (!passwordEncoder.matches(passwordToUpdate, user.getPassword())) {
                user.setPassword(encodePassword(passwordToUpdate));
            }
        }

        User updatedUser = update(user.getId(), user);
        return userMapper.toResponseDTO(updatedUser);
    }

    /**
     * Encodes the given raw password using the configured password encoder.
     *
     * @param rawPassword the raw password to encode
     * @return the encoded password
     */

    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}

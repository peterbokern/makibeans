package com.makibeans.mapper;

import com.makibeans.dto.user.UserRequestDTO;
import com.makibeans.dto.user.UserResponseDTO;
import com.makibeans.model.Role;
import com.makibeans.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link User} and its DTO {@link UserResponseDTO}.
 */

@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a User entity to a UserResponseDTO.
     *
     * @param user the User entity to convert
     * @return the converted UserResponseDTO
     */

    @Mapping(source = "roles", target = "roles")
    UserResponseDTO toResponseDTO(User user);

    /**
     * Converts a UserRequestDTO to a User entity.
     *
     * @param userRequestDTO the UserRequestDTO to convert
     * @return the converted User entity
     */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toEntity(UserRequestDTO userRequestDTO);

    /**
     * Converts a Role to a String.
     *
     * @param role the Role to convert
     * @return the converted String
     */

    default String map(Role role) {
        return role.getAuthority();
    }
}

package com.makibeans.mapper;

import com.makibeans.dto.UserRequestDTO;
import com.makibeans.dto.UserResponseDTO;
import com.makibeans.model.Role;
import com.makibeans.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "roles", target = "roles")
    UserResponseDTO toResponseDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toEntity(UserRequestDTO userRequestDTO);

    //callback to convert set of role object to set of role name strings
    default String map(Role role) {
        return role.getAuthority();
    }
}

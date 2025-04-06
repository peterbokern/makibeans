package com.makibeans.mapper;

import com.makibeans.dto.login.LoginResponseDTO;
import com.makibeans.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between User entities and LoginResponseDTOs.
 * Utilizes MapStruct for automatic mapping generation.
 */
@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "token", target = "token")
    LoginResponseDTO toResponseDTO(User user, String token);
}

package com.makibeans.user.mapper;

import com.makibeans.user.dto.UserPublicResponseDTO;
import com.makibeans.user.dto.UserAdminResponseDTO;

import com.makibeans.user.dto.UserResponseDTO;
import com.makibeans.user.dto.UserUpdateDTO;
import com.makibeans.role.model.Role;
import com.makibeans.user.model.User;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link User}.
 */
@Mapper(
        componentModel = "spring"
)
public interface UserMapper {

    // -------------------------------------------------------------------------
    // Legacy DTO (keep until you’ve removed all usages)
    // -------------------------------------------------------------------------
    UserResponseDTO toResponseDTO(User user);

    // -------------------------------------------------------------------------
    // New public DTO mapping
    // -------------------------------------------------------------------------
    @Mapping(target = "roles", source = "roles")
    UserPublicResponseDTO toPublicResponseDTO(User user);

    // -------------------------------------------------------------------------
    // New admin DTO mapping (with audit)
    // -------------------------------------------------------------------------
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "audit", expression = "java(com.makibeans.audit.mapper.AuditableMapper.toAuditableInfo(user))")
    UserAdminResponseDTO toAdminResponseDTO(User user);

    // -------------------------------------------------------------------------
    // Role -> String (authority) mapping
    // -------------------------------------------------------------------------
    default String map(Role role) {
        return role.getAuthority();
    }

    // -------------------------------------------------------------------------
    // UPDATE from DTO
    // -------------------------------------------------------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(UserUpdateDTO updateDTO, @MappingTarget User user);
}

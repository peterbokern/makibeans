package com.makibeans.user.mapper;

import com.makibeans.audit.mapper.AuditableMapper;
import com.makibeans.user.dto.UserPublicResponseDTO;
import com.makibeans.user.dto.UserAdminResponseDTO;

import com.makibeans.user.dto.UserUpdateDTO;
import com.makibeans.role.model.Role;
import com.makibeans.user.model.User;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link User}.
 */
@Mapper(
        componentModel = "spring", uses = {AuditableMapper.class}
)
public interface UserMapper {

    // -------------------------------------------------------------------------
    // New public DTO mapping
    // -------------------------------------------------------------------------
    @Mapping(target = "roles", source = "roles")
    UserPublicResponseDTO toPublicResponseDTO(User user);

    // -------------------------------------------------------------------------
    // New admin DTO mapping (with audit)
    // -------------------------------------------------------------------------
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "audit", source = ".")
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

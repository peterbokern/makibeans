package com.makibeans.role.service;

import org.springframework.transaction.annotation.Transactional;

public interface RoleService {

    boolean existsByName(String name);

    @Transactional
    void createRole(String name);
}

package com.makibeans.service.service;

import com.makibeans.dto.login.LoginRequestDTO;
import com.makibeans.dto.login.LoginResponseDTO;
import jakarta.validation.Valid;

public interface AuthService {
    LoginResponseDTO loginUser(@Valid LoginRequestDTO loginRequestDTO);
}

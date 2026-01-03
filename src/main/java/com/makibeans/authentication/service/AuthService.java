package com.makibeans.authentication.service;

import com.makibeans.authentication.dto.LoginRequestDTO;
import com.makibeans.authentication.dto.LoginResponseDTO;
import jakarta.validation.Valid;

public interface    AuthService {
    LoginResponseDTO loginUser(@Valid LoginRequestDTO loginRequestDTO);
}

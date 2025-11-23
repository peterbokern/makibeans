package com.makibeans.authentication.service;

import com.makibeans.login.dto.LoginRequestDTO;
import com.makibeans.login.dto.LoginResponseDTO;
import jakarta.validation.Valid;

public interface    AuthService {
    LoginResponseDTO loginUser(@Valid LoginRequestDTO loginRequestDTO);
}

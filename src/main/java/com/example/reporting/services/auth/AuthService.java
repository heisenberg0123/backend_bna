package com.example.reporting.services.auth;

import com.example.reporting.dtos.SignupRequest;
import com.example.reporting.dtos.UserDto;

public interface     AuthService {
    UserDto createUser(SignupRequest signupRequest);
}

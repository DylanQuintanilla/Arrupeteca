package com.arrupeteca.service;

import com.arrupeteca.dto.request.LoginRequest;
import com.arrupeteca.dto.request.RegisterRequest;
import com.arrupeteca.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

}

package com.arrupeteca.controller;

import com.arrupeteca.dto.request.LoginRequest;
import com.arrupeteca.dto.request.RegisterRequest;
import com.arrupeteca.dto.response.AuthResponse;
import com.arrupeteca.exception.GlobalExceptionHandler;
import com.arrupeteca.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/register - Debe retornar 201 Created y token al registrar exitosamente")
    void debeRegistrarUsuarioExitosamente() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .nombres("Carlos")
                .apellidos("Gómez")
                .correo("carlos@arrupeteca.com")
                .contrasena("password123")
                .idTipo(1L)
                .build();

        AuthResponse authResponse = AuthResponse.builder()
                .token("jwt-mock-token")
                .tipo("admin")
                .correo("carlos@arrupeteca.com")
                .nombreCompleto("Carlos Gómez")
                .build();

        given(authService.register(any(RegisterRequest.class))).willReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.token").value("jwt-mock-token"))
                .andExpect(jsonPath("$.data.correo").value("carlos@arrupeteca.com"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Debe retornar 200 OK al autenticar exitosamente")
    void debeAutenticarExitosamente() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .correo("carlos@arrupeteca.com")
                .contrasena("password123")
                .build();

        AuthResponse authResponse = AuthResponse.builder()
                .token("jwt-mock-token")
                .tipo("admin")
                .correo("carlos@arrupeteca.com")
                .nombreCompleto("Carlos Gómez")
                .build();

        given(authService.login(any(LoginRequest.class))).willReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.token").value("jwt-mock-token"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Debe retornar 400 Bad Request cuando el DTO es inválido")
    void debeRetornar400CuandoDtoEsInvalido() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .correo("correo-invalido")
                .contrasena("")
                .build();

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false));
    }

}

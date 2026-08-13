package com.arrupeteca.controller;

import com.arrupeteca.dto.response.TipoUsuarioResponse;
import com.arrupeteca.dto.response.detail.UsuarioDetalleResponse;
import com.arrupeteca.dto.response.summary.UsuarioResumen;
import com.arrupeteca.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest extends BaseControllerTest {

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    @WithMockUser(username = "user@test.com")
    @DisplayName("GET /api/v1/usuarios/me - Should return profile of logged in user")
    void shouldReturnMiPerfil() throws Exception {
        UsuarioDetalleResponse detail = UsuarioDetalleResponse.builder()
                .id(1L).nombres("Juan").apellidos("Perez").correo("user@test.com")
                .tipoUsuario(TipoUsuarioResponse.builder().id(1L).nombre("ALUMNO").build())
                .fechaRegistro(LocalDateTime.now()).borradoLogico(false).bloqueadoPorMora(false).build();

        when(usuarioService.obtenerPerfilActual("user@test.com")).thenReturn(detail);

        mockMvc.perform(get("/api/v1/usuarios/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.correo").value("user@test.com"));
    }

    @Test
    @WithMockUser(username = "user@test.com")
    @DisplayName("PATCH /api/v1/usuarios/me - Should update profile")
    void shouldUpdateMiPerfil() throws Exception {
        UsuarioDetalleResponse detail = UsuarioDetalleResponse.builder()
                .id(1L).nombres("Juan Updated").apellidos("Perez").correo("user@test.com")
                .tipoUsuario(TipoUsuarioResponse.builder().id(1L).nombre("ALUMNO").build())
                .fechaRegistro(LocalDateTime.now()).borradoLogico(false).bloqueadoPorMora(false).build();

        when(usuarioService.actualizarPerfilActual(eq("user@test.com"), any())).thenReturn(detail);

        mockMvc.perform(patch("/api/v1/usuarios/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombres\":\"Juan Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombres").value("Juan Updated"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/v1/usuarios/admin/all - Should return list of users")
    void shouldReturnAllForAdmin() throws Exception {
        Page<UsuarioResumen> page = new PageImpl<>(List.of());
        when(usuarioService.busquedaAdmin(any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/usuarios/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/v1/usuarios/admin - Should create user for admin")
    void shouldCreateUserForAdmin() throws Exception {
        UsuarioDetalleResponse detail = UsuarioDetalleResponse.builder()
                .id(1L).nombres("Pedro").apellidos("Gomez").correo("pedro@test.com")
                .tipoUsuario(TipoUsuarioResponse.builder().id(1L).nombre("DOCENTE").build())
                .fechaRegistro(LocalDateTime.now()).borradoLogico(false).bloqueadoPorMora(false).build();

        when(usuarioService.crearAdmin(any())).thenReturn(detail);

        mockMvc.perform(post("/api/v1/usuarios/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombres\":\"Pedro\",\"apellidos\":\"Gomez\",\"correo\":\"pedro@test.com\",\"contrasena\":\"pass123\",\"idTipoUsuario\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.correo").value("pedro@test.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/v1/usuarios/admin/{id} - Should deactivate user")
    void shouldDeactivateUser() throws Exception {
        mockMvc.perform(delete("/api/v1/usuarios/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /api/v1/usuarios/admin/{id}/bloquear-mora - Should change mora status")
    void shouldChangeMoraStatus() throws Exception {
        mockMvc.perform(patch("/api/v1/usuarios/admin/1/bloquear-mora?bloqueado=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}

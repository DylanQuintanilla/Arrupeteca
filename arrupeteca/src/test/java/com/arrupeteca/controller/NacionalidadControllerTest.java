package com.arrupeteca.controller;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.dto.response.NacionalidadResponse;
import com.arrupeteca.service.NacionalidadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NacionalidadController.class)
class NacionalidadControllerTest extends BaseControllerTest {

    @MockitoBean
    private NacionalidadService nacionalidadService;

    @Test
    @DisplayName("GET /api/v1/nacionalidades - Should return active nationalities")
    void shouldReturnActive() throws Exception {
        Page<NacionalidadResponse> page = new PageImpl<>(List.of());
        when(nacionalidadService.obtenerTodosActivos(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/nacionalidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/nacionalidades/{id} - Should return nationality by ID")
    void shouldReturnById() throws Exception {
        NacionalidadResponse response = new NacionalidadResponse(1L, "Test", false);
        when(nacionalidadService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/nacionalidades/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Test"));
    }

    @Test
    @DisplayName("POST /api/v1/nacionalidades - Should create nationality")
    void shouldCreate() throws Exception {
        NacionalidadRequest request = NacionalidadRequest.builder().nombre("Nuevo").build();
        NacionalidadResponse response = new NacionalidadResponse(1L, "Nuevo", false);

        when(nacionalidadService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/nacionalidades/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/nacionalidades/{id} - Should update nationality")
    void shouldUpdate() throws Exception {
        NacionalidadRequest request = NacionalidadRequest.builder().nombre("Actualizado").build();
        NacionalidadResponse response = new NacionalidadResponse(1L, "Actualizado", false);

        when(nacionalidadService.actualizar(any(Long.class), any(NacionalidadRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/nacionalidades/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/nacionalidades/admin - Should return all nationalities for admin")
    void shouldReturnAllAdmin() throws Exception {
        Page<NacionalidadResponse> page = new PageImpl<>(List.of());
        when(nacionalidadService.obtenerTodos(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/nacionalidades/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Tabla de nacionalidades"));
    }

    @Test
    @DisplayName("GET /api/v1/nacionalidades/admin/{id} - Should return nationality by ID for admin")
    void shouldReturnByIdAdmin() throws Exception {
        NacionalidadResponse response = new NacionalidadResponse(1L, "Test", false);
        when(nacionalidadService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/nacionalidades/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Nacionalidad encontrado(a)"));
    }

    @Test
    @DisplayName("DELETE /api/v1/nacionalidades/{id} - Should deactivate nationality")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/nacionalidades/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Nacionalidad desactivado(a) correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/nacionalidades/{id}/activar - Should activate nationality")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/nacionalidades/admin/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Nacionalidad activado(a) correctamente"));
    }
}

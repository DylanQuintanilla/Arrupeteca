package com.arrupeteca.controller;

import com.arrupeteca.dto.request.CicloRequest;
import com.arrupeteca.dto.response.CicloResponse;
import com.arrupeteca.service.CicloService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CicloController.class)
class CicloControllerTest extends BaseControllerTest {

    @MockitoBean
    private CicloService cicloService;

    @Test
    @DisplayName("GET /api/v1/ciclos - Should return active ciclos list")
    void shouldReturnActiveCiclos() throws Exception {
        when(cicloService.obtenerTodosActivos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/ciclos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de ciclos activos"));
    }

    @Test
    @DisplayName("GET /api/v1/ciclos/all - Should return ciclos table")
    void shouldReturnAllCiclos() throws Exception {
        Page<CicloResponse> page = new PageImpl<>(List.of());
        when(cicloService.obtenerTodos(eq(null), eq(null), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/ciclos/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Tabla de ciclos"));
    }

    @Test
    @DisplayName("GET /api/v1/ciclos/{id} - Should return ciclo by ID")
    void shouldReturnCicloById() throws Exception {
        CicloResponse response = new CicloResponse(1L, "Primer ciclo", false);
        when(cicloService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/ciclos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Ciclo encontrado"))
                .andExpect(jsonPath("$.data.nombre").value("Primer ciclo"));
    }

    @Test
    @DisplayName("POST /api/v1/ciclos - Should create ciclo")
    void shouldCreateCiclo() throws Exception {
        CicloRequest request = CicloRequest.builder().nombre("Nuevo").build();

        CicloResponse response = new CicloResponse(1L, "Nuevo", false);
        when(cicloService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/ciclos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Nuevo"));
    }

    @Test
    @DisplayName("PUT /api/v1/ciclos/{id} - Should update ciclo")
    void shouldUpdateCiclo() throws Exception {
        CicloRequest request = CicloRequest.builder().nombre("Actualizado").build();

        CicloResponse response = new CicloResponse(1L, "Actualizado", false);
        when(cicloService.actualizar(any(Long.class), any(CicloRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/ciclos/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Ciclo actualizado correctamente"))
                .andExpect(jsonPath("$.data.nombre").value("Actualizado"));
    }

    @Test
    @DisplayName("DELETE /api/v1/ciclos/{id} - Should deactivate ciclo")
    void shouldDeactivateCiclo() throws Exception {
        mockMvc.perform(delete("/api/v1/ciclos/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Ciclo desactivado correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/ciclos/{id}/activar - Should activate ciclo")
    void shouldActivateCiclo() throws Exception {
        mockMvc.perform(patch("/api/v1/ciclos/1/activar")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Ciclo activado correctamente"));
    }
}

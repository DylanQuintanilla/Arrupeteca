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
import static org.mockito.Mockito.when;
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
    @DisplayName("GET /api/v1/ciclos - Should return active ciclos")
    void shouldReturnActive() throws Exception {
        Page<CicloResponse> page = new PageImpl<>(List.of());
        when(cicloService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/ciclos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/ciclos/{id} - Should return ciclo by ID")
    void shouldReturnById() throws Exception {
        CicloResponse response = new CicloResponse(1L, "Primer ciclo", false);
        when(cicloService.obtenerPorIdActivo(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/ciclos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Primer ciclo"));
    }

    @Test
    @DisplayName("POST /api/v1/ciclos - Should create ciclo")
    void shouldCreate() throws Exception {
        CicloRequest request = CicloRequest.builder().nombre("Nuevo ciclo").build();
        CicloResponse response = new CicloResponse(1L, "Nuevo ciclo", false);
        when(cicloService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/ciclos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/ciclos/{id} - Should update ciclo")
    void shouldUpdate() throws Exception {
        CicloRequest request = CicloRequest.builder().nombre("Ciclo act").build();
        CicloResponse response = new CicloResponse(1L, "Ciclo act", false);
        when(cicloService.actualizar(any(Long.class), any(CicloRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/ciclos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("DELETE /api/v1/ciclos/{id} - Should deactivate ciclo")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/ciclos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/ciclos/{id}/activar - Should activate ciclo")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/ciclos/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}

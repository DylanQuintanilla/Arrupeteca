package com.arrupeteca.controller;

import com.arrupeteca.dto.request.EtiquetaRequest;
import com.arrupeteca.dto.response.EtiquetaResponse;
import com.arrupeteca.service.EtiquetaService;
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

@WebMvcTest(EtiquetaController.class)
class EtiquetaControllerTest extends BaseControllerTest {

    @MockitoBean
    private EtiquetaService etiquetaService;

    @Test
    @DisplayName("GET /api/v1/etiquetas - Should return active etiquetas list")
    void shouldReturnActiveEtiquetas() throws Exception {
        when(etiquetaService.obtenerTodosActivos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/etiquetas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de etiquetas activas"));
    }

    @Test
    @DisplayName("GET /api/v1/etiquetas/all - Should return etiquetas table")
    void shouldReturnAllEtiquetas() throws Exception {
        Page<EtiquetaResponse> page = new PageImpl<>(List.of());
        when(etiquetaService.obtenerTodos(eq(null), eq(null), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/etiquetas/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Tabla de etiquetas"));
    }

    @Test
    @DisplayName("GET /api/v1/etiquetas/{id} - Should return etiqueta by ID")
    void shouldReturnEtiquetaById() throws Exception {
        EtiquetaResponse response = new EtiquetaResponse(1L, "Tecnología", false);
        when(etiquetaService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/etiquetas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Etiqueta encontrada"))
                .andExpect(jsonPath("$.data.nombre").value("Tecnología"));
    }

    @Test
    @DisplayName("POST /api/v1/etiquetas - Should create etiqueta")
    void shouldCreateEtiqueta() throws Exception {
        EtiquetaRequest request = EtiquetaRequest.builder().nombre("Nueva").build();

        EtiquetaResponse response = new EtiquetaResponse(1L, "Nueva", false);
        when(etiquetaService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/etiquetas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Nueva"));
    }

    @Test
    @DisplayName("PUT /api/v1/etiquetas/{id} - Should update etiqueta")
    void shouldUpdateEtiqueta() throws Exception {
        EtiquetaRequest request = EtiquetaRequest.builder().nombre("Actualizada").build();

        EtiquetaResponse response = new EtiquetaResponse(1L, "Actualizada", false);
        when(etiquetaService.actualizar(any(Long.class), any(EtiquetaRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/etiquetas/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Etiqueta actualizada correctamente"))
                .andExpect(jsonPath("$.data.nombre").value("Actualizada"));
    }

    @Test
    @DisplayName("DELETE /api/v1/etiquetas/{id} - Should deactivate etiqueta")
    void shouldDeactivateEtiqueta() throws Exception {
        mockMvc.perform(delete("/api/v1/etiquetas/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Etiqueta desactivada correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/etiquetas/{id}/activar - Should activate etiqueta")
    void shouldActivateEtiqueta() throws Exception {
        mockMvc.perform(patch("/api/v1/etiquetas/1/activar")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Etiqueta activada correctamente"));
    }
}

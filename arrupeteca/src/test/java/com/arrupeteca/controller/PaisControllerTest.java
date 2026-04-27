package com.arrupeteca.controller;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.dto.response.summary.PaisResumen;
import com.arrupeteca.service.PaisService;
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

@WebMvcTest(PaisController.class)
class PaisControllerTest extends BaseControllerTest {

    @MockitoBean
    private PaisService paisService;

    @Test
    @DisplayName("GET /api/v1/paises - Should return active countries")
    void shouldReturnActive() throws Exception {
        Page<PaisResumen> page = new PageImpl<>(List.of());
        when(paisService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/paises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/paises/{id} - Should return country by ID")
    void shouldReturnById() throws Exception {
        PaisResumen resumen = new PaisResumen(1L, "Test", false);
        when(paisService.obtenerPorIdActivo(1L)).thenReturn(resumen);

        mockMvc.perform(get("/api/v1/paises/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Test"));
    }

    @Test
    @DisplayName("POST /api/v1/paises - Should create country")
    void shouldCreate() throws Exception {
        PaisRequest request = new PaisRequest();
        request.setNombre("Nuevo");

        PaisResumen resumen = new PaisResumen(1L, "Nuevo", false);
        when(paisService.crear(any())).thenReturn(resumen);

        mockMvc.perform(post("/api/v1/paises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/paises/{id} - Should update country")
    void shouldUpdate() throws Exception {
        PaisRequest request = new PaisRequest();
        request.setNombre("Actualizado");

        PaisResumen resumen = new PaisResumen(1L, "Actualizado", false);
        when(paisService.actualizar(any(Long.class), any(PaisRequest.class))).thenReturn(resumen);

        mockMvc.perform(put("/api/v1/paises/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/paises/admin - Should return all countries for admin")
    void shouldReturnAllAdmin() throws Exception {
        Page<PaisResumen> page = new PageImpl<>(List.of());
        when(paisService.obtenerTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/paises/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de países"));
    }

    @Test
    @DisplayName("GET /api/v1/paises/admin/{id} - Should return country by ID for admin")
    void shouldReturnByIdAdmin() throws Exception {
        PaisResumen resumen = new PaisResumen(1L, "Test", false);
        when(paisService.obtenerPorId(1L)).thenReturn(resumen);

        mockMvc.perform(get("/api/v1/paises/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("País encontrado (admin)"));
    }

    @Test
    @DisplayName("DELETE /api/v1/paises/{id} - Should deactivate country")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/paises/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("País desactivado correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/paises/{id}/activar - Should activate country")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/paises/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("País activado correctamente"));
    }
}

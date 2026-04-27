package com.arrupeteca.controller;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.dto.response.summary.NacionalidadResumen;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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
    @DisplayName("GET /api/v1/nacionalidades - Should return active")
    void shouldReturnActive() throws Exception {
        Page<NacionalidadResumen> page = new PageImpl<>(List.of());
        when(nacionalidadService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/nacionalidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/nacionalidades/{id} - Should return by ID")
    void shouldReturnById() throws Exception {
        NacionalidadResumen resumen = new NacionalidadResumen(1L, "Salvadoreña", false);
        when(nacionalidadService.obtenerPorIdActivo(1L)).thenReturn(resumen);

        mockMvc.perform(get("/api/v1/nacionalidades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Salvadoreña"));
    }

    @Test
    @DisplayName("GET /api/v1/nacionalidades/admin - Should return all")
    void shouldReturnAllAdmin() throws Exception {
        Page<NacionalidadResumen> page = new PageImpl<>(List.of());
        when(nacionalidadService.obtenerTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/nacionalidades/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/nacionalidades/admin/{id} - Should return by ID (admin)")
    void shouldReturnByIdAdmin() throws Exception {
        NacionalidadResumen resumen = new NacionalidadResumen(1L, "Salvadoreña", true);
        when(nacionalidadService.obtenerPorId(1L)).thenReturn(resumen);

        mockMvc.perform(get("/api/v1/nacionalidades/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Salvadoreña"));
    }

    @Test
    @DisplayName("POST /api/v1/nacionalidades - Should create")
    void shouldCreate() throws Exception {
        NacionalidadRequest request = new NacionalidadRequest();
        request.setNombre("Nueva");

        NacionalidadResumen resumen = new NacionalidadResumen(1L, "Nueva", false);
        when(nacionalidadService.crear(any())).thenReturn(resumen);

        mockMvc.perform(post("/api/v1/nacionalidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/nacionalidades/{id} - Should update")
    void shouldUpdate() throws Exception {
        NacionalidadRequest request = new NacionalidadRequest();
        request.setNombre("Actualizada");

        NacionalidadResumen resumen = new NacionalidadResumen(1L, "Actualizada", false);
        when(nacionalidadService.actualizar(eq(1L), any())).thenReturn(resumen);

        mockMvc.perform(put("/api/v1/nacionalidades/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("DELETE /api/v1/nacionalidades/{id} - Should deactivate")
    void shouldDeactivate() throws Exception {
        doNothing().when(nacionalidadService).cambiarEstado(1L, false);

        mockMvc.perform(delete("/api/v1/nacionalidades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/nacionalidades/{id}/activar - Should activate")
    void shouldActivate() throws Exception {
        doNothing().when(nacionalidadService).cambiarEstado(1L, true);

        mockMvc.perform(patch("/api/v1/nacionalidades/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}

package com.arrupeteca.controller;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.persistence.projection.FormatoResumen;
import com.arrupeteca.service.FormatoService;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FormatoController.class)
class FormatoControllerTest extends BaseControllerTest {

    @MockitoBean
    private FormatoService formatoService;

    @Test
    @DisplayName("GET /api/v1/formatos - Should return active formats")
    void shouldReturnActive() throws Exception {
        Page<FormatoResumen> page = new PageImpl<>(List.of());
        when(formatoService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/formatos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de formatos activos"));
    }

    @Test
    @DisplayName("GET /api/v1/formatos/{id} - Should return format by ID")
    void shouldReturnById() throws Exception {
        FormatoResumen resumen = new TestFormatoResumen(1L, "Formato Test", false);
        when(formatoService.obtenerPorIdActivo(1L)).thenReturn(resumen);

        mockMvc.perform(get("/api/v1/formatos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Formato encontrado"));
    }

    @Test
    @DisplayName("POST /api/v1/formatos - Should create format")
    void shouldCreate() throws Exception {
        FormatoRequest request = new FormatoRequest();
        request.setNombre("Nuevo");

        FormatoResumen resumen = new TestFormatoResumen(1L, "Nuevo", false);
        when(formatoService.crear(any())).thenReturn(resumen);

        mockMvc.perform(post("/api/v1/formatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Nuevo"));
    }

    @Test
    @DisplayName("PUT /api/v1/formatos/{id} - Should update format")
    void shouldUpdate() throws Exception {
        FormatoRequest request = new FormatoRequest();
        request.setNombre("Actualizado");

        FormatoResumen resumen = new TestFormatoResumen(1L, "Actualizado", false);
        when(formatoService.actualizar(any(Long.class), any(FormatoRequest.class))).thenReturn(resumen);

        mockMvc.perform(put("/api/v1/formatos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Formato actualizado correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/formatos/{id} - Should deactivate format")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/formatos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Formato desactivado correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/formatos/{id}/activar - Should activate format")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/formatos/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Formato activado correctamente"));
    }

    @Test
    @DisplayName("GET /api/v1/formatos/admin - Should return all formats for admin")
    void shouldReturnAllAdmin() throws Exception {
        Page<FormatoResumen> page = new PageImpl<>(List.of());
        when(formatoService.obtenerTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/formatos/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de formatos"));
    }

    private static record TestFormatoResumen(
            Long id,
            String nombre,
            boolean borradoLogico
    ) implements FormatoResumen {

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public String getNombre() {
            return nombre;
        }

        @Override
        public boolean getBorradoLogico() {
            return borradoLogico;
        }
    }
}

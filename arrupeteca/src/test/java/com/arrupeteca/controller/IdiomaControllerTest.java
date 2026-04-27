package com.arrupeteca.controller;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.dto.response.detail.IdiomaDetalleResponse;
import com.arrupeteca.dto.response.summary.IdiomaResumen;
import com.arrupeteca.service.IdiomaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
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

@WebMvcTest(IdiomaController.class)
class IdiomaControllerTest extends BaseControllerTest {

    @MockitoBean
    private IdiomaService idiomaService;

    @Test
    @DisplayName("GET /api/v1/idiomas - Should return active languages")
    void shouldReturnActive() throws Exception {
        Page<IdiomaResumen> page = new PageImpl<>(List.of());
        when(idiomaService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/idiomas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/idiomas/{id} - Should return language by ID")
    void shouldReturnById() throws Exception {
        IdiomaDetalleResponse detail = new IdiomaDetalleResponse(
                1L, "Idioma Test", false, "admin", null, "admin", null, Collections.emptySet());
        when(idiomaService.obtenerPorIdActivo(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/idiomas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Idioma Test"));
    }

    @Test
    @DisplayName("POST /api/v1/idiomas - Should create language")
    void shouldCreate() throws Exception {
        IdiomaRequest request = new IdiomaRequest();
        request.setNombre("Nuevo");

        IdiomaDetalleResponse detail = new IdiomaDetalleResponse(
                1L, "Nuevo", false, "admin", null, "admin", null, Collections.emptySet());
        when(idiomaService.crear(any())).thenReturn(detail);

        mockMvc.perform(post("/api/v1/idiomas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/idiomas/{id} - Should update language")
    void shouldUpdate() throws Exception {
        IdiomaRequest request = new IdiomaRequest();
        request.setNombre("Actualizado");

        IdiomaDetalleResponse detail = new IdiomaDetalleResponse(
                1L, "Actualizado", false, "admin", null, "admin", null, Collections.emptySet());
        when(idiomaService.actualizar(any(Long.class), any(IdiomaRequest.class))).thenReturn(detail);

        mockMvc.perform(put("/api/v1/idiomas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/idiomas/admin - Should return all languages for admin")
    void shouldReturnAllAdmin() throws Exception {
        Page<IdiomaResumen> page = new PageImpl<>(List.of());
        when(idiomaService.obtenerTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/idiomas/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de idiomas"));
    }

    @Test
    @DisplayName("GET /api/v1/idiomas/admin/{id} - Should return language by ID for admin")
    void shouldReturnByIdAdmin() throws Exception {
        IdiomaDetalleResponse detail = new IdiomaDetalleResponse(
                1L, "Idioma Test", false, "admin", null, "admin", null, Collections.emptySet());
        when(idiomaService.obtenerPorId(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/idiomas/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Idioma encontrado (admin)"));
    }

    @Test
    @DisplayName("DELETE /api/v1/idiomas/{id} - Should deactivate language")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/idiomas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Idioma desactivado correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/idiomas/{id}/activar - Should activate language")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/idiomas/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Idioma activado correctamente"));
    }
}

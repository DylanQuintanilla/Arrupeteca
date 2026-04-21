package com.arrupeteca.controller;

import java.util.List;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.persistence.projection.GeneroResumen;
import com.arrupeteca.service.GeneroService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GeneroController.class)
class GeneroControllerTest extends BaseControllerTest {

    @MockitoBean
    private GeneroService generoService;

    @Test
    @DisplayName("GET /api/v1/generos - Should return active genres")
    void shouldReturnActive() throws Exception {
        when(generoService.obtenerTodosActivos(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
        mockMvc.perform(get("/api/v1/generos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/generos/{id} - Should return genre by ID")
    void shouldReturnGenreById() throws Exception {
        GeneroResumen resumen = new TestGeneroResumen(1L, "Género Test", false);
        when(generoService.obtenerPorIdActivo(1L)).thenReturn(resumen);

        mockMvc.perform(get("/api/v1/generos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Género encontrado"));
    }

    @Test
    @DisplayName("POST /api/v1/generos - Should create genre")
    void shouldCreateGenre() throws Exception {
        GeneroRequest request = new GeneroRequest();
        request.setNombre("Nuevo");

        GeneroResumen resumen = new TestGeneroResumen(1L, "Nuevo", false);
        when(generoService.crear(any())).thenReturn(resumen);

        mockMvc.perform(post("/api/v1/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Nuevo"));
    }

    @Test
    @DisplayName("PUT /api/v1/generos/{id} - Should update genre")
    void shouldUpdateGenre() throws Exception {
        GeneroRequest request = new GeneroRequest();
        request.setNombre("Actualizado");

        GeneroResumen resumen = new TestGeneroResumen(1L, "Actualizado", false);
        when(generoService.actualizar(any(Long.class), any(GeneroRequest.class))).thenReturn(resumen);

        mockMvc.perform(put("/api/v1/generos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Género actualizado correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/generos/{id} - Should deactivate genre")
    void shouldDeactivateGenre() throws Exception {
        mockMvc.perform(delete("/api/v1/generos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Género desactivado correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/generos/{id}/activar - Should activate genre")
    void shouldActivateGenre() throws Exception {
        mockMvc.perform(patch("/api/v1/generos/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Género activado correctamente"));
    }

    @Test
    @DisplayName("GET /api/v1/generos/admin - Should return all genres for admin")
    void shouldReturnAllGenresAdmin() throws Exception {
        Page<GeneroResumen> page = new PageImpl<>(List.of());
        when(generoService.obtenerTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/generos/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de géneros"));
    }

    private static record TestGeneroResumen(
            Long id,
            String nombre,
            boolean borradoLogico
    ) implements GeneroResumen {

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

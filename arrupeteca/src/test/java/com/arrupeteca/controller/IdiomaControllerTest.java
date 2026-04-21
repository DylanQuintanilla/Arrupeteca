package com.arrupeteca.controller;

import java.util.List;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.persistence.projection.IdiomaResumen;
import com.arrupeteca.service.IdiomaService;
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

@WebMvcTest(IdiomaController.class)
class IdiomaControllerTest extends BaseControllerTest {

    @MockitoBean
    private IdiomaService idiomaService;

    @Test
    @DisplayName("GET /api/v1/idiomas - Should return active idioms")
    void shouldReturnActive() throws Exception {
        Page<IdiomaResumen> page = new PageImpl<>(List.of());
        when(idiomaService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/idiomas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de idiomas activos"));
    }

    @Test
    @DisplayName("GET /api/v1/idiomas/{id} - Should return idiom by ID")
    void shouldReturnById() throws Exception {
        IdiomaResumen resumen = new TestIdiomaResumen(1L, "Idioma Test", false);
        when(idiomaService.obtenerPorIdActivo(1L)).thenReturn(resumen);

        mockMvc.perform(get("/api/v1/idiomas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Idioma encontrado"));
    }

    @Test
    @DisplayName("POST /api/v1/idiomas - Should create idiom")
    void shouldCreate() throws Exception {
        IdiomaRequest request = new IdiomaRequest();
        request.setNombre("Nuevo");

        IdiomaResumen resumen = new TestIdiomaResumen(1L, "Nuevo", false);
        when(idiomaService.crear(any())).thenReturn(resumen);

        mockMvc.perform(post("/api/v1/idiomas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Nuevo"));
    }

    @Test
    @DisplayName("PUT /api/v1/idiomas/{id} - Should update idiom")
    void shouldUpdate() throws Exception {
        IdiomaRequest request = new IdiomaRequest();
        request.setNombre("Actualizado");

        IdiomaResumen resumen = new TestIdiomaResumen(1L, "Actualizado", false);
        when(idiomaService.actualizar(any(Long.class), any(IdiomaRequest.class))).thenReturn(resumen);

        mockMvc.perform(put("/api/v1/idiomas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Idioma actualizado correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/idiomas/{id} - Should deactivate idiom")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/idiomas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Idioma desactivado correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/idiomas/{id}/activar - Should activate idiom")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/idiomas/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Idioma activado correctamente"));
    }

    @Test
    @DisplayName("GET /api/v1/idiomas/admin - Should return all idioms for admin")
    void shouldReturnAllAdmin() throws Exception {
        Page<IdiomaResumen> page = new PageImpl<>(List.of());
        when(idiomaService.obtenerTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/idiomas/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de idiomas"));
    }

    private static record TestIdiomaResumen(
            Long id,
            String nombre,
            boolean borradoLogico
    ) implements IdiomaResumen {

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

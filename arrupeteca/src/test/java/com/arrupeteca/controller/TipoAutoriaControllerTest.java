package com.arrupeteca.controller;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.persistence.projection.TipoAutoriaResumen;
import com.arrupeteca.service.TipoAutoriaService;
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

@WebMvcTest(TipoAutoriaController.class)
class TipoAutoriaControllerTest extends BaseControllerTest {

    @MockitoBean
    private TipoAutoriaService tipoAutoriaService;

    @Test
    @DisplayName("GET /api/v1/tipos-autoria - Should return active types")
    void shouldReturnActive() throws Exception {
        Page<TipoAutoriaResumen> page = new PageImpl<>(List.of());
        when(tipoAutoriaService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/tipos-autoria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de tipos de autoría activos"));
    }

    @Test
    @DisplayName("GET /api/v1/tipos-autoria/{id} - Should return type by ID")
    void shouldReturnById() throws Exception {
        TipoAutoriaResumen resumen = new TestTipoAutoriaResumen(1L, "Tipo Autoria Test", false);
        when(tipoAutoriaService.obtenerPorIdActivo(1L)).thenReturn(resumen);

        mockMvc.perform(get("/api/v1/tipos-autoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Tipo de autoría encontrado"));
    }

    @Test
    @DisplayName("POST /api/v1/tipos-autoria - Should create type")
    void shouldCreate() throws Exception {
        TipoAutoriaRequest request = new TipoAutoriaRequest();
        request.setNombre("Nuevo");

        TipoAutoriaResumen resumen = new TestTipoAutoriaResumen(1L, "Nuevo", false);
        when(tipoAutoriaService.crear(any())).thenReturn(resumen);

        mockMvc.perform(post("/api/v1/tipos-autoria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Nuevo"));
    }

    @Test
    @DisplayName("PUT /api/v1/tipos-autoria/{id} - Should update type")
    void shouldUpdate() throws Exception {
        TipoAutoriaRequest request = new TipoAutoriaRequest();
        request.setNombre("Actualizado");

        TipoAutoriaResumen resumen = new TestTipoAutoriaResumen(1L, "Actualizado", false);
        when(tipoAutoriaService.actualizar(any(Long.class), any(TipoAutoriaRequest.class))).thenReturn(resumen);

        mockMvc.perform(put("/api/v1/tipos-autoria/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Tipo de autoría actualizado correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/tipos-autoria/{id} - Should deactivate type")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/tipos-autoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Tipo de autoría desactivado correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/tipos-autoria/{id}/activar - Should activate type")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/tipos-autoria/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Tipo de autoría activado correctamente"));
    }

    @Test
    @DisplayName("GET /api/v1/tipos-autoria/admin - Should return all types for admin")
    void shouldReturnAllAdmin() throws Exception {
        Page<TipoAutoriaResumen> page = new PageImpl<>(List.of());
        when(tipoAutoriaService.obtenerTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/tipos-autoria/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de tipos de autoría"));
    }

    private static record TestTipoAutoriaResumen(
            Long id,
            String nombre,
            boolean borradoLogico
    ) implements TipoAutoriaResumen {

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

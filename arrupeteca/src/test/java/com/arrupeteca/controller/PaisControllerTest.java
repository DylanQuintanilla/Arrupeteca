package com.arrupeteca.controller;

import com.arrupeteca.dto.request.PaisRequest;
import com.arrupeteca.persistence.projection.PaisResumen;
import com.arrupeteca.service.PaisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaisController.class)
class PaisControllerTest extends BaseControllerTest {
    @MockitoBean private PaisService paisService;

    @Test
    @DisplayName("GET /api/v1/paises - Should return active countries")
    void shouldReturnActive() throws Exception {
        when(paisService.obtenerTodosActivos(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
        mockMvc.perform(get("/api/v1/paises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de países activos"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("GET /api/v1/paises/{id} - Should return country by ID")
    void shouldReturnCountryById() throws Exception {
        PaisResumen resumen = new TestPaisResumen(1L, "País Test", false);
        when(paisService.obtenerPorIdActivo(1L)).thenReturn(resumen);

        mockMvc.perform(get("/api/v1/paises/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("País encontrado"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("POST /api/v1/paises - Should create country")
    void shouldCreateCountry() throws Exception {
        PaisRequest request = new PaisRequest();
        request.setNombre("Nuevo");

        PaisResumen resumen = new TestPaisResumen(1L, "Nuevo", false);
        when(paisService.crear(any())).thenReturn(resumen);

        mockMvc.perform(post("/api/v1/paises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("País creado exitosamente"))
                .andExpect(jsonPath("$.data.nombre").value("Nuevo"));
    }

    @Test
    @DisplayName("PUT /api/v1/paises/{id} - Should update country")
    void shouldUpdateCountry() throws Exception {
        PaisRequest request = new PaisRequest();
        request.setNombre("Actualizado");

        PaisResumen resumen = new TestPaisResumen(1L, "Actualizado", false);
        when(paisService.actualizar(any(Long.class), any(PaisRequest.class))).thenReturn(resumen);

        mockMvc.perform(put("/api/v1/paises/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("País actualizado correctamente"))
                .andExpect(jsonPath("$.data.nombre").value("Actualizado"));
    }

    @Test
    @DisplayName("DELETE /api/v1/paises/{id} - Should deactivate country")
    void shouldDeactivateCountry() throws Exception {
        mockMvc.perform(delete("/api/v1/paises/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("País desactivado correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/paises/{id}/activar - Should activate country")
    void shouldActivateCountry() throws Exception {
        mockMvc.perform(patch("/api/v1/paises/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("País activado correctamente"));
    }

    @Test
    @DisplayName("GET /api/v1/paises/admin - Should return all countries for admin")
    void shouldReturnAllCountriesAdmin() throws Exception {
        Page<PaisResumen> page = new PageImpl<>(List.of());
        when(paisService.obtenerTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/paises/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de países"))
                .andExpect(jsonPath("$.data").exists());
    }

    private static record TestPaisResumen(
            Long id,
            String nombre,
            boolean borradoLogico
    ) implements PaisResumen {

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

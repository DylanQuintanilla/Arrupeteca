package com.arrupeteca.controller;

import java.util.List;

import com.arrupeteca.dto.request.NacionalidadRequest;
import com.arrupeteca.persistence.projection.NacionalidadResumen;
import com.arrupeteca.service.NacionalidadService;
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

@WebMvcTest(NacionalidadController.class)
class NacionalidadControllerTest extends BaseControllerTest {

    @MockitoBean
    private NacionalidadService nacionalidadService;

    @Test
    @DisplayName("GET /api/v1/nacionalidades - Should return active nationalities")
    void shouldReturnActive() throws Exception {
        when(nacionalidadService.obtenerTodasActivas(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
        mockMvc.perform(get("/api/v1/nacionalidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de nacionalidades activas"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("GET /api/v1/nacionalidades/{id} - Should return nationality by ID")
    void shouldReturnNationalityById() throws Exception {
        NacionalidadResumen resumen = new TestNacionalidadResumen(1L, "Nacionalidad Test", false);
        when(nacionalidadService.obtenerPorIdActivo(1L)).thenReturn(resumen);

        mockMvc.perform(get("/api/v1/nacionalidades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Nacionalidad encontrada"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("POST /api/v1/nacionalidades - Should create nationality")
    void shouldCreateNationality() throws Exception {
        NacionalidadRequest request = new NacionalidadRequest();
        request.setNombre("Nueva");

        NacionalidadResumen resumen = new TestNacionalidadResumen(1L, "Nueva", false);
        when(nacionalidadService.crear(any())).thenReturn(resumen);

        mockMvc.perform(post("/api/v1/nacionalidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Nacionalidad creada exitosamente"))
                .andExpect(jsonPath("$.data.nombre").value("Nueva"));
    }

    @Test
    @DisplayName("PUT /api/v1/nacionalidades/{id} - Should update nationality")
    void shouldUpdateNationality() throws Exception {
        NacionalidadRequest request = new NacionalidadRequest();
        request.setNombre("Actualizada");

        NacionalidadResumen resumen = new TestNacionalidadResumen(1L, "Actualizada", false);
        when(nacionalidadService.actualizar(any(Long.class), any(NacionalidadRequest.class))).thenReturn(resumen);

        mockMvc.perform(put("/api/v1/nacionalidades/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Nacionalidad actualizada correctamente"))
                .andExpect(jsonPath("$.data.nombre").value("Actualizada"));
    }

    @Test
    @DisplayName("DELETE /api/v1/nacionalidades/{id} - Should deactivate nationality")
    void shouldDeactivateNationality() throws Exception {
        mockMvc.perform(delete("/api/v1/nacionalidades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Nacionalidad desactivada correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/nacionalidades/{id}/activar - Should activate nationality")
    void shouldActivateNationality() throws Exception {
        mockMvc.perform(patch("/api/v1/nacionalidades/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Nacionalidad activada correctamente"));
    }

    @Test
    @DisplayName("GET /api/v1/nacionalidades/admin - Should return all nationalities for admin")
    void shouldReturnAllNationalitiesAdmin() throws Exception {
        Page<NacionalidadResumen> page = new PageImpl<>(List.of());
        when(nacionalidadService.obtenerTodas(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/nacionalidades/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de nacionalidades"))
                .andExpect(jsonPath("$.data").exists());
    }

    private static record TestNacionalidadResumen(
            Long id,
            String nombre,
            boolean borradoLogico
    ) implements NacionalidadResumen {

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

package com.arrupeteca.controller;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.persistence.projection.PlantaResumen;
import com.arrupeteca.service.PlantaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlantaController.class)
class PlantaControllerTest extends BaseControllerTest {

    @MockitoBean
    private PlantaService plantaService;

    @Test
    @DisplayName("GET /api/v1/plantas - Should return active plants page")
    void obtenerTodosActivos() throws Exception {
        when(plantaService.obtenerTodosActivos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/plantas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de plantas activas"));
    }

    @Test
    @DisplayName("GET /api/v1/plantas/{id} - Should return plant when active")
    void obtenerPorId() throws Exception {
        when(plantaService.obtenerPorIdActivo(1L))
                .thenReturn(null);

        mockMvc.perform(get("/api/v1/plantas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Planta encontrada"));
    }

    @Test
    @DisplayName("GET /api/v1/plantas/edificio/{id} - Should return building plants")
    void obtenerPorEdificio() throws Exception {
        when(plantaService.obtenerPorIdEdificio(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/plantas/edificio/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Plantas del edificio"));
    }

    @Test
    @DisplayName("POST /api/v1/plantas - Should create plant")
    void crear() throws Exception {
        PlantaRequest request = new PlantaRequest();
        request.setNombre("Planta 1");
        request.setIdEdificio(1L);

        when(plantaService.crear(any(PlantaRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/plantas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Planta creada exitosamente"));
    }

    @Test
    @DisplayName("PUT /api/v1/plantas/{id} - Should update plant")
    void actualizar() throws Exception {
        PlantaRequest request = new PlantaRequest();
        request.setNombre("Planta 1");
        request.setIdEdificio(1L);

        when(plantaService.actualizar(eq(1L), any(PlantaRequest.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/plantas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Planta actualizada correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/plantas/{id} - Should deactivate plant")
    void desactivar() throws Exception {
        mockMvc.perform(delete("/api/v1/plantas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Planta desactivada correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/plantas/{id}/activar - Should activate plant")
    void activar() throws Exception {
        mockMvc.perform(patch("/api/v1/plantas/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Planta activada correctamente"));
    }

    @Test
    @DisplayName("GET /api/v1/plantas/admin - Should return all plants for admin")
    void obtenerTodosAdmin() throws Exception {
        when(plantaService.obtenerTodos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/plantas/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de plantas"));
    }
}

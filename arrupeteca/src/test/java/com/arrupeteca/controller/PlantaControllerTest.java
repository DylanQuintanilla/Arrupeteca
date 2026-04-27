package com.arrupeteca.controller;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.dto.response.detail.PlantaDetalleResponse;
import com.arrupeteca.dto.response.summary.PlantaResumen;
import com.arrupeteca.service.PlantaService;
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

@WebMvcTest(PlantaController.class)
class PlantaControllerTest extends BaseControllerTest {

    @MockitoBean
    private PlantaService plantaService;

    @Test
    @DisplayName("GET /api/v1/plantas - Should return active plants")
    void shouldReturnActive() throws Exception {
        Page<PlantaResumen> page = new PageImpl<>(List.of());
        when(plantaService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/plantas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/plantas/{id} - Should return plant by ID")
    void shouldReturnById() throws Exception {
        PlantaDetalleResponse detail = new PlantaDetalleResponse(1L, "Planta 1", false, "admin", null, "admin", null, null);
        when(plantaService.obtenerPorIdActivo(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/plantas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Planta 1"));
    }

    @Test
    @DisplayName("GET /api/v1/plantas/admin - Should return all plants")
    void shouldReturnAllAdmin() throws Exception {
        Page<PlantaResumen> page = new PageImpl<>(List.of());
        when(plantaService.obtenerTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/plantas/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/plantas/admin/{id} - Should return any plant by ID")
    void shouldReturnByIdAdmin() throws Exception {
        PlantaDetalleResponse detail = new PlantaDetalleResponse(1L, "Planta 1", true, "admin", null, "admin", null, null);
        when(plantaService.obtenerPorId(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/plantas/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Planta 1"));
    }

    @Test
    @DisplayName("GET /api/v1/plantas/edificio/{idEdificio} - Should return plants by edificio")
    void shouldReturnByEdificio() throws Exception {
        Page<PlantaResumen> page = new PageImpl<>(List.of());
        when(plantaService.buscarPorEdificioId(eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/plantas/edificio/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/plantas - Should create plant")
    void shouldCreate() throws Exception {
        PlantaRequest request = new PlantaRequest();
        request.setNombre("Nuevo");
        request.setIdEdificio(1L);

        PlantaDetalleResponse detail = new PlantaDetalleResponse(1L, "Nuevo", false, "admin", null, "admin", null, null);
        when(plantaService.crear(any())).thenReturn(detail);

        mockMvc.perform(post("/api/v1/plantas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/plantas/{id} - Should update plant")
    void shouldUpdate() throws Exception {
        PlantaRequest request = new PlantaRequest();
        request.setNombre("Actualizado");
        request.setIdEdificio(1L);

        PlantaDetalleResponse detail = new PlantaDetalleResponse(1L, "Actualizado", false, "admin", null, "admin", null, null);
        when(plantaService.actualizar(eq(1L), any())).thenReturn(detail);

        mockMvc.perform(put("/api/v1/plantas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("DELETE /api/v1/plantas/{id} - Should deactivate plant")
    void shouldDeactivate() throws Exception {
        doNothing().when(plantaService).cambiarEstado(1L, false);

        mockMvc.perform(delete("/api/v1/plantas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/plantas/{id}/activar - Should activate plant")
    void shouldActivate() throws Exception {
        doNothing().when(plantaService).cambiarEstado(1L, true);

        mockMvc.perform(patch("/api/v1/plantas/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}

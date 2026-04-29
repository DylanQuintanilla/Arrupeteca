package com.arrupeteca.controller;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.dto.response.detail.SalonDetalleResponse;
import com.arrupeteca.dto.response.summary.SalonResumen;
import com.arrupeteca.service.SalonService;
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

@WebMvcTest(SalonController.class)
class SalonControllerTest extends BaseControllerTest {

    @MockitoBean
    private SalonService salonService;

    @Test
    @DisplayName("GET /api/v1/salones - Should return active rooms")
    void shouldReturnActive() throws Exception {
        Page<SalonResumen> page = new PageImpl<>(List.of());
        when(salonService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/salones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/salones/{id} - Should return room by ID")
    void shouldReturnById() throws Exception {
        SalonDetalleResponse detail = new SalonDetalleResponse(1L, "Salon A", false, "admin", null, "admin", null, null);
        when(salonService.obtenerPorIdActivo(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/salones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Salon A"));
    }

    @Test
    @DisplayName("GET /api/v1/salones/admin - Should return all rooms")
    void shouldReturnAllAdmin() throws Exception {
        Page<SalonResumen> page = new PageImpl<>(List.of());
        when(salonService.obtenerTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/salones/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/salones/admin/{id} - Should return any room by ID")
    void shouldReturnByIdAdmin() throws Exception {
        SalonDetalleResponse detail = new SalonDetalleResponse(1L, "Salon A", true, "admin", null, "admin", null, null);
        when(salonService.obtenerPorId(1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/salones/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Salon A"));
    }

    @Test
    @DisplayName("GET /api/v1/salones/planta/{idPlanta} - Should return rooms by planta")
    void shouldReturnByPlanta() throws Exception {
        Page<SalonResumen> page = new PageImpl<>(List.of());
        when(salonService.buscarPorPlantaId(eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/salones/planta/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/salones - Should create room")
    void shouldCreate() throws Exception {
        SalonRequest request = new SalonRequest();
        request.setNombre("Nuevo");
        request.setIdPlanta(1L);

        SalonDetalleResponse detail = new SalonDetalleResponse(1L, "Nuevo", false, "admin", null, "admin", null, null);
        when(salonService.crear(any())).thenReturn(detail);

        mockMvc.perform(post("/api/v1/salones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/salones/{id} - Should update room")
    void shouldUpdate() throws Exception {
        SalonRequest request = new SalonRequest();
        request.setNombre("Actualizado");
        request.setIdPlanta(1L);

        SalonDetalleResponse detail = new SalonDetalleResponse(1L, "Actualizado", false, "admin", null, "admin", null, null);
        when(salonService.actualizar(eq(1L), any())).thenReturn(detail);

        mockMvc.perform(put("/api/v1/salones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("DELETE /api/v1/salones/{id} - Should deactivate room")
    void shouldDeactivate() throws Exception {
        doNothing().when(salonService).cambiarEstado(1L, false);

        mockMvc.perform(delete("/api/v1/salones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PATCH /api/v1/salones/{id}/activar - Should activate room")
    void shouldActivate() throws Exception {
        doNothing().when(salonService).cambiarEstado(1L, true);

        mockMvc.perform(patch("/api/v1/salones/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }
}

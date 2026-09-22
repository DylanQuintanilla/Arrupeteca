package com.arrupeteca.controller;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.dto.response.EdificioResponse;
import com.arrupeteca.dto.response.PlantaResponse;
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
        Page<PlantaResponse> page = new PageImpl<>(List.of());
        when(plantaService.obtenerTodosActivos(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/plantas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/plantas/{id} - Should return plant by ID")
    void shouldReturnById() throws Exception {
        PlantaResponse response = new PlantaResponse(1L, "Test", false, new EdificioResponse(1L, "Edif", false));
        when(plantaService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/plantas/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Test"));
    }

    @Test
    @DisplayName("POST /api/v1/plantas - Should create plant")
    void shouldCreate() throws Exception {
        PlantaRequest request = PlantaRequest.builder().nombre("Nuevo").idEdificio(1L).build();
        PlantaResponse response = new PlantaResponse(1L, "Nuevo", false, new EdificioResponse(1L, "Edif", false));

        when(plantaService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/plantas/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/plantas/{id} - Should update plant")
    void shouldUpdate() throws Exception {
        PlantaRequest request = PlantaRequest.builder().nombre("Actualizado").idEdificio(1L).build();
        PlantaResponse response = new PlantaResponse(1L, "Actualizado", false, new EdificioResponse(1L, "Edif", false));

        when(plantaService.actualizar(any(Long.class), any(PlantaRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/plantas/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/plantas/admin - Should return all plants for admin")
    void shouldReturnAllAdmin() throws Exception {
        Page<PlantaResponse> page = new PageImpl<>(List.of());
        when(plantaService.obtenerTodos(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/plantas/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Tabla de plantas de edificios"));
    }

    @Test
    @DisplayName("GET /api/v1/plantas/admin/{id} - Should return plant by ID for admin")
    void shouldReturnByIdAdmin() throws Exception {
        PlantaResponse response = new PlantaResponse(1L, "Test", false, new EdificioResponse(1L, "Edif", false));
        when(plantaService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/plantas/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Planta encontrado(a)"));
    }

    @Test
    @DisplayName("DELETE /api/v1/plantas/{id} - Should deactivate plant")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/plantas/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Planta desactivado(a) correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/plantas/{id}/activar - Should activate plant")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/plantas/admin/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Planta activado(a) correctamente"));
    }
}

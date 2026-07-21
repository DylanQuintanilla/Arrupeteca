package com.arrupeteca.controller;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.dto.response.EdificioResponse;
import com.arrupeteca.dto.response.PlantaResponse;
import com.arrupeteca.dto.response.SalonResponse;
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
    @DisplayName("GET /api/v1/salones - Should return active salones")
    void shouldReturnActive() throws Exception {
        Page<SalonResponse> page = new PageImpl<>(List.of());
        when(salonService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/salones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/salones/{id} - Should return salon by ID")
    void shouldReturnById() throws Exception {
        SalonResponse response = new SalonResponse(1L, "Test", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false)));
        when(salonService.obtenerPorIdActivo(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/salones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Test"));
    }

    @Test
    @DisplayName("GET /api/v1/salones/planta/{idPlanta} - Should return salones by plant ID")
    void shouldReturnByPlantaId() throws Exception {
        Page<SalonResponse> page = new PageImpl<>(List.of());
        when(salonService.buscarPorPlantaId(any(Long.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/salones/planta/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/salones - Should create salon")
    void shouldCreate() throws Exception {
        SalonRequest request = SalonRequest.builder().nombre("Nuevo").idPlanta(1L).build();
        SalonResponse response = new SalonResponse(1L, "Nuevo", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false)));

        when(salonService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/salones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/salones/{id} - Should update salon")
    void shouldUpdate() throws Exception {
        SalonRequest request = SalonRequest.builder().nombre("Actualizado").idPlanta(1L).build();
        SalonResponse response = new SalonResponse(1L, "Actualizado", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false)));

        when(salonService.actualizar(any(Long.class), any(SalonRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/salones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/salones/admin - Should return all salones for admin")
    void shouldReturnAllAdmin() throws Exception {
        Page<SalonResponse> page = new PageImpl<>(List.of());
        when(salonService.obtenerTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/salones/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de salones"));
    }

    @Test
    @DisplayName("GET /api/v1/salones/admin/{id} - Should return salon by ID for admin")
    void shouldReturnByIdAdmin() throws Exception {
        SalonResponse response = new SalonResponse(1L, "Test", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false)));
        when(salonService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/salones/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Salón encontrado (admin)"));
    }

    @Test
    @DisplayName("DELETE /api/v1/salones/{id} - Should deactivate salon")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/salones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Salón desactivado correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/salones/{id}/activar - Should activate salon")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/salones/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Salón activado correctamente"));
    }
}

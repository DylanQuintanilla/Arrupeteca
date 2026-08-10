package com.arrupeteca.controller;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.dto.response.EdificioResponse;
import com.arrupeteca.dto.response.MuebleResponse;
import com.arrupeteca.dto.response.PlantaResponse;
import com.arrupeteca.dto.response.SalonResponse;
import com.arrupeteca.service.MuebleService;
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

@WebMvcTest(MuebleController.class)
class MuebleControllerTest extends BaseControllerTest {

    @MockitoBean
    private MuebleService muebleService;

    @Test
    @DisplayName("GET /api/v1/muebles - Should return active muebles")
    void shouldReturnActive() throws Exception {
        Page<MuebleResponse> page = new PageImpl<>(List.of());
        when(muebleService.obtenerTodosActivos(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/muebles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/muebles/{id} - Should return mueble by ID")
    void shouldReturnById() throws Exception {
        MuebleResponse response = new MuebleResponse(1L, "Test", false, new SalonResponse(1L, "Salon", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false))));
        when(muebleService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/muebles/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.data.nombre").value("Test"));
    }

    @Test
    @DisplayName("POST /api/v1/muebles - Should create mueble")
    void shouldCreate() throws Exception {
        MuebleRequest request = MuebleRequest.builder().nombre("Nuevo").idSalon(1L).build();
        MuebleResponse response = new MuebleResponse(1L, "Nuevo", false, new SalonResponse(1L, "Salon", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false))));

        when(muebleService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/muebles/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("PUT /api/v1/muebles/{id} - Should update mueble")
    void shouldUpdate() throws Exception {
        MuebleRequest request = MuebleRequest.builder().nombre("Actualizado").idSalon(1L).build();
        MuebleResponse response = new MuebleResponse(1L, "Actualizado", false, new SalonResponse(1L, "Salon", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false))));

        when(muebleService.actualizar(any(Long.class), any(MuebleRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/muebles/admin/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/muebles/admin - Should return all muebles for admin")
    void shouldReturnAllAdmin() throws Exception {
        Page<MuebleResponse> page = new PageImpl<>(List.of());
        when(muebleService.obtenerTodos(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/muebles/admin/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Tabla de muebles"));
    }

    @Test
    @DisplayName("GET /api/v1/muebles/admin/{id} - Should return mueble by ID for admin")
    void shouldReturnByIdAdmin() throws Exception {
        MuebleResponse response = new MuebleResponse(1L, "Test", false, new SalonResponse(1L, "Salon", false, new PlantaResponse(1L, "Planta", false, new EdificioResponse(1L, "Edif", false))));
        when(muebleService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/muebles/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Mueble encontrado(a)"));
    }

    @Test
    @DisplayName("DELETE /api/v1/muebles/{id} - Should deactivate mueble")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/muebles/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Mueble desactivado(a) correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/muebles/{id}/activar - Should activate mueble")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/muebles/admin/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Mueble activado(a) correctamente"));
    }
}

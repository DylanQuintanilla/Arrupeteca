package com.arrupeteca.controller;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.persistence.projection.SalonResumen;
import com.arrupeteca.service.SalonService;
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
    @DisplayName("GET /api/v1/salones - Should return active salons page")
    void obtenerTodosActivos() throws Exception {
        when(salonService.obtenerTodosActivos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/salones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de salones activos"));
    }

    @Test
    @DisplayName("GET /api/v1/salones/{id} - Should return salon when active")
    void obtenerPorId() throws Exception {
        when(salonService.obtenerPorIdActivo(1L))
                .thenReturn(null); // Assuming projection can be null or mock it properly

        mockMvc.perform(get("/api/v1/salones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Salón encontrado"));
    }

    @Test
    @DisplayName("GET /api/v1/salones/planta/{id} - Should return floor salons")
    void obtenerPorPlanta() throws Exception {
        when(salonService.obtenerPorIdPlanta(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/salones/planta/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Salones de la planta"));
    }

    @Test
    @DisplayName("POST /api/v1/salones - Should create salon")
    void crear() throws Exception {
        SalonRequest request = new SalonRequest();
        request.setNombre("Salon A");
        request.setIdPlanta(1L);

        when(salonService.crear(any(SalonRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/salones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Salón creado exitosamente"));
    }

    @Test
    @DisplayName("PUT /api/v1/salones/{id} - Should update salon")
    void actualizar() throws Exception {
        SalonRequest request = new SalonRequest();
        request.setNombre("Salon A");
        request.setIdPlanta(1L);

        when(salonService.actualizar(eq(1L), any(SalonRequest.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/salones/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Salón actualizado correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/salones/{id} - Should deactivate salon")
    void desactivar() throws Exception {
        mockMvc.perform(delete("/api/v1/salones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Salón desactivado correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/salones/{id}/activar - Should activate salon")
    void activar() throws Exception {
        mockMvc.perform(patch("/api/v1/salones/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Salón activado correctamente"));
    }

    @Test
    @DisplayName("GET /api/v1/salones/admin - Should return all salons for admin")
    void obtenerTodosAdmin() throws Exception {
        when(salonService.obtenerTodos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/salones/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de salones"));
    }
}

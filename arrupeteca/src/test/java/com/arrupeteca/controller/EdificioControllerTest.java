package com.arrupeteca.controller;

import java.util.List;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.service.EdificioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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

@WebMvcTest(EdificioController.class)
class EdificioControllerTest extends BaseControllerTest {

    @MockitoBean
    private EdificioService edificioService;

    @Test
    @DisplayName("GET /api/v1/edificios - Should return active buildings page")
    void obtenerTodosActivos() throws Exception {
        when(edificioService.obtenerTodosActivos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/edificios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de edificios activos"));
    }

    @Test
    @DisplayName("GET /api/v1/edificios/{id} - Should return building when active")
    void obtenerPorId() throws Exception {
        when(edificioService.obtenerPorIdActivo(1L))
                .thenReturn(null);

        mockMvc.perform(get("/api/v1/edificios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Edificio encontrado"));
    }

    @Test
    @DisplayName("POST /api/v1/edificios - Should create building")
    void crear() throws Exception {
        EdificioRequest request = new EdificioRequest();
        request.setNombre("Edificio Central");

        when(edificioService.crear(any(EdificioRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/edificios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Edificio creado exitosamente"));
    }

    @Test
    @DisplayName("PUT /api/v1/edificios/{id} - Should update building")
    void actualizar() throws Exception {
        EdificioRequest request = new EdificioRequest();
        request.setNombre("Edificio Norte");

        when(edificioService.actualizar(eq(1L), any(EdificioRequest.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/edificios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Edificio actualizado correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/edificios/{id} - Should deactivate building")
    void desactivar() throws Exception {
        mockMvc.perform(delete("/api/v1/edificios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Edificio desactivado correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/edificios/{id}/activar - Should activate building")
    void activar() throws Exception {
        mockMvc.perform(patch("/api/v1/edificios/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Edificio activado correctamente"));
    }

    @Test
    @DisplayName("GET /api/v1/edificios/admin - Should return all buildings for admin")
    void obtenerTodosAdmin() throws Exception {
        when(edificioService.obtenerTodos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/edificios/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de edificios"));
    }
}

package com.arrupeteca.controller;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.persistence.projection.MuebleResumen;
import com.arrupeteca.service.MuebleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MuebleController.class)
class MuebleControllerTest extends BaseControllerTest {

    @MockitoBean
    private MuebleService muebleService;

    @Test
    @DisplayName("GET /api/v1/muebles - Should return active muebles page")
    void obtenerTodosActivos() throws Exception {
        when(muebleService.obtenerTodosActivos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/muebles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de muebles activos"));
    }

    @Test
    @DisplayName("GET /api/v1/muebles/{id} - Should return mueble when active")
    void obtenerPorId() throws Exception {
        when(muebleService.obtenerPorIdActivo(1L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/muebles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Mueble encontrado"));
    }

    @Test
    @DisplayName("GET /api/v1/muebles/busqueda - Should return search results")
    void busquedaGlobal() throws Exception {
        when(muebleService.busquedaGlobal(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/muebles/busqueda").param("palabra", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Resultados de búsqueda"));
    }

    @Test
    @DisplayName("POST /api/v1/muebles - Should create mueble")
    void crear() throws Exception {
        MuebleRequest request = new MuebleRequest();
        request.setNombre("Estante 1");
        request.setIdSalon(1L);

        when(muebleService.crear(any(MuebleRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/muebles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Mueble creado exitosamente"));
    }

    @Test
    @DisplayName("PUT /api/v1/muebles/{id} - Should update mueble")
    void actualizar() throws Exception {
        MuebleRequest request = new MuebleRequest();
        request.setNombre("Estante 1");
        request.setIdSalon(1L);

        when(muebleService.actualizar(eq(1L), any(MuebleRequest.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/muebles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Mueble actualizado correctamente"));
    }

    @Test
    @DisplayName("DELETE /api/v1/muebles/{id} - Should deactivate mueble")
    void desactivar() throws Exception {
        mockMvc.perform(delete("/api/v1/muebles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Mueble desactivado correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/muebles/{id}/activar - Should activate mueble")
    void activar() throws Exception {
        mockMvc.perform(patch("/api/v1/muebles/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Mueble activado correctamente"));
    }

    @Test
    @DisplayName("GET /api/v1/muebles/admin - Should return all muebles for admin")
    void obtenerTodosAdmin() throws Exception {
        when(muebleService.obtenerTodos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/muebles/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de muebles"));
    }

    @Test
    @DisplayName("GET /api/v1/muebles/admin/busqueda-maestra - Should return master search results")
    void busquedaMaestra() throws Exception {
        when(muebleService.busquedaMaestraAdmin(anyBoolean(), anyLong(), anyLong(), anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/muebles/admin/busqueda-maestra")
                .param("termino", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Resultados búsqueda maestra"));
    }
}

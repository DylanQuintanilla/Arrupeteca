package com.arrupeteca.controller;

import java.util.List;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.persistence.projection.EditorialResumen;
import com.arrupeteca.service.EditorialService;
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

@WebMvcTest(EditorialController.class)
class EditorialControllerTest extends BaseControllerTest {

    @MockitoBean
    private EditorialService editorialService;

    @Test
    @DisplayName("GET /api/v1/editoriales - Should return active editoriales")
    void shouldReturnActive() throws Exception {
        Page<EditorialResumen> page = new PageImpl<>(List.of());
        when(editorialService.obtenerTodosActivos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/editoriales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Lista de editoriales activas"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("GET /api/v1/editoriales/{id} - Should return editorial by ID")
    void shouldReturnById() throws Exception {
        EditorialResumen resumen = new TestEditorialResumen(1L, "Editorial Test", "Calle 123", "12345678",
                "test@editorial.com", false);
        when(editorialService.obtenerPorIdActivo(1L)).thenReturn(resumen);

        mockMvc.perform(get("/api/v1/editoriales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Editorial encontrada"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("POST /api/v1/editoriales - Should create editorial")
    void shouldCreate() throws Exception {
        EditorialRequest request = new EditorialRequest();
        request.setNombre("Nueva");
        request.setCorreo("nueva@test.com");
        request.setTelefono("1234-5678");
        request.setPersonaContacto("Contacto");

        EditorialResumen resumen = new TestEditorialResumen(1L, "Nueva", "Direccion", "1234-5678",
                "nueva@test.com", false);
        when(editorialService.crear(any())).thenReturn(resumen);

        mockMvc.perform(post("/api/v1/editoriales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Editorial creada exitosamente"))
                .andExpect(jsonPath("$.data.nombre").value("Nueva"));
    }

    @Test
    @DisplayName("PUT /api/v1/editoriales/{id} - Should update editorial")
    void shouldUpdate() throws Exception {
        EditorialRequest request = new EditorialRequest();
        request.setNombre("Actualizada");
        request.setCorreo("test@test.com");
        request.setTelefono("1234-5678");
        request.setPersonaContacto("Contacto");

        EditorialResumen resumen = new TestEditorialResumen(1L, "Actualizada", "Direccion", "1234-5678",
                "test@test.com", false);
        when(editorialService.actualizar(any(Long.class), any(EditorialRequest.class))).thenReturn(resumen);

        mockMvc.perform(put("/api/v1/editoriales/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Editorial actualizada correctamente"))
                .andExpect(jsonPath("$.data.nombre").value("Actualizada"));
    }

    @Test
    @DisplayName("DELETE /api/v1/editoriales/{id} - Should deactivate editorial")
    void shouldDeactivate() throws Exception {
        mockMvc.perform(delete("/api/v1/editoriales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Editorial desactivada correctamente"));
    }

    @Test
    @DisplayName("PATCH /api/v1/editoriales/{id}/activar - Should activate editorial")
    void shouldActivate() throws Exception {
        mockMvc.perform(patch("/api/v1/editoriales/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Editorial activada correctamente"));
    }

    @Test
    @DisplayName("GET /api/v1/editoriales/admin - Should return all editoriales for admin")
    void shouldReturnAllAdmin() throws Exception {
        Page<EditorialResumen> page = new PageImpl<>(List.of());
        when(editorialService.obtenerTodos(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/editoriales/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Historial completo de editoriales"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("GET /api/v1/editoriales/admin/busqueda-maestra - Should perform master search")
    void shouldPerformMasterSearch() throws Exception {
        Page<EditorialResumen> page = new PageImpl<>(List.of());
        when(editorialService.busquedaMaestraAdmin(any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/editoriales/admin/busqueda-maestra")
                        .param("esBorrado", "false")
                        .param("termino", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Resultados búsqueda maestra"))
                .andExpect(jsonPath("$.data").exists());
    }

    private record TestEditorialResumen(
            Long id,
            String nombre,
            String direccion,
            String telefono,
            String correo,
            boolean borradoLogico
    ) implements EditorialResumen {

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public String getNombre() {
            return nombre;
        }

        @Override
        public String getDireccion() {
            return direccion;
        }

        @Override
        public String getTelefono() {
            return telefono;
        }

        @Override
        public String getCorreo() {
            return correo;
        }

        @Override
        public boolean getBorradoLogico() {
            return borradoLogico;
        }
    }
}

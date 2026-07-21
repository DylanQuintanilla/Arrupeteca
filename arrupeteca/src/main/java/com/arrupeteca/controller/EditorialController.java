package com.arrupeteca.controller;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.EditorialResponse;
import com.arrupeteca.service.EditorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/editoriales")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Editoriales", description = "Endpoints para la gestión del catálogo de editoriales")
public class EditorialController {

    private final EditorialService editorialService;

    @GetMapping
    @Operation(
        summary = "Obtener editoriales activas paginadas",
        description = "Retorna una lista paginada de todas las editoriales activas en el sistema."
    )
    public ResponseEntity<ApiResponse<Page<EditorialResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de editoriales activas", editorialService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener editorial activa por ID",
        description = "Busca y retorna una editorial activa por su identificador único."
    )
    public ResponseEntity<ApiResponse<EditorialResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Editorial encontrada", editorialService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todas las editoriales (Admin)",
        description = "Retorna el historial completo de editoriales (activas e inactivas) para administradores."
    )
    public ResponseEntity<ApiResponse<Page<EditorialResponse>>> obtenerTodos(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de editoriales", editorialService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener editorial por ID (Admin)",
        description = "Busca y retorna cualquier editorial (activa o inactiva) por su identificador único para administradores."
    )
    public ResponseEntity<ApiResponse<EditorialResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Editorial encontrada (admin)", editorialService.obtenerPorId(id)));
    }

    @GetMapping("/admin/busqueda-maestra")
    @Operation(
        summary = "Búsqueda maestra de editoriales (Admin)",
        description = "Realiza una búsqueda avanzada de editoriales filtrando por estado y término clave."
    )
    public ResponseEntity<ApiResponse<Page<EditorialResponse>>> busquedaMaestraAdmin(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) String termino,
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra",
                editorialService.busquedaMaestraAdmin(esBorrado, termino, pageable)));
    }

    @PostMapping
    @Operation(
        summary = "Crear nueva editorial",
        description = "Crea una nueva editorial validando la unicidad del nombre y del correo."
    )
    public ResponseEntity<ApiResponse<EditorialResponse>> crear(@Valid @RequestBody EditorialRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Editorial creada exitosamente",
                editorialService.crear(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar editorial",
        description = "Actualiza los datos de una editorial existente."
    )
    public ResponseEntity<ApiResponse<EditorialResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody EditorialRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Editorial actualizada correctamente",
                editorialService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar editorial",
        description = "Realiza el borrado lógico de una editorial en el sistema."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        editorialService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Editorial desactivada correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar editorial",
        description = "Reactiva una editorial previamente desactivada."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        editorialService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Editorial activada correctamente"));
    }
}

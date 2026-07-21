package com.arrupeteca.controller;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.FormatoResponse;
import com.arrupeteca.service.FormatoService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/formatos")
@Tag(name = "Formatos", description = "Endpoints para la gestión del catálogo de formatos de libros")
public class FormatoController {

    private final FormatoService formatoService;

    @GetMapping
    @Operation(
        summary = "Obtener formatos activos paginados",
        description = "Retorna una lista paginada de todos los formatos activos en el sistema."
    )
    public ResponseEntity<ApiResponse<Page<FormatoResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<FormatoResponse> pagina = formatoService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de formatos activos", pagina));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener formato activo por ID",
        description = "Busca y retorna un formato activo por su identificador único."
    )
    public ResponseEntity<ApiResponse<FormatoResponse>> obtenerPorId(@PathVariable Long id) {
        FormatoResponse formato = formatoService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Formato encontrado", formato));
    }

    @PostMapping
    @Operation(
        summary = "Crear nuevo formato",
        description = "Crea un nuevo formato validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<FormatoResponse>> crear(@Valid @RequestBody FormatoRequest request) {
        FormatoResponse nuevo = formatoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Formato creado exitosamente", nuevo));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar formato",
        description = "Actualiza los datos de un formato existente."
    )
    public ResponseEntity<ApiResponse<FormatoResponse>> actualizar(@PathVariable Long id,
                                                                   @Valid @RequestBody FormatoRequest request) {
        FormatoResponse actualizado = formatoService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Formato actualizado correctamente", actualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar formato",
        description = "Realiza el borrado lógico de un formato en el sistema."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        formatoService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Formato desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar formato",
        description = "Reactiva un formato previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        formatoService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Formato activado correctamente"));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todos los formatos (Admin)",
        description = "Retorna el historial completo de formatos (activos e inactivos) para administradores."
    )
    public ResponseEntity<ApiResponse<Page<FormatoResponse>>> obtenerTodosAdmin(
            @ParameterObject @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.exito("Historial completo de formatos", formatoService.obtenerTodos(pageable))
        );
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener formato por ID (Admin)",
        description = "Busca y retorna cualquier formato (activo o inactivo) por su identificador único para administradores."
    )
    public ResponseEntity<ApiResponse<FormatoResponse>> obtenerPorIdAdmin(@PathVariable Long id) {
        FormatoResponse formato = formatoService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.exito("Formato encontrado (admin)", formato));
    }
}

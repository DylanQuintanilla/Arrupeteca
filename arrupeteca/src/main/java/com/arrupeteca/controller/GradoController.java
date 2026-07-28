package com.arrupeteca.controller;

import com.arrupeteca.dto.request.GradoRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.GradoResponse;
import com.arrupeteca.service.GradoService;
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
@RequestMapping("/api/v1/grados")
@RequiredArgsConstructor
@Tag(name = "Grados", description = "Endpoints para la gestión de grados académicos")
public class GradoController {

    private final GradoService gradoService;

    @GetMapping
    @Operation(
        summary = "Obtener grados activos",
        description = "Retorna una página de grados registrados que no están borrados lógicamente."
    )
    public ResponseEntity<ApiResponse<Page<GradoResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de grados activos", gradoService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle de grado activo",
        description = "Busca la información de un grado activo por su ID."
    )
    public ResponseEntity<ApiResponse<GradoResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Grado encontrado", gradoService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todos los grados (Admin)",
        description = "Retorna todos los grados incluyendo los eliminados lógicamente (solo admin)."
    )
    public ResponseEntity<ApiResponse<Page<GradoResponse>>> obtenerTodos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de grados", gradoService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener grado por ID (Admin)",
        description = "Busca el detalle de un grado por su ID sin importar su estado lógico (solo admin)."
    )
    public ResponseEntity<ApiResponse<GradoResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Grado encontrado (admin)", gradoService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(
        summary = "Registrar nuevo grado",
        description = "Crea un nuevo grado en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<GradoResponse>> crear(@Valid @RequestBody GradoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Grado creado exitosamente", gradoService.crear(request)));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar grado",
        description = "Actualiza los campos de un grado existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<GradoResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody GradoRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Grado actualizado correctamente",
                gradoService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar grado (Borrado lógico)",
        description = "Oculta el grado en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        gradoService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Grado desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar grado",
        description = "Restaura la visibilidad pública de un grado previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        gradoService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Grado activado correctamente"));
    }
}

package com.arrupeteca.controller;

import com.arrupeteca.dto.request.ParentescoRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.ParentescoResponse;
import com.arrupeteca.service.ParentescoService;
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
@RequestMapping("/api/v1/parentescos")
@RequiredArgsConstructor
@Tag(name = "Parentescos", description = "Endpoints para la gestión del catálogo de parentescos")
public class ParentescoController {

    private final ParentescoService parentescoService;

    @GetMapping
    @Operation(
        summary = "Obtener parentescos activos",
        description = "Retorna una página de parentescos registrados que no están borrados lógicamente."
    )
    public ResponseEntity<ApiResponse<Page<ParentescoResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de parentescos activos", parentescoService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle de parentesco activo",
        description = "Busca la información de un parentesco activo por su ID."
    )
    public ResponseEntity<ApiResponse<ParentescoResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Parentesco encontrado", parentescoService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todos los parentescos (Admin)",
        description = "Retorna todos los parentescos incluyendo los eliminados lógicamente."
    )
    public ResponseEntity<ApiResponse<Page<ParentescoResponse>>> obtenerTodos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de parentescos", parentescoService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener parentesco por ID (Admin)",
        description = "Busca el detalle de un parentesco por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<ParentescoResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Parentesco encontrado (admin)", parentescoService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(
        summary = "Registrar nuevo parentesco",
        description = "Crea un nuevo parentesco validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<ParentescoResponse>> crear(@Valid @RequestBody ParentescoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Parentesco creado exitosamente", parentescoService.crear(request)));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar parentesco",
        description = "Actualiza los campos de un parentesco existente."
    )
    public ResponseEntity<ApiResponse<ParentescoResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody ParentescoRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Parentesco actualizado correctamente",
                parentescoService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar parentesco (Borrado lógico)",
        description = "Desactiva el parentesco cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        parentescoService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Parentesco desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar parentesco",
        description = "Restaura un parentesco previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        parentescoService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Parentesco activado correctamente"));
    }

}

package com.arrupeteca.controller;

import com.arrupeteca.dto.request.CicloRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.CicloResponse;
import com.arrupeteca.service.CicloService;
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
@RequestMapping("/api/v1/ciclos")
@RequiredArgsConstructor
@Tag(name = "Ciclos", description = "Endpoints para la gestión de ciclos académicos")
public class CicloController {

    private final CicloService cicloService;

    @GetMapping
    @Operation(
        summary = "Obtener ciclos activos",
        description = "Retorna una página de ciclos registrados que no están borrados lógicamente."
    )
    public ResponseEntity<ApiResponse<Page<CicloResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de ciclos activos", cicloService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle de ciclo activo",
        description = "Busca la información de un ciclo activo por su ID."
    )
    public ResponseEntity<ApiResponse<CicloResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Ciclo encontrado", cicloService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todos los ciclos (Admin)",
        description = "Retorna todos los ciclos incluyendo los eliminados lógicamente (solo admin)."
    )
    public ResponseEntity<ApiResponse<Page<CicloResponse>>> obtenerTodos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de ciclos", cicloService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener ciclo por ID (Admin)",
        description = "Busca el detalle de un ciclo por su ID sin importar su estado lógico (solo admin)."
    )
    public ResponseEntity<ApiResponse<CicloResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Ciclo encontrado (admin)", cicloService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(
        summary = "Registrar nuevo ciclo",
        description = "Crea un nuevo ciclo en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<CicloResponse>> crear(@Valid @RequestBody CicloRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Ciclo creado exitosamente", cicloService.crear(request)));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar ciclo",
        description = "Actualiza los campos de un ciclo existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<CicloResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody CicloRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Ciclo actualizado correctamente",
                cicloService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar ciclo (Borrado lógico)",
        description = "Oculta el ciclo en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        cicloService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Ciclo desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar ciclo",
        description = "Restaura la visibilidad pública de un ciclo previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        cicloService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Ciclo activado correctamente"));
    }
}

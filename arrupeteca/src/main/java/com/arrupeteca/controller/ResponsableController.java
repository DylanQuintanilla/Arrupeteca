package com.arrupeteca.controller;

import java.util.List;

import com.arrupeteca.dto.request.ResponsableRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.ResponsableResponse;
import com.arrupeteca.service.ResponsableService;
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
@RequestMapping("/api/v1/responsables")
@RequiredArgsConstructor
@Tag(name = "Responsables", description = "Endpoints para la gestión de responsables asociados a alumnos")
public class ResponsableController {

    private final ResponsableService responsableService;

    @GetMapping
    @Operation(
        summary = "Obtener responsables activos",
        description = "Retorna una página de responsables registrados que no están borrados lógicamente."
    )
    public ResponseEntity<ApiResponse<Page<ResponsableResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombres") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de responsables activos", responsableService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle de responsable activo",
        description = "Busca la información de un responsable activo por su ID."
    )
    public ResponseEntity<ApiResponse<ResponsableResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Responsable encontrado", responsableService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(
        summary = "Obtener responsables por usuario",
        description = "Retorna la lista de responsables asociados a un usuario específico."
    )
    public ResponseEntity<ApiResponse<List<ResponsableResponse>>> obtenerPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(ApiResponse.exito("Responsables encontrados para el usuario",
                responsableService.obtenerPorUsuario(idUsuario)));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todos los responsables (Admin)",
        description = "Retorna todos los responsables incluyendo los eliminados lógicamente."
    )
    public ResponseEntity<ApiResponse<Page<ResponsableResponse>>> obtenerTodos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombres") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de responsables", responsableService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener responsable por ID (Admin)",
        description = "Busca el detalle de un responsable por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<ResponsableResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Responsable encontrado (admin)", responsableService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(
        summary = "Registrar nuevo responsable",
        description = "Crea un nuevo responsable asociado a un usuario y parentesco."
    )
    public ResponseEntity<ApiResponse<ResponsableResponse>> crear(@Valid @RequestBody ResponsableRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Responsable creado exitosamente", responsableService.crear(request)));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar responsable",
        description = "Actualiza los campos de un responsable existente."
    )
    public ResponseEntity<ApiResponse<ResponsableResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody ResponsableRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Responsable actualizado correctamente",
                responsableService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar responsable (Borrado lógico)",
        description = "Desactiva el responsable cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        responsableService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Responsable desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar responsable",
        description = "Restaura un responsable previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        responsableService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Responsable activado correctamente"));
    }

}

package com.arrupeteca.prestamo.controller;

import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.prestamo.dto.request.EstadoSolicitudRequest;
import com.arrupeteca.prestamo.dto.response.EstadoSolicitudResponse;
import com.arrupeteca.prestamo.service.EstadoSolicitudService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/estados-solicitud")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Estados de Solicitud", description = "Endpoints para la gestión del catálogo de estados de solicitud y logística")
public class EstadoSolicitudController {

    private final EstadoSolicitudService estadoSolicitudService;

    @GetMapping
    @Operation(
        summary = "Obtener estados de solicitud activos para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de estados de solicitud activos, con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<EstadoSolicitudResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de estados de solicitud activos", estadoSolicitudService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de estados de solicitud",
        description = "Retorna una página de estados de solicitud con soporte para filtros por nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<EstadoSolicitudResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de estados de solicitud", estadoSolicitudService.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener estado de solicitud por ID",
        description = "Busca la información de un estado de solicitud por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<EstadoSolicitudResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Estado de solicitud encontrado", estadoSolicitudService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo estado de solicitud",
        description = "Crea un nuevo estado de solicitud en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<EstadoSolicitudResponse>> crear(@Valid @RequestBody EstadoSolicitudRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Estado de solicitud creado exitosamente", estadoSolicitudService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar estado de solicitud",
        description = "Actualiza los campos de un estado de solicitud existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<EstadoSolicitudResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody EstadoSolicitudRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Estado de solicitud actualizado correctamente",
                estadoSolicitudService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar estado de solicitud (Borrado lógico)",
        description = "Oculta el estado de solicitud en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        estadoSolicitudService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Estado de solicitud desactivado correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar estado de solicitud",
        description = "Restaura la visibilidad pública de un estado de solicitud previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        estadoSolicitudService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Estado de solicitud activado correctamente"));
    }
}


package com.arrupeteca.prestamo.controller;

import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.prestamo.dto.request.SolicitudPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.SolicitudPrestamoDetalleResponse;
import com.arrupeteca.prestamo.dto.response.summary.SolicitudPrestamoResumen;
import com.arrupeteca.prestamo.internal.service.SolicitudPrestamoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/solicitudes-prestamo")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Solicitudes de Préstamo", description = "Endpoints para la gestión de solicitudes de préstamo de libros")
public class SolicitudPrestamoController {

    private final SolicitudPrestamoService solicitudPrestamoService;

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de solicitudes de préstamo",
        description = "Retorna una página de solicitudes de préstamo con soporte para filtros por usuario, estado, rango de fechas y borrado lógico."
    )
    public ResponseEntity<ApiResponse<Page<SolicitudPrestamoResumen>>> obtenerTodos(
            @RequestParam(required = false) Boolean borradoLogico,
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) Long idEstadoSolicitud,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @ParameterObject @PageableDefault(size = 20, sort = "fechaSolicitud") Pageable pageable) {
        Page<SolicitudPrestamoResumen> resultado = solicitudPrestamoService.obtenerTodos(
                borradoLogico, idUsuario, idEstadoSolicitud, fechaInicio, fechaFin, pageable);
        return ResponseEntity.ok(ApiResponse.exito("Tabla de solicitudes de préstamo", resultado));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener solicitud de préstamo por ID",
        description = "Busca los detalles de una solicitud de préstamo por su identificador único."
    )
    public ResponseEntity<ApiResponse<SolicitudPrestamoDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        SolicitudPrestamoDetalleResponse detalle = solicitudPrestamoService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.exito("Solicitud de préstamo encontrada", detalle));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nueva solicitud de préstamo",
        description = "Crea una nueva solicitud de préstamo en el sistema validando restricciones de usuario y estado."
    )
    public ResponseEntity<ApiResponse<SolicitudPrestamoDetalleResponse>> crear(@Valid @RequestBody SolicitudPrestamoRequest request) {
        SolicitudPrestamoDetalleResponse creado = solicitudPrestamoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Solicitud de préstamo creada exitosamente", creado));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar solicitud de préstamo",
        description = "Actualiza el estado u observaciones de una solicitud de préstamo existente."
    )
    public ResponseEntity<ApiResponse<SolicitudPrestamoDetalleResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody SolicitudPrestamoRequest request) {
        SolicitudPrestamoDetalleResponse actualizado = solicitudPrestamoService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Solicitud de préstamo actualizada correctamente", actualizado));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar solicitud de préstamo (Borrado lógico)",
        description = "Marca como inactiva una solicitud de préstamo en el sistema."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        solicitudPrestamoService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Solicitud de préstamo desactivada correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar solicitud de préstamo",
        description = "Restaura la actividad de una solicitud de préstamo previamente inactiva."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        solicitudPrestamoService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Solicitud de préstamo activada correctamente"));
    }
}

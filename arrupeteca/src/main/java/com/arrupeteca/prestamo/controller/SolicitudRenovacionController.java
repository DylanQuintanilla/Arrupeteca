package com.arrupeteca.prestamo.controller;

import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.prestamo.dto.request.SolicitudRenovacionRequest;
import com.arrupeteca.prestamo.dto.response.detail.SolicitudRenovacionDetalleResponse;
import com.arrupeteca.prestamo.dto.response.summary.SolicitudRenovacionResumen;
import com.arrupeteca.prestamo.service.SolicitudRenovacionService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/renovaciones")
@RequiredArgsConstructor
@Tag(name = "Solicitudes de Renovación y Prórrogas", description = "Endpoints para la solicitud y aprobación de prórrogas de tiempo de préstamo")
public class SolicitudRenovacionController {

    private final SolicitudRenovacionService renovacionService;

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de renovaciones",
        description = "Retorna una página de solicitudes de renovación con soporte para filtros por usuario, estado y fechas."
    )
    public ResponseEntity<ApiResponse<Page<SolicitudRenovacionResumen>>> obtenerTodos(
            @RequestParam(required = false) Boolean borradoLogico,
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) Long idEstadoSolicitud,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @ParameterObject @PageableDefault(size = 20, sort = "fechaSolicitud") Pageable pageable) {
        Page<SolicitudRenovacionResumen> resultado = renovacionService.obtenerTodos(
                borradoLogico, idUsuario, idEstadoSolicitud, fechaInicio, fechaFin, pageable);
        return ResponseEntity.ok(ApiResponse.exito("Tabla de solicitudes de renovación", resultado));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener solicitud de renovación por ID",
        description = "Busca los detalles de una solicitud de renovación o prórroga por su ID único."
    )
    public ResponseEntity<ApiResponse<SolicitudRenovacionDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        SolicitudRenovacionDetalleResponse detalle = renovacionService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.exito("Solicitud de renovación encontrada", detalle));
    }

    @PostMapping("/solicitar")
    @Operation(
        summary = "Solicitar prórroga / renovación de ejemplar",
        description = "Permite a un alumno, maestro o bibliotecario registrar una solicitud de prórroga para un ejemplar prestado."
    )
    public ResponseEntity<ApiResponse<SolicitudRenovacionDetalleResponse>> solicitarRenovacion(@Valid @RequestBody SolicitudRenovacionRequest request) {
        SolicitudRenovacionDetalleResponse creado = renovacionService.solicitarRenovacion(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Solicitud de renovación enviada correctamente", creado));
    }

    @PatchMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar solicitud de renovación",
        description = "Permite actualizar parcialmente los campos de una solicitud de renovación existente."
    )
    public ResponseEntity<ApiResponse<SolicitudRenovacionDetalleResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody SolicitudRenovacionRequest request) {
        SolicitudRenovacionDetalleResponse actualizado = renovacionService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Solicitud de renovación actualizada correctamente", actualizado));
    }

    @PatchMapping("/admin/{id}/responder")
    @Operation(
        summary = "Responder / Resolver solicitud de renovación",
        description = "Permite al bibliotecario aprobar o rechazar una prórroga con motivo."
    )
    public ResponseEntity<ApiResponse<SolicitudRenovacionDetalleResponse>> responderRenovacion(
            @PathVariable Long id, @Valid @RequestBody SolicitudRenovacionRequest request) {
        SolicitudRenovacionDetalleResponse resuelto = renovacionService.responderRenovacion(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Solicitud de renovación resuelta correctamente", resuelto));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar solicitud de renovación (Borrado lógico)",
        description = "Marca como inactiva una solicitud de renovación."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        renovacionService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Solicitud de renovación desactivada correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar solicitud de renovación",
        description = "Restaura la actividad de una solicitud de renovación inactiva."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        renovacionService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Solicitud de renovación activada correctamente"));
    }
}

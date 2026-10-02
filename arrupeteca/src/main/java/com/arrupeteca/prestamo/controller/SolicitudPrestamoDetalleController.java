package com.arrupeteca.prestamo.controller;

import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.prestamo.dto.request.SolicitudPrestamoDetalleRequest;
import com.arrupeteca.prestamo.dto.response.detail.SolicitudPrestamoDetalleItemResponse;
import com.arrupeteca.prestamo.internal.service.SolicitudPrestamoDetalleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/solicitudes-prestamo-detalle")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Detalles de Solicitud de Préstamo", description = "Endpoints para la gestión de libros incluidos en una solicitud de préstamo")
public class SolicitudPrestamoDetalleController {

    private final SolicitudPrestamoDetalleService detalleService;

    @GetMapping("/solicitud/{idSolicitud}")
    @Operation(
        summary = "Obtener libros de una solicitud de préstamo",
        description = "Retorna la lista de ítems (libros y cantidades) asociados a una solicitud de préstamo específica."
    )
    public ResponseEntity<ApiResponse<List<SolicitudPrestamoDetalleItemResponse>>> obtenerPorSolicitud(@PathVariable Long idSolicitud) {
        List<SolicitudPrestamoDetalleItemResponse> lista = detalleService.obtenerPorSolicitud(idSolicitud);
        return ResponseEntity.ok(ApiResponse.exito("Detalles de la solicitud de préstamo", lista));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener detalle de solicitud por ID",
        description = "Busca la información de un ítem de detalle de solicitud por su identificador único."
    )
    public ResponseEntity<ApiResponse<SolicitudPrestamoDetalleItemResponse>> obtenerPorId(@PathVariable Long id) {
        SolicitudPrestamoDetalleItemResponse detalle = detalleService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.exito("Detalle de solicitud encontrado", detalle));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Agregar libro a solicitud de préstamo",
        description = "Agrega un nuevo ítem (libro y cantidad) a una solicitud de préstamo existente."
    )
    public ResponseEntity<ApiResponse<SolicitudPrestamoDetalleItemResponse>> agregarDetalle(@Valid @RequestBody SolicitudPrestamoDetalleRequest request) {
        SolicitudPrestamoDetalleItemResponse creado = detalleService.agregarDetalle(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Detalle de solicitud agregado correctamente", creado));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar detalle de solicitud",
        description = "Modifica la cantidad u opcionalmente el libro de un detalle existente."
    )
    public ResponseEntity<ApiResponse<SolicitudPrestamoDetalleItemResponse>> actualizarDetalle(
            @PathVariable Long id, @Valid @RequestBody SolicitudPrestamoDetalleRequest request) {
        SolicitudPrestamoDetalleItemResponse actualizado = detalleService.actualizarDetalle(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Detalle de solicitud actualizado correctamente", actualizado));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar detalle de solicitud (Borrado lógico)",
        description = "Elimina lógicamente un ítem de libro de una solicitud de préstamo."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        detalleService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Detalle de solicitud desactivado correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar detalle de solicitud",
        description = "Restaura la actividad de un ítem previamente eliminado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        detalleService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Detalle de solicitud activado correctamente"));
    }
}

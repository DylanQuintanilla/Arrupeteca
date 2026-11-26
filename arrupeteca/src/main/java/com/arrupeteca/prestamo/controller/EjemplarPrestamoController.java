package com.arrupeteca.prestamo.controller;

import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.prestamo.dto.request.EjemplarPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.EjemplarPrestamoDetalleResponse;
import com.arrupeteca.prestamo.service.EjemplarPrestamoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ejemplares-prestamo")
@RequiredArgsConstructor
@Tag(name = "Ejemplares Físicos en Préstamo", description = "Endpoints para la gestión individual y devolución de ejemplares físicos prestados")
public class EjemplarPrestamoController {

    private final EjemplarPrestamoService ejemplarPrestamoService;

    @GetMapping("/prestamo/{idPrestamo}")
    @Operation(
        summary = "Obtener ejemplares de un préstamo",
        description = "Retorna la lista de ejemplares físicos vinculados a una cabecera de préstamo."
    )
    public ResponseEntity<ApiResponse<List<EjemplarPrestamoDetalleResponse>>> obtenerPorPrestamo(@PathVariable Long idPrestamo) {
        List<EjemplarPrestamoDetalleResponse> lista = ejemplarPrestamoService.obtenerPorPrestamo(idPrestamo);
        return ResponseEntity.ok(ApiResponse.exito("Ejemplares del préstamo", lista));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener ejemplar de préstamo por ID",
        description = "Busca los detalles de devolución y moras de un ejemplar prestado por su ID único."
    )
    public ResponseEntity<ApiResponse<EjemplarPrestamoDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        EjemplarPrestamoDetalleResponse detalle = ejemplarPrestamoService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.exito("Ejemplar de préstamo encontrado", detalle));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Agregar ejemplar físico a un préstamo",
        description = "Asigna una copia física específica a un préstamo en ventanilla."
    )
    public ResponseEntity<ApiResponse<EjemplarPrestamoDetalleResponse>> agregarEjemplar(@Valid @RequestBody EjemplarPrestamoRequest request) {
        EjemplarPrestamoDetalleResponse creado = ejemplarPrestamoService.agregarEjemplar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Ejemplar agregado al préstamo correctamente", creado));
    }

    @PatchMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar estado de devolución / mora de un ejemplar",
        description = "Actualiza el estado físico de devolución, fecha real de entrega o mora calculada del ejemplar."
    )
    public ResponseEntity<ApiResponse<EjemplarPrestamoDetalleResponse>> actualizarEjemplar(
            @PathVariable Long id, @Valid @RequestBody EjemplarPrestamoRequest request) {
        EjemplarPrestamoDetalleResponse actualizado = ejemplarPrestamoService.actualizarEjemplar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Ejemplar de préstamo actualizado correctamente", actualizado));
    }

    @PatchMapping("/admin/pagar-mora/{id}")
    @Operation(
        summary = "Pagar mora acumulada por un ejemplar prestado",
        description = "Registra el pago en ventanilla de la mora acumulada por este ejemplar y retorna el detalle actualizado."
    )
    public ResponseEntity<ApiResponse<EjemplarPrestamoDetalleResponse>> pagarMora(@PathVariable Long id) {
        EjemplarPrestamoDetalleResponse actualizado = ejemplarPrestamoService.pagarMora(id);
        return ResponseEntity.ok(ApiResponse.exito("Mora del ejemplar registrada como pagada exitosamente", actualizado));
    }

    @PatchMapping("/admin/exentar-mora/{id}")
    @Operation(
        summary = "Exentar de mora un ejemplar de préstamo",
        description = "Marca el ejemplar prestado como exento de mora, establece la mora calculada en cero y retorna el detalle actualizado."
    )
    public ResponseEntity<ApiResponse<EjemplarPrestamoDetalleResponse>> exentarMora(@PathVariable Long id) {
        EjemplarPrestamoDetalleResponse actualizado = ejemplarPrestamoService.exentarMora(id);
        return ResponseEntity.ok(ApiResponse.exito("Ejemplar marcado como exento de mora exitosamente", actualizado));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar ejemplar de préstamo (Borrado lógico)",
        description = "Marca como inactivo un registro de ejemplar en préstamo."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        ejemplarPrestamoService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Ejemplar de préstamo desactivado correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar ejemplar de préstamo",
        description = "Restaura la actividad de un ejemplar en préstamo previamente inactivo."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        ejemplarPrestamoService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Ejemplar de préstamo activado correctamente"));
    }
}

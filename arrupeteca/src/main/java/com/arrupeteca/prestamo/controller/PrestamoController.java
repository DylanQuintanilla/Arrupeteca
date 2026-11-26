package com.arrupeteca.prestamo.controller;

import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.prestamo.dto.request.PrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.PrestamoDetalleResponse;
import com.arrupeteca.prestamo.dto.response.summary.PrestamoResumen;
import com.arrupeteca.prestamo.service.PrestamoService;
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
@RequestMapping("/api/v1/prestamos")
@RequiredArgsConstructor
@Tag(name = "Préstamos de Libros", description = "Endpoints para la gestión de préstamos de ejemplares en ventanilla")
public class PrestamoController {

    private final PrestamoService prestamoService;

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de préstamos",
        description = "Retorna una página de préstamos con soporte para filtros por usuario, estado, rango de fechas de préstamo y borrado lógico."
    )
    public ResponseEntity<ApiResponse<Page<PrestamoResumen>>> obtenerTodos(
            @RequestParam(required = false) Boolean borradoLogico,
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) Long idEstadoPrestamo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @ParameterObject @PageableDefault(size = 20, sort = "fechaPrestamo") Pageable pageable) {
        Page<PrestamoResumen> resultado = prestamoService.obtenerTodos(
                borradoLogico, idUsuario, idEstadoPrestamo, fechaInicio, fechaFin, pageable);
        return ResponseEntity.ok(ApiResponse.exito("Tabla de préstamos de libros", resultado));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener préstamo por ID",
        description = "Busca los detalles de un préstamo por su identificador único."
    )
    public ResponseEntity<ApiResponse<PrestamoDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        PrestamoDetalleResponse detalle = prestamoService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.exito("Préstamo encontrado", detalle));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo préstamo en ventanilla",
        description = "Crea un nuevo préstamo de libros permitiendo la asignación manual de fecha de préstamo por el bibliotecario."
    )
    public ResponseEntity<ApiResponse<PrestamoDetalleResponse>> crear(@Valid @RequestBody PrestamoRequest request) {
        PrestamoDetalleResponse creado = prestamoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Préstamo registrado exitosamente", creado));
    }

    @PatchMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar información del préstamo",
        description = "Actualiza estado, observaciones o fechas de un préstamo existente."
    )
    public ResponseEntity<ApiResponse<PrestamoDetalleResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody PrestamoRequest request) {
        PrestamoDetalleResponse actualizado = prestamoService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Préstamo actualizado correctamente", actualizado));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar préstamo (Borrado lógico)",
        description = "Marca como inactivo un registro de préstamo en el sistema."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        prestamoService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Préstamo desactivado correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar préstamo",
        description = "Restaura la actividad de un préstamo previamente inactivo."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        prestamoService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Préstamo activado correctamente"));
    }
}

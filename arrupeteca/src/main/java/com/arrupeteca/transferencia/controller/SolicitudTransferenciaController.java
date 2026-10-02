package com.arrupeteca.transferencia.controller;

import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.transferencia.dto.request.SolicitudTransferenciaRequest;
import com.arrupeteca.transferencia.dto.response.SolicitudTransferenciaDetalleResponse;
import com.arrupeteca.transferencia.dto.response.SolicitudTransferenciaResumenResponse;
import com.arrupeteca.transferencia.internal.service.SolicitudTransferenciaService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/solicitudes-transferencia")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Solicitudes de Transferencia", description = "Endpoints para la gestion de solicitudes de transferencias de libros entre salones")
public class SolicitudTransferenciaController {

    private final SolicitudTransferenciaService service;

    @GetMapping
    @Operation(
        summary = "Busqueda global de solicitudes de transferencia activas",
        description = "Retorna una pagina de solicitudes de transferencia activas permitiendo filtrar por usuario, salon destino, estado o motivo."
    )
    public ResponseEntity<ApiResponse<Page<SolicitudTransferenciaResumenResponse>>> busquedaGlobal(
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) Long idSalonDestino,
            @RequestParam(required = false) Long idEstadoSolicitud,
            @RequestParam(required = false) String motivo,
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Solicitudes de transferencia activas",
                service.busquedaGlobal(idUsuario, idSalonDestino, idEstadoSolicitud, motivo, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle publico de solicitud de transferencia activa por ID",
        description = "Retorna la informacion detallada de una solicitud activa incluyendo los libros y cantidades solicitadas."
    )
    public ResponseEntity<ApiResponse<SolicitudTransferenciaDetalleResponse>> busquedaGlobalPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Solicitud de transferencia encontrada", service.busquedaGlobalPorId(id)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Busqueda de administracion de solicitudes de transferencia",
        description = "Retorna una pagina de solicitudes permitiendo filtrar por estado de borrado logico y multiples criterios."
    )
    public ResponseEntity<ApiResponse<Page<SolicitudTransferenciaResumenResponse>>> busquedaAdmin(
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) Long idSalonDestino,
            @RequestParam(required = false) Long idEstadoSolicitud,
            @RequestParam(required = false) String motivo,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de solicitudes de transferencia (admin)",
                service.busquedaAdmin(idUsuario, idSalonDestino, idEstadoSolicitud, motivo, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener detalle administrativo de solicitud de transferencia por ID",
        description = "Retorna la informacion detallada de una solicitud por su ID independientemente de su estado logico."
    )
    public ResponseEntity<ApiResponse<SolicitudTransferenciaDetalleResponse>> busquedaAdminPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Solicitud de transferencia encontrada (admin)", service.busquedaAdminPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nueva solicitud de transferencia",
        description = "Crea una nueva solicitud de transferencia con sus respectivos items de libros."
    )
    public ResponseEntity<ApiResponse<SolicitudTransferenciaDetalleResponse>> crear(@Valid @RequestBody SolicitudTransferenciaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Solicitud de transferencia creada exitosamente", service.crear(request)));
    }

    @PatchMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar solicitud de transferencia (Actualizacion parcial)",
        description = "Actualiza de forma parcial los campos o detalles de una solicitud de transferencia existente."
    )
    public ResponseEntity<ApiResponse<SolicitudTransferenciaDetalleResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody SolicitudTransferenciaRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Solicitud de transferencia actualizada correctamente", service.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar solicitud de transferencia (Borrado logico)",
        description = "Desactiva la solicitud de transferencia cambiando su estado borradoLogico a true."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        service.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Solicitud de transferencia desactivada correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar solicitud de transferencia",
        description = "Restaura la visibilidad de una solicitud de transferencia previamente desactivada."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        service.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Solicitud de transferencia activada correctamente"));
    }
}

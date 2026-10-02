package com.arrupeteca.transferencia.controller;

import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.transferencia.dto.request.TransferenciaRequest;
import com.arrupeteca.transferencia.dto.response.TransferenciaDetalleResponse;
import com.arrupeteca.transferencia.dto.response.TransferenciaResumenResponse;
import com.arrupeteca.transferencia.internal.service.TransferenciaService;
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
@RequestMapping("/api/v1/transferencias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Transferencias de Ejemplares", description = "Endpoints para la ejecucion fisica y traslados de ejemplares entre muebles")
public class TransferenciaController {

    private final TransferenciaService service;

    @GetMapping
    @Operation(
        summary = "Busqueda global de transferencias activas",
        description = "Retorna una pagina de transferencias activas permitiendo filtrar por usuario emisor, usuario receptor, mueble origen, mueble destino, estado o confirmada."
    )
    public ResponseEntity<ApiResponse<Page<TransferenciaResumenResponse>>> busquedaGlobal(
            @RequestParam(required = false) Long idUsuarioEnvia,
            @RequestParam(required = false) Long idUsuarioRecibe,
            @RequestParam(required = false) Long idMuebleOrigen,
            @RequestParam(required = false) Long idMuebleDestino,
            @RequestParam(required = false) Long idEstadoTransferencia,
            @RequestParam(required = false) Boolean confirmada,
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Transferencias activas",
                service.busquedaGlobal(idUsuarioEnvia, idUsuarioRecibe, idMuebleOrigen, idMuebleDestino, idEstadoTransferencia, confirmada, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle publico de transferencia activa por ID",
        description = "Retorna la informacion detallada de una transferencia activa incluyendo los ejemplares fisicos despachados."
    )
    public ResponseEntity<ApiResponse<TransferenciaDetalleResponse>> busquedaGlobalPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Transferencia encontrada", service.busquedaGlobalPorId(id)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Busqueda de administracion de transferencias",
        description = "Retorna una pagina de transferencias permitiendo filtrar por estado de borrado logico y multiples criterios."
    )
    public ResponseEntity<ApiResponse<Page<TransferenciaResumenResponse>>> busquedaAdmin(
            @RequestParam(required = false) Long idUsuarioEnvia,
            @RequestParam(required = false) Long idUsuarioRecibe,
            @RequestParam(required = false) Long idMuebleOrigen,
            @RequestParam(required = false) Long idMuebleDestino,
            @RequestParam(required = false) Long idEstadoTransferencia,
            @RequestParam(required = false) Boolean confirmada,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de transferencias (admin)",
                service.busquedaAdmin(idUsuarioEnvia, idUsuarioRecibe, idMuebleOrigen, idMuebleDestino, idEstadoTransferencia, confirmada, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener detalle administrativo de transferencia por ID",
        description = "Retorna la informacion detallada de una transferencia por su ID independientemente de su estado logico."
    )
    public ResponseEntity<ApiResponse<TransferenciaDetalleResponse>> busquedaAdminPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Transferencia encontrada (admin)", service.busquedaAdminPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nueva transferencia fisica de ejemplares",
        description = "Crea una nueva transferencia y despacha la lista de ejemplares fisicos de un mueble origen a un mueble destino."
    )
    public ResponseEntity<ApiResponse<TransferenciaDetalleResponse>> crear(@Valid @RequestBody TransferenciaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Transferencia creada exitosamente", service.crear(request)));
    }

    @PatchMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar transferencia (Actualizacion parcial)",
        description = "Actualiza de forma parcial los campos o ejemplares de una transferencia existente."
    )
    public ResponseEntity<ApiResponse<TransferenciaDetalleResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody TransferenciaRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Transferencia actualizada correctamente", service.actualizar(id, request)));
    }

    @PatchMapping("/admin/{id}/confirmar-recepcion")
    @Operation(
        summary = "Confirmar recepcion y firma de transferencia en destino",
        description = "Firma y confirma la llegada de la transferencia a destino, reubicando automaticamente la posicion del inventario de cada ejemplar en la base de datos."
    )
    public ResponseEntity<ApiResponse<TransferenciaDetalleResponse>> confirmarRecepcion(
            @PathVariable Long id,
            @RequestParam Long idUsuarioRecibe,
            @RequestParam(required = false) String observacionRecepcion) {
        return ResponseEntity.ok(ApiResponse.exito("Recepcion de transferencia confirmada exitosamente",
                service.confirmarRecepcion(id, idUsuarioRecibe, observacionRecepcion)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar transferencia (Borrado logico)",
        description = "Desactiva la transferencia cambiando su estado borradoLogico a true."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        service.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Transferencia desactivada correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar transferencia",
        description = "Restaura la visibilidad de una transferencia previamente desactivada."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        service.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Transferencia activada correctamente"));
    }
}

package com.arrupeteca.controller;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.detail.MuebleDetalleResponse;
import com.arrupeteca.dto.response.summary.MuebleResumen;
import com.arrupeteca.service.MuebleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

@RestController
@RequestMapping("/api/v1/muebles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MuebleController {

    private final MuebleService muebleService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MuebleResumen>>> obtenerTodosActivos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de muebles activos", muebleService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MuebleDetalleResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Mueble encontrado", muebleService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/global")
    public ResponseEntity<ApiResponse<Page<MuebleResumen>>> busquedaGlobal(
            @RequestParam String palabra, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda global", muebleService.busquedaGlobal(palabra, pageable)));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<MuebleResumen>>> obtenerTodos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de muebles", muebleService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<MuebleDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Mueble encontrado (admin)", muebleService.obtenerPorId(id)));
    }

    @GetMapping("/admin/busqueda-maestra")
    public ResponseEntity<ApiResponse<Page<MuebleResumen>>> busquedaMaestraAdmin(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) Long idSalon,
            @RequestParam(required = false) String termino,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra",
                muebleService.busquedaMaestraAdmin(esBorrado, idSalon, termino, pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MuebleDetalleResponse>> crear(@Valid @RequestBody MuebleRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Mueble creado exitosamente", muebleService.crear(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MuebleDetalleResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody MuebleRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Mueble actualizado correctamente", muebleService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        muebleService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Mueble desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        muebleService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Mueble activado correctamente"));
    }
}

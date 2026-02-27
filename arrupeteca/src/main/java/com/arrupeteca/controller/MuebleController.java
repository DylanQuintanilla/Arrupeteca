package com.arrupeteca.controller;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.MuebleResumen;
import com.arrupeteca.service.MuebleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/muebles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MuebleController {

    private final MuebleService muebleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MuebleResumen>>> obtenerTodosActivos() {
        List<MuebleResumen> lista = muebleService.obtenerTodosActivos();
        return ResponseEntity.ok(ApiResponse.exito("Lista de muebles activos", lista));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MuebleResumen>> obtenerPorId(@PathVariable Long id) {
        MuebleResumen mueble = muebleService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Mueble encontrado", mueble));
    }

    @GetMapping("/por-salon/{idSalon}")
    public ResponseEntity<ApiResponse<List<MuebleResumen>>> obtenerPorSalon(@PathVariable Long idSalon) {
        List<MuebleResumen> lista = muebleService.obtenerPorIdSalon(idSalon);
        return ResponseEntity.ok(ApiResponse.exito("Muebles del salón seleccionado", lista));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MuebleResumen>> crear(@Valid @RequestBody MuebleRequest request) {
        MuebleResumen nuevo = muebleService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Mueble creado exitosamente", nuevo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MuebleResumen>> actualizar(@PathVariable Long id,
                                                                 @Valid @RequestBody MuebleRequest request) {
        MuebleResumen actualizado = muebleService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Mueble actualizado correctamente", actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        muebleService.cambiarBorradoLogico(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Mueble desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        muebleService.cambiarBorradoLogico(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Mueble activado correctamente"));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<MuebleResumen>>> obtenerTodosAdmin() {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de muebles", muebleService.obtenerTodos()));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<MuebleResumen>> obtenerPorIdAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Detalle de mueble (Admin)", muebleService.obtenerPorId(id)));
    }
}
package com.arrupeteca.controller;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.SalonResumen;
import com.arrupeteca.service.SalonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/salones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SalonController {

    private final SalonService salonService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SalonResumen>>> obtenerTodosActivos() {
        List<SalonResumen> lista = salonService.obtenerTodosActivos();
        return ResponseEntity.ok(ApiResponse.exito("Lista de salones activos", lista));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SalonResumen>> obtenerPorId(@PathVariable Long id) {
        SalonResumen salon = salonService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Salón encontrado", salon));
    }

    @GetMapping("/por-planta/{idPlanta}")
    public ResponseEntity<ApiResponse<List<SalonResumen>>> obtenerPorPlanta(@PathVariable Long idPlanta) {
        List<SalonResumen> lista = salonService.obtenerPorIdPlanta(idPlanta);
        return ResponseEntity.ok(ApiResponse.exito("Salones de la planta seleccionada", lista));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SalonResumen>> crear(@Valid @RequestBody SalonRequest request) {
        SalonResumen nuevo = salonService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Salón creado exitosamente", nuevo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SalonResumen>> actualizar(@PathVariable Long id,
                                                                @Valid @RequestBody SalonRequest request) {
        SalonResumen actualizado = salonService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Salón actualizado correctamente", actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        salonService.cambiarBorradoLogico(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Salón desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        salonService.cambiarBorradoLogico(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Salón activado correctamente"));
    }


    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<SalonResumen>>> obtenerTodosAdmin() {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de salones", salonService.obtenerTodos()));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<SalonResumen>> obtenerPorIdAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Detalle de salón (Admin)", salonService.obtenerPorId(id)));
    }
}
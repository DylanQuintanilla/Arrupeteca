package com.arrupeteca.controller;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.detail.SalonDetalleResponse;
import com.arrupeteca.dto.response.summary.SalonResumen;
import com.arrupeteca.service.SalonService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/salones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SalonController {

    private final SalonService salonService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SalonResumen>>> obtenerTodosActivos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de salones activos", salonService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SalonDetalleResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Salón encontrado", salonService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<SalonResumen>>> obtenerTodos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de salones", salonService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<SalonDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Salón encontrado (admin)", salonService.obtenerPorId(id)));
    }

    @GetMapping("/planta/{idPlanta}")
    public ResponseEntity<ApiResponse<Page<SalonResumen>>> buscarPorPlantaId(@PathVariable Long idPlanta, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Salones de la planta", salonService.buscarPorPlantaId(idPlanta, pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SalonDetalleResponse>> crear(@Valid @RequestBody SalonRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Salón creado exitosamente", salonService.crear(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SalonDetalleResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody SalonRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Salón actualizado correctamente", salonService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        salonService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Salón desactivado correctamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        salonService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Salón activado correctamente"));
    }
}

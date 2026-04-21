package com.arrupeteca.controller;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.SalonResumen;
import com.arrupeteca.service.SalonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/salones")
public class SalonController {

    private final SalonService salonService;

    // GET: Obtener todos los activos
    @GetMapping
    public ResponseEntity<ApiResponse<Page<SalonResumen>>> obtenerTodosActivos(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<SalonResumen> pagina = salonService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de salones activos", pagina));
    }

    // GET: Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SalonResumen>> obtenerPorId(@PathVariable Long id) {
        SalonResumen salon = salonService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Salón encontrado", salon));
    }

    // GET: Obtener por planta
    @GetMapping("/planta/{idPlanta}")
    public ResponseEntity<ApiResponse<Page<SalonResumen>>> obtenerPorPlanta(
            @PathVariable Long idPlanta,
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<SalonResumen> pagina = salonService.obtenerPorIdPlanta(idPlanta, pageable);
        return ResponseEntity.ok(ApiResponse.exito("Salones de la planta", pagina));
    }

    // POST: Crear nuevo
    @PostMapping
    public ResponseEntity<ApiResponse<SalonResumen>> crear(@Valid @RequestBody SalonRequest request) {
        SalonResumen nuevo = salonService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Salón creado exitosamente", nuevo));
    }

    // PUT: Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SalonResumen>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SalonRequest request) {
        SalonResumen actualizado = salonService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Salón actualizado correctamente", actualizado));
    }

    // DELETE: Desactivar
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        salonService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Salón desactivado correctamente"));
    }

    // PATCH: activar
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        salonService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Salón activado correctamente"));
    }

    // ADMIN

    // GET: Ver TODOS (incluso borrados)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<SalonResumen>>> obtenerTodosAdmin(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de salones", salonService.obtenerTodos(pageable)));
    }
}

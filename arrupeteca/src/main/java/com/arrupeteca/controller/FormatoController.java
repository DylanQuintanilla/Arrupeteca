package com.arrupeteca.controller;

import com.arrupeteca.dto.request.FormatoRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.FormatoResumen;
import com.arrupeteca.service.FormatoService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/formatos")
public class FormatoController {

    private final FormatoService formatoService;

    // GET: Obtener todos los activos
    @GetMapping
    public ResponseEntity<ApiResponse<Page<FormatoResumen>>> obtenerTodosActivos(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<FormatoResumen> pagina = formatoService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de formatos activos", pagina));
    }

    // GET: Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FormatoResumen>> obtenerPorId(@PathVariable Long id) {
        FormatoResumen formato = formatoService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Formato encontrado", formato));
    }

    // POST: Crear nuevo
    @PostMapping
    public ResponseEntity<ApiResponse<FormatoResumen>> crear(@Valid @RequestBody FormatoRequest request) {
        FormatoResumen nuevo = formatoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Formato creado exitosamente", nuevo));
    }

    // PUT: Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FormatoResumen>> actualizar(@PathVariable Long id,
                                                                  @Valid @RequestBody FormatoRequest request) {
        FormatoResumen actualizado = formatoService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Formato actualizado correctamente", actualizado));
    }

    // DELETE: Desactivar
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        formatoService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Formato desactivado correctamente"));
    }

    // PATCH: activar
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        formatoService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Formato activado correctamente"));
    }

    // ADMIN

    // GET: Ver TODOS (incluso borrados)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<FormatoResumen>>> obtenerTodosAdmin(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de formatos", formatoService.obtenerTodos(pageable)));
    }
}

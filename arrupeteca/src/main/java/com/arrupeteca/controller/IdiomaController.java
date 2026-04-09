package com.arrupeteca.controller;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.IdiomaResumen;
import com.arrupeteca.service.IdiomaService;
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
@RequestMapping("/api/v1/idiomas")
public class IdiomaController {

    private final IdiomaService idiomaService;

    // GET: Obtener todos los activos
    @GetMapping
    public ResponseEntity<ApiResponse<Page<IdiomaResumen>>> obtenerTodosActivos(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<IdiomaResumen> pagina = idiomaService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de idiomas activos", pagina));
    }

    // GET: Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IdiomaResumen>> obtenerPorId(@PathVariable Long id) {
        IdiomaResumen idioma = idiomaService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Idioma encontrado", idioma));
    }

    // POST: Crear nuevo
    @PostMapping
    public ResponseEntity<ApiResponse<IdiomaResumen>> crear(@Valid @RequestBody IdiomaRequest request) {
        IdiomaResumen nuevo = idiomaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Idioma creado exitosamente", nuevo));
    }

    // PUT: Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IdiomaResumen>> actualizar(@PathVariable Long id,
                                                                 @Valid @RequestBody IdiomaRequest request) {
        IdiomaResumen actualizado = idiomaService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Idioma actualizado correctamente", actualizado));
    }

    // DELETE: Desactivar
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        idiomaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Idioma desactivado correctamente"));
    }

    // PATCH: activar
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        idiomaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Idioma activado correctamente"));
    }

    // ADMIN

    // GET: Ver TODOS (incluso borrados)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<IdiomaResumen>>> obtenerTodosAdmin(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de idiomas", idiomaService.obtenerTodos(pageable)));
    }
}

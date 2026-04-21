package com.arrupeteca.controller;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.EditorialResumen;
import com.arrupeteca.service.EditorialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/editoriales")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EditorialController {

    private final EditorialService editorialService;

    // GET: Obtener todos los activos
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EditorialResumen>>> obtenerTodosActivos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion
    ) {
        Page<EditorialResumen> lista = editorialService.obtenerTodosActivos(page, size, ordenarPor, direccion);
        return ResponseEntity.ok(ApiResponse.exito("Lista de editoriales activos", lista));
    }

    // GET: Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EditorialResumen>> obtenerPorId(@PathVariable Long id) {
        EditorialResumen Editorial = editorialService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Editorial encontrada", Editorial));
    }

    // POST: Crear nuevo
    @PostMapping
    public ResponseEntity<ApiResponse<EditorialResumen>> crear(@Valid @RequestBody EditorialRequest request) {
        EditorialResumen nuevo = editorialService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Editorial creada exitosamente", nuevo));
    }

    // PUT: Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EditorialResumen>> actualizar(@PathVariable Long id,
                                                                       @Valid @RequestBody EditorialRequest request) {
        EditorialResumen actualizado = editorialService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Editorial actualizada correctamente", actualizado));
    }

    // DELETE: Desactivar
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        editorialService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Editorial desactivada correctamente"));
    }

    // PATCH: activar
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        editorialService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Editorial activada correctamente"));
    }

    // ADMIN

    // GET: Ver TODOS (incluso borrados)
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<EditorialResumen>>> obtenerTodosAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion
    ) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de editoriales", editorialService.obtenerTodos(page, size, ordenarPor, direccion)));
    }

    @GetMapping("/admin/busqueda-maestra")
    public ResponseEntity<ApiResponse<Page<EditorialResumen>>> busquedaMaestra(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) String termino,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion
    ) {
        Page<EditorialResumen> lista = editorialService.busquedaMaestraAdmin(esBorrado, termino, page, size, ordenarPor, direccion);
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra", lista));
    }
}
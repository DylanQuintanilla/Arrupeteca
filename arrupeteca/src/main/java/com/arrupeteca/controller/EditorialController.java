package com.arrupeteca.controller;

import com.arrupeteca.dto.request.EditorialRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.EditorialResumen;
import com.arrupeteca.service.EditorialService;
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
@RequestMapping("/api/v1/editoriales")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EditorialController {

    private final EditorialService editorialService;

    // GET: Obtener todos los activos
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EditorialResumen>>> obtenerTodosActivos(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable
    ) {
        Page<EditorialResumen> pagina = editorialService.obtenerTodosActivos(pageable);
        return ResponseEntity.ok(ApiResponse.exito("Lista de editoriales activas", pagina));
    }

    // GET: Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EditorialResumen>> obtenerPorId(@PathVariable Long id) {
        EditorialResumen editorial = editorialService.obtenerPorIdActivo(id);
        return ResponseEntity.ok(ApiResponse.exito("Editorial encontrada", editorial));
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
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable
    ) {
        return ResponseEntity.ok(
                ApiResponse.exito("Historial completo de editoriales", editorialService.obtenerTodos(pageable))
        );
    }

    @GetMapping("/admin/busqueda-maestra")
    public ResponseEntity<ApiResponse<Page<EditorialResumen>>> busquedaMaestra(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) String termino,
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable
    ) {
        Page<EditorialResumen> pagina = editorialService.busquedaMaestraAdmin(esBorrado, termino, pageable);
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra", pagina));
    }
}

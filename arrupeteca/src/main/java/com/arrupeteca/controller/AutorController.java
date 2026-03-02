package com.arrupeteca.controller;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.projection.AutorResumen;
import com.arrupeteca.service.AutorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/autores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AutorController {

    private final AutorService autorService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AutorResumen>>> obtenerTodosActivos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion
    ) {
        Page<AutorResumen> pagina = autorService.obtenerTodosActivos(page, size, ordenarPor, direccion);
        return ResponseEntity.ok(ApiResponse.exito("Lista de autores activos", pagina));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AutorResumen>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Autor encontrado", autorService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/busqueda")
    public ResponseEntity<ApiResponse<Page<AutorResumen>>> busquedaGlobal(
            @RequestParam(required = false) String palabra,
            @RequestParam(required = false) Integer inicio,
            @RequestParam(required = false) Integer fin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion) {

        Page<AutorResumen> pagina = autorService.busquedaGlobalAvanzada(palabra, inicio, fin, page, size, ordenarPor, direccion);
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda", pagina));
    }

    @GetMapping("/vivos-rango")
    public ResponseEntity<ApiResponse<Page<AutorResumen>>> autoresVivos(
            @RequestParam Integer inicio,
            @RequestParam Integer fin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion) {

        Page<AutorResumen> pagina = autorService.buscarAutoresVivosEnRango(inicio, fin, page, size, ordenarPor, direccion);
        return ResponseEntity.ok(ApiResponse.exito("Autores vivos en el rango", pagina));
    }


    @PostMapping
    public ResponseEntity<ApiResponse<AutorResumen>> crear(@Valid @RequestBody AutorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Autor creado", autorService.crear(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AutorResumen>> actualizar(@PathVariable Long id, @Valid @RequestBody AutorRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Autor actualizado", autorService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        autorService.cambiarEstadoLogico(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Autor desactivado"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        autorService.cambiarEstadoLogico(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Autor activado"));
    }

    // --- ADMIN ---

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<AutorResumen>>> obtenerTodosAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion
    ) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo", autorService.obtenerTodos(page, size, ordenarPor, direccion)));
    }

    @GetMapping("/admin/busqueda-maestra")
    public ResponseEntity<ApiResponse<Page<AutorResumen>>> busquedaMaestra(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) Long idNacionalidad,
            @RequestParam(required = false) Long idPais,
            @RequestParam(required = false) Long idGenero,
            @RequestParam(required = false) Long idObra,
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) Integer anioInicio,
            @RequestParam(required = false) Integer anioFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direccion) {

        Page<AutorResumen> pagina = autorService.busquedaMaestraAdmin(
                esBorrado, idNacionalidad, idPais, idGenero, idObra, termino, anioInicio, anioFin, page, size, ordenarPor, direccion);

        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra", pagina));
    }
}
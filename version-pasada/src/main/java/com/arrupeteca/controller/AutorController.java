package com.arrupeteca.controller;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.detail.AutorDetalleResponse;
import com.arrupeteca.dto.response.summary.AutorResumen;
import com.arrupeteca.service.AutorService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/autores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AutorController {

    private final AutorService autorService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AutorResumen>>> obtenerTodosActivos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de autores activos", autorService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Autor encontrado", autorService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Page<AutorResumen>>> obtenerTodos(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo", autorService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Autor encontrado (admin)", autorService.obtenerPorId(id)));
    }

    @GetMapping("/busqueda")
    public ResponseEntity<ApiResponse<Page<AutorResumen>>> busquedaGlobal(
            @RequestParam String palabra,
            @RequestParam(required = false) Integer inicio,
            @RequestParam(required = false) Integer fin,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda", autorService.busquedaGlobal(palabra, inicio, fin, pageable)));
    }

    @GetMapping("/admin/busqueda-maestra")
    public ResponseEntity<ApiResponse<Page<AutorResumen>>> busquedaMaestraAdmin(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) Long idNacionalidad,
            @RequestParam(required = false) Long idPais,
            @RequestParam(required = false) Long idGenero,
            @RequestParam(required = false) Long idObra,
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) Integer anioInicio,
            @RequestParam(required = false) Integer anioFin,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra",
                autorService.busquedaMaestraAdmin(esBorrado, idNacionalidad, idPais, idGenero, idObra,
                        termino, anioInicio, anioFin, pageable)));
    }

    @GetMapping("/vivos-rango")
    public ResponseEntity<ApiResponse<Page<AutorResumen>>> buscarVivos(
            @RequestParam Integer inicio,
            @RequestParam Integer fin,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Autores vivos en el rango", autorService.buscarVivosEnRango(inicio, fin, pageable)));
    }

    @GetMapping("/verificar-duplicados")
    public ResponseEntity<ApiResponse<List<AutorResumen>>> buscarDuplicados(
            @RequestParam String termino,
            @RequestParam(required = false) Long idExcluir) {
        return ResponseEntity.ok(ApiResponse.exito("Verificación completada", autorService.buscarDuplicados(termino, idExcluir)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> crear(@Valid @RequestBody AutorRequest request) {
        return new ResponseEntity<>(ApiResponse.exito("Autor creado exitosamente", autorService.crear(request)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody AutorRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Autor actualizado correctamente", autorService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        autorService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Autor desactivado"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        autorService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Autor activado"));
    }
}

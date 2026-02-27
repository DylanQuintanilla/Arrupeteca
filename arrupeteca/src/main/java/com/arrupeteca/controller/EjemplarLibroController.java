package com.arrupeteca.controller;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.enums.EstadoFisico;
import com.arrupeteca.persistence.projection.EjemplarLibroResumen;
import com.arrupeteca.service.EjemplarLibroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ejemplares")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EjemplarLibroController {

    private final EjemplarLibroService ejemplarLibroService;

    // ==========================================
    // 🟢 ENDPOINTS PÚBLICOS
    // ==========================================

    @GetMapping
    public ResponseEntity<ApiResponse<List<EjemplarLibroResumen>>> obtenerTodosActivos() {
        return ResponseEntity.ok(ApiResponse.exito("Lista de ejemplares activos", ejemplarLibroService.obtenerTodosActivos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EjemplarLibroResumen>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Ejemplar encontrado", ejemplarLibroService.obtenerPorIdActivo(id)));
    }

    // 🚀 ENDPOINT EXTRA: Saber cuántos hay disponibles
    // Ejemplo: /api/v1/ejemplares/conteo?idLibro=5&disponibilidad=DISPONIBLE
    @GetMapping("/conteo")
    public ResponseEntity<ApiResponse<Long>> contarEjemplares(
            @RequestParam Long idLibro,
            @RequestParam(defaultValue = "DISPONIBLE") Disponibilidad disponibilidad) {

        Long cantidad = ejemplarLibroService.cantidadEjemplaresActivosPorIdLibroYDisponibilidad(idLibro, disponibilidad);
        return ResponseEntity.ok(ApiResponse.exito("Cantidad encontrada", cantidad));
    }

    // 🔍 BÚSQUEDA TIPO GOOGLE
    @GetMapping("/busqueda")
    public ResponseEntity<ApiResponse<List<EjemplarLibroResumen>>> busquedaGlobal(
            @RequestParam(required = false) String palabra,
            @RequestParam(required = false, defaultValue = "libro.obra.titulo") String ordenarPor,
            @RequestParam(required = false, defaultValue = "ASC") String direccion) {

        List<EjemplarLibroResumen> resultados = ejemplarLibroService.busquedaGlobalAvanzada(palabra, ordenarPor, direccion);
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda", resultados));
    }

    // ==========================================
    // 📝 GESTIÓN
    // ==========================================

    @PostMapping
    public ResponseEntity<ApiResponse<EjemplarLibroResumen>> crear(@Valid @RequestBody EjemplarLibroRequest request) {
        EjemplarLibroResumen nuevo = ejemplarLibroService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Ejemplar registrado exitosamente", nuevo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EjemplarLibroResumen>> actualizar(@PathVariable Long id,
                                                                        @Valid @RequestBody EjemplarLibroRequest request) {
        EjemplarLibroResumen actualizado = ejemplarLibroService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.exito("Ejemplar actualizado correctamente", actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        ejemplarLibroService.cambiarBorradoLogico(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Ejemplar dado de baja (Lógico)"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        ejemplarLibroService.cambiarBorradoLogico(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Ejemplar reactivado"));
    }

    // ==========================================
    // 🕵️ ZONA ADMIN (Búsqueda Maestra)
    // ==========================================

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<EjemplarLibroResumen>>> obtenerTodosAdmin() {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de ejemplares", ejemplarLibroService.obtenerTodos()));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<EjemplarLibroResumen>> obtenerPorIdAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Detalle de ejemplar (Admin)", ejemplarLibroService.obtenerPorId(id)));
    }

    // 📊 BÚSQUEDA MAESTRA MULTI-FILTRO
    @GetMapping("/admin/busqueda-maestra")
    public ResponseEntity<ApiResponse<List<EjemplarLibroResumen>>> busquedaMaestra(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) Long idLibro,
            @RequestParam(required = false) Long idMueble,
            @RequestParam(required = false) EstadoFisico estadoFisico,
            @RequestParam(required = false) Disponibilidad disponibilidad,
            @RequestParam(required = false) String termino,
            @RequestParam(required = false, defaultValue = "libro.obra.titulo") String ordenarPor,
            @RequestParam(required = false, defaultValue = "ASC") String direccion) {

        List<EjemplarLibroResumen> resultados = ejemplarLibroService.busquedaMaestraAdmin(
                esBorrado, idLibro, idMueble, estadoFisico, disponibilidad, termino, ordenarPor, direccion);

        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda maestra", resultados));
    }
}
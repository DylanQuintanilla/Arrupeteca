package com.arrupeteca.controller;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.SalonResponse;
import com.arrupeteca.service.SalonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/salones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Salones", description = "Endpoints para la gestión de salones")
public class SalonController {

    private final SalonService salonService;

    @GetMapping
    @Operation(
        summary = "Obtener salones activos(as) para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de salones activos(as), con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<SalonResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de salones activos(as)", salonService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de salones",
        description = "Retorna una página de salones con soporte para filtros por idPlanta, nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<SalonResponse>>> obtenerTodos(
            @RequestParam(required = false) Long idPlanta,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de salones", salonService.obtenerTodos(idPlanta, nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener salones por ID",
        description = "Busca la información de un(a) salon por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<SalonResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Salon encontrado(a)", salonService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo(a) salon",
        description = "Crea un(a) nuevo(a) salon en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<SalonResponse>> crear(@Valid @RequestBody SalonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Salon creado(a) exitosamente", salonService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar salon",
        description = "Actualiza los campos de un(a) salon existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<SalonResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody SalonRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Salon actualizado(a) correctamente",
                salonService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar salon (Borrado lógico)",
        description = "Oculta el/la salon en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        salonService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Salon desactivado(a) correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar salon",
        description = "Restaura la visibilidad pública de un(a) salon previamente desactivado(a)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        salonService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Salon activado(a) correctamente"));
    }
}

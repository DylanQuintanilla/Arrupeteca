package com.arrupeteca.controller;

import com.arrupeteca.dto.request.CicloRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.CicloResponse;
import com.arrupeteca.service.CicloService;
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
@RequestMapping("/api/v1/ciclos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Ciclos", description = "Endpoints para la gestión de ciclos escolares")
public class CicloController {

    private final CicloService cicloService;

    @GetMapping
    @Operation(
        summary = "Obtener ciclos escolares activos(as) para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de ciclos escolares activos(as), con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<CicloResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de ciclos escolares activos(as)", cicloService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de ciclos escolares",
        description = "Retorna una página de ciclos escolares con soporte para filtros por nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<CicloResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de ciclos escolares", cicloService.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener ciclos escolares por ID",
        description = "Busca la información de un(a) ciclo por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<CicloResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Ciclo encontrado(a)", cicloService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo(a) ciclo",
        description = "Crea un(a) nuevo(a) ciclo en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<CicloResponse>> crear(@Valid @RequestBody CicloRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Ciclo creado(a) exitosamente", cicloService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar ciclo",
        description = "Actualiza los campos de un(a) ciclo existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<CicloResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody CicloRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Ciclo actualizado(a) correctamente",
                cicloService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar ciclo (Borrado lógico)",
        description = "Oculta el/la ciclo en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        cicloService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Ciclo desactivado(a) correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar ciclo",
        description = "Restaura la visibilidad pública de un(a) ciclo previamente desactivado(a)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        cicloService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Ciclo activado(a) correctamente"));
    }}

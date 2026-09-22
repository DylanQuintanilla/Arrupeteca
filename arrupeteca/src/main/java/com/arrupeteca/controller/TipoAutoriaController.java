package com.arrupeteca.controller;

import com.arrupeteca.dto.request.TipoAutoriaRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.TipoAutoriaResponse;
import com.arrupeteca.service.TipoAutoriaService;
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
@RequestMapping("/api/v1/tipos-autoria")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Tipos de Autoría", description = "Endpoints para la gestión de tipos de autoría")
public class TipoAutoriaController {

    private final TipoAutoriaService tipoAutoriaService;

    @GetMapping
    @Operation(
        summary = "Obtener tipos de autoría activos(as) para dropdowns y autocompletado",
        description = "Retorna una lista no paginada de tipos de autoría activos(as), con soporte opcional para filtrado por nombre."
    )
    public ResponseEntity<ApiResponse<List<TipoAutoriaResponse>>> obtenerTodosActivos(
            @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de tipos de autoría activos(as)", tipoAutoriaService.obtenerTodosActivos(nombre)));
    }

    @GetMapping("/admin/all")
    @Operation(
        summary = "Obtener tabla de administración de tipos de autoría",
        description = "Retorna una página de tipos de autoría con soporte para filtros por nombre y estado borradoLogico."
    )
    public ResponseEntity<ApiResponse<Page<TipoAutoriaResponse>>> obtenerTodos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean borradoLogico,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Tabla de tipos de autoría", tipoAutoriaService.obtenerTodos(nombre, borradoLogico, pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener tipos de autoría por ID",
        description = "Busca la información de un(a) tipoautoria por su ID sin importar su estado lógico."
    )
    public ResponseEntity<ApiResponse<TipoAutoriaResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("TipoAutoria encontrado(a)", tipoAutoriaService.obtenerPorId(id)));
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Registrar nuevo(a) tipoautoria",
        description = "Crea un(a) nuevo(a) tipoautoria en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<TipoAutoriaResponse>> crear(@Valid @RequestBody TipoAutoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("TipoAutoria creado(a) exitosamente", tipoAutoriaService.crear(request)));
    }

    @PutMapping("/admin/{id}")
    @Operation(
        summary = "Actualizar tipoautoria",
        description = "Actualiza los campos de un(a) tipoautoria existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<TipoAutoriaResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody TipoAutoriaRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("TipoAutoria actualizado(a) correctamente",
                tipoAutoriaService.actualizar(id, request)));
    }

    @DeleteMapping("/admin/{id}")
    @Operation(
        summary = "Desactivar tipoautoria (Borrado lógico)",
        description = "Oculta el/la tipoautoria en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        tipoAutoriaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("TipoAutoria desactivado(a) correctamente"));
    }

    @PatchMapping("/admin/{id}/activar")
    @Operation(
        summary = "Activar tipoautoria",
        description = "Restaura la visibilidad pública de un(a) tipoautoria previamente desactivado(a)."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        tipoAutoriaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("TipoAutoria activado(a) correctamente"));
    }}

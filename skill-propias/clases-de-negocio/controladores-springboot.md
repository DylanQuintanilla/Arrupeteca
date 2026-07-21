# Estándar de Desarrollo: Controladores Spring Boot (Arrupeteca)

Este documento establece las directrices, convenciones y mejores prácticas para el diseño e implementación de la capa de **Controladores (Controladores REST)** en Arrupeteca.

---

## 1. Configuración de Clase

Todo controlador REST de Arrupeteca debe seguir las siguientes reglas de anotación y configuración:

*   **`@RestController`**: Registra la clase como un controlador de tipo REST que retorna respuestas en formato JSON.
*   **`@RequestMapping("/api/v1/recurso")`**: Define el prefijo de la ruta del API. Debe seguir la convención `/api/v1/` seguido del nombre del recurso en plural (ej. `/api/v1/autores`, `/api/v1/libros`).
*   **`@RequiredArgsConstructor`**: Inyecta los servicios colaboradores de forma automática por constructor vía Lombok (los colaboradores deben declararse como `private final`).
*   **`@CrossOrigin(origins = "*")`**: Permite la comunicación CORS desde cualquier cliente de desarrollo (Frontend).
*   **`@Tag(name = "Recurso", description = "...")`**: Anotación de OpenAPI obligatoria para agrupar y describir los endpoints de este controlador en la UI de Swagger.

---

## 2. Semántica HTTP y Estructura de Endpoints

Los controladores deben implementar la arquitectura REST de forma estricta, mapeando las acciones a los verbos HTTP correctos:

| Método HTTP | Ruta relativa | Acción REST | Códigos de Estado Comunes | Formato de Retorno |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/` | Obtener listado activo. | `200 OK` | `ApiResponse<Page<[Entidad]ResumenResponse>>` |
| **GET** | `/{id}` | Obtener detalle activo. | `200 OK`, `400 Bad Request` | `ApiResponse<[Entidad]DetalleResponse>` |
| **GET** | `/admin` | Obtener historial completo (Admin). | `200 OK` | `ApiResponse<Page<[Entidad]ResumenResponse>>` |
| **GET** | `/admin/{id}` | Obtener detalle (Admin). | `200 OK`, `400 Bad Request` | `ApiResponse<[Entidad]DetalleResponse>` |
| **POST** | `/` | Crear nuevo registro. | `201 Created`, `400 Bad Request` | `ApiResponse<[Entidad]DetalleResponse>` |
| **PATCH** | `/{id}` | Actualizar registro (Parcial con JsonNullable). | `200 OK`, `400 Bad Request` | `ApiResponse<[Entidad]DetalleResponse>` |
| **DELETE** | `/{id}` | Desactivar (Borrado lógico). | `200 OK`, `400 Bad Request` | `ApiResponse<Void>` |
| **PATCH** | `/{id}/activar` | Activar registro desactivado. | `200 OK`, `400 Bad Request` | `ApiResponse<Void>` |

*Nota: Para el estado `201 Created` en el POST, utilizar la sintaxis fluida de Spring:*
```java
return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.exito("...", resultado));
```

---

## 3. Autodocumentación Limpia con Springdoc OpenAPI

Para mantener el código fuente limpio y libre de colisiones de nombres con DTOs locales (como nuestra clase `ApiResponse`):

*   **Uso de `@Operation`**: Cada endpoint debe estar documentado únicamente con `@Operation(summary = "...", description = "...")` para describir de forma clara la acción.
*   **Prohibido**: Utilizar anotaciones `@ApiResponse` personalizadas en cada método.
*   **Autodetección**: Springdoc OpenAPI lee de forma inteligente el tipo de retorno genérico (ej. `ResponseEntity<ApiResponse<DTO>>`) y genera automáticamente en el Swagger UI el esquema JSON y los códigos de éxito.
*   **Paginación con `@ParameterObject`**: Todo endpoint que reciba un parámetro `Pageable` debe anotarlo obligatoriamente con `@ParameterObject` y `@PageableDefault(size = 20, sort = "nombre")`. Esto permite que Springdoc exponga de forma plana los parámetros de consulta (`page`, `size`, `sort`) en la documentación de Swagger UI en lugar de un objeto anidado complejo, además de definir los valores por defecto del sistema.
*   **Checkstyle Compliance (140 Caracteres)**: Las anotaciones de OpenAPI deben formatearse en múltiples líneas independientes para evitar violar el límite máximo de caracteres por línea.

---

## 4. Plantilla Maestra de Referencia (AutorController)

```java
package com.arrupeteca.controller;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.detail.AutorDetalleResponse;
import com.arrupeteca.dto.response.summary.AutorResumenResponse;
import com.arrupeteca.service.AutorService;
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

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/v1/autores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Autores", description = "Endpoints para la gestión de autores literarios")
public class AutorController {

    private final AutorService autorService;

    @GetMapping
    @Operation(
        summary = "Obtener autores activos",
        description = "Retorna una página de autores registrados que no están borrados lógicamente."
    )
    public ResponseEntity<ApiResponse<Page<AutorResumenResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de autores activos", autorService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle de autor activo",
        description = "Busca la información detallada de un autor por su ID si está activo."
    )
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Autor encontrado", autorService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener historial de autores (Admin)",
        description = "Retorna todos los autores incluyendo eliminados lógicamente (solo admin)."
    )
    public ResponseEntity<ApiResponse<Page<AutorResumenResponse>>> obtenerTodos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo", autorService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener autor por ID (Admin)",
        description = "Busca el detalle de un autor por su ID sin importar su estado lógico (solo admin)."
    )
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Autor encontrado (admin)", autorService.obtenerPorId(id)));
    }

    @GetMapping("/busqueda")
    @Operation(
        summary = "Búsqueda global de autores",
        description = "Busca autores por coincidencia parcial de texto y rango de año de nacimiento."
    )
    public ResponseEntity<ApiResponse<Page<AutorResumenResponse>>> busquedaGlobal(
            @RequestParam(required = false) String palabra,
            @RequestParam(required = false) Year inicio,
            @RequestParam(required = false) Year fin,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados de búsqueda", autorService.busquedaGlobal(palabra, inicio, fin, pageable)));
    }

    @GetMapping("/admin/busqueda-maestra")
    @Operation(
        summary = "Búsqueda avanzada (Admin)",
        description = "Búsqueda avanzada de administración con filtros opcionales de estado, género, país, período y término."
    )
    public ResponseEntity<ApiResponse<Page<AutorResumenResponse>>> busquedaMaestraAdmin(
            @RequestParam(required = false) Boolean esBorrado,
            @RequestParam(required = false) Long idNacionalidad,
            @RequestParam(required = false) Long idPais,
            @RequestParam(required = false) Long idGenero,
            @RequestParam(required = false) Year anioInicio,
            @RequestParam(required = false) Year anioFin,
            @RequestParam(required = false) String termino,
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Resultados búsqueda maestra",
                autorService.busquedaMaestraAdmin(esBorrado, idNacionalidad, idPais, idGenero,
                        anioInicio, anioFin, termino, pageable)));
    }

    @PostMapping
    @Operation(
        summary = "Registrar nuevo autor",
        description = "Crea un nuevo autor en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> crear(@Valid @RequestBody AutorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Autor creado exitosamente", autorService.crear(request)));
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "Actualizar autor",
        description = "Actualiza los campos de un autor existente y valida su versión de bloqueo optimista."
    )
    public ResponseEntity<ApiResponse<AutorDetalleResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody AutorRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Autor actualizado correctamente", autorService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar autor (Borrado lógico)",
        description = "Oculta el autor en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        autorService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Autor desactivado"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar autor",
        description = "Restaura la visibilidad pública de un autor previamente desactivado."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        autorService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Autor activado"));
    }
}
```

---

## 5. Lista de Autoverificación (Checklist del Desarrollador)

1. **¿El mapeo de rutas es correcto?**
   * ¿El prefijo empieza con `/api/v1/` y sigue con el nombre del recurso en plural en minúsculas?
2. **¿Se inyectan las dependencias por constructor?**
   * ¿Están todos los servicios declarados como `private final` y la clase anotada con `@RequiredArgsConstructor`?
3. **¿Se validan las peticiones de entrada?**
   * ¿Tienen los parámetros `@RequestBody` anotado `@Valid` al lado para habilitar las validaciones del DTO?
4. **¿Usa la sintaxis fluida de ResponseEntity en la creación?**
   * En el método POST, ¿se retorna `ResponseEntity.status(HttpStatus.CREATED).body(...)`?
5. **¿Autodocumentación limpia y libre de colisiones?**
   * ¿Se incluyó `@Tag` a nivel de clase y `@Operation` en cada método?
   * ¿Se evitaron anotaciones `@ApiResponse` redundantes para dejar que Springdoc infiera el esquema automáticamente?
   * ¿Los parámetros de tipo `Pageable` tienen anotados `@ParameterObject` y `@PageableDefault`?
6. **¿Conformidad con Checkstyle?**
   * ¿Se formatearon las anotaciones OpenAPI en múltiples líneas para asegurar que ninguna línea de código supere los 140 caracteres?

---
*Este estándar garantiza controladores REST uniformes, limpios, seguros y perfectamente autodocumentados en Arrupeteca.*

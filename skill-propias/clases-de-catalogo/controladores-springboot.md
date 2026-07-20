# Estándar de Desarrollo: Controladores Spring Boot para Catálogos (Arrupeteca)

Este documento establece las directrices para el diseño e implementación de la capa de **Controladores REST** en los módulos de **Catálogo** (tablas maestras de referencia, ej. `Categoria`, `Idioma`, `Pais`, `Nacionalidad`, `Formato`, `Genero`).

---

## 1. Configuración del Controlador REST

Todo controlador de catálogo debe seguir las siguientes reglas de anotación y configuración:

*   **`@RestController`**: Registra la clase como un controlador de tipo REST.
*   **`@RequestMapping("/api/v1/[recurso]")`**: Ruta plural en minúsculas (ej. `/api/v1/categorias`).
*   **`@RequiredArgsConstructor`**: Inyección de colaboradores de manera automática por constructor (los atributos inyectados deben ser `private final`).
*   **`@CrossOrigin(origins = "*")`**: Habilitar CORS para desarrollo.
*   **`@Tag(name = "[Recurso]", description = "...")`**: Agrupación e identificación obligatoria para OpenAPI Swagger.

---

## 2. Semántica HTTP de Endpoints de Catálogo

Los endpoints deben respetar la semántica HTTP clásica de manera estricta y retornar la misma estructura unificada de salida `[Entidad]Response`:

| Método HTTP | Ruta relativa | Acción | Retorno exitoso |
| :--- | :--- | :--- | :--- |
| **GET** | `/` | Obtener listado activo. | `200 OK` $\rightarrow$ `ApiResponse<Page<[Entidad]Response>>` |
| **GET** | `/{id}` | Obtener detalle activo. | `200 OK` $\rightarrow$ `ApiResponse<[Entidad]Response>` |
| **GET** | `/admin` | Obtener historial completo (Admin). | `200 OK` $\rightarrow$ `ApiResponse<Page<[Entidad]Response>>` |
| **GET** | `/admin/{id}` | Obtener detalle (Admin). | `200 OK` $\rightarrow$ `ApiResponse<[Entidad]Response>` |
| **POST** | `/` | Crear nuevo registro. | `201 Created` $\rightarrow$ `ApiResponse<[Entidad]Response>` |
| **PUT** | `/{id}` | Actualizar completo (Full update). | `200 OK` $\rightarrow$ `ApiResponse<[Entidad]Response>` |
| **DELETE** | `/{id}` | Desactivar (Borrado lógico). | `200 OK` $\rightarrow$ `ApiResponse<Void>` |
| **PATCH** | `/{id}/activar` | Activar registro desactivado. | `200 OK` $\rightarrow$ `ApiResponse<Void>` |

*Para la respuesta exitosa del POST, utilizar la API fluida:*
```java
return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.exito("...", resultado));
```

---

## 3. Autodocumentación OpenAPI y Paginación

*   **Anotar con `@Operation(summary = "...", description = "...")`**: Obligatorio en todos los métodos para documentar la acción.
*   **Formatear anotaciones**: Mantener las anotaciones OpenAPI multilínea para cumplir estrictamente con el límite de 140 caracteres de Checkstyle.
*   **Documentación de paginación**: Todo parámetro `Pageable` debe ir decorado con `@ParameterObject` y `@PageableDefault(size = 20, sort = "nombre")` para aplanar sus parámetros y hacer el Swagger UI legible.

---

## 4. Plantilla Maestra de Referencia (CategoriaController)

```java
package com.arrupeteca.controller;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.dto.response.ApiResponse;
import com.arrupeteca.dto.response.CategoriaResponse;
import com.arrupeteca.service.CategoriaService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Categorías", description = "Endpoints para la gestión de categorías literarias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    @Operation(
        summary = "Obtener categorías activas",
        description = "Retorna una página de categorías registradas que no están borradas lógicamente."
    )
    public ResponseEntity<ApiResponse<Page<CategoriaResponse>>> obtenerTodosActivos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Lista de categorías activas", categoriaService.obtenerTodosActivos(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener detalle de categoría activa",
        description = "Busca la información de una categoría activa por su ID."
    )
    public ResponseEntity<ApiResponse<CategoriaResponse>> obtenerPorIdActivo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Categoría encontrada", categoriaService.obtenerPorIdActivo(id)));
    }

    @GetMapping("/admin")
    @Operation(
        summary = "Obtener todas las categorías (Admin)",
        description = "Retorna todas las categorías incluyendo las eliminadas lógicamente (solo admin)."
    )
    public ResponseEntity<ApiResponse<Page<CategoriaResponse>>> obtenerTodos(
            @ParameterObject @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.exito("Historial completo de categorías", categoriaService.obtenerTodos(pageable)));
    }

    @GetMapping("/admin/{id}")
    @Operation(
        summary = "Obtener categoría por ID (Admin)",
        description = "Busca el detalle de una categoría por su ID sin importar su estado lógico (solo admin)."
    )
    public ResponseEntity<ApiResponse<CategoriaResponse>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.exito("Categoría encontrada (admin)", categoriaService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(
        summary = "Registrar nueva categoría",
        description = "Crea una nueva categoría en el sistema validando la unicidad del nombre."
    )
    public ResponseEntity<ApiResponse<CategoriaResponse>> crear(@Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exito("Categoría creada exitosamente", categoriaService.crear(request)));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar categoría",
        description = "Actualiza los campos de una categoría existente mediante una edición completa."
    )
    public ResponseEntity<ApiResponse<CategoriaResponse>> actualizar(
            @PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(ApiResponse.exito("Categoría actualizada correctamente",
                categoriaService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Desactivar categoría (Borrado lógico)",
        description = "Oculta la categoría en el catálogo público cambiando su estado a borrado."
    )
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        categoriaService.cambiarEstado(id, false);
        return ResponseEntity.ok(ApiResponse.exito("Categoría desactivada correctamente"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(
        summary = "Activar categoría",
        description = "Restaura la visibilidad pública de una categoría previamente desactivada."
    )
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Long id) {
        categoriaService.cambiarEstado(id, true);
        return ResponseEntity.ok(ApiResponse.exito("Categoría activada correctamente"));
    }
}
```

---

## 5. Lista de Autoverificación (Checklist)

1. **¿El mapeo de rutas es correcto?**
   * ¿Comienza con `/api/v1/` y sigue el nombre del recurso en plural en minúsculas?
2. **¿Los métodos HTTP son correctos?**
   * ¿GET para consultas, POST para crear, PUT para actualizar, DELETE para desactivar y PATCH para activar?
3. **¿Se validan las entradas `@RequestBody`?**
   * ¿Tienen la anotación `@Valid` al lado del cuerpo?
4. **¿Sintaxis fluida para el código de creación (POST)?**
   * ¿Se utiliza `ResponseEntity.status(HttpStatus.CREATED).body(...)`?
5. **¿La salida de controladores está centralizada?**
   * ¿Todos los endpoints de lectura retornan `[Entidad]Response` en lugar de diferenciar entre resumen y detalle?
6. **¿Autodocumentación OpenAPI y Paginación configuradas?**
   * ¿Tiene `@Tag` y `@Operation`?
   * ¿Los parámetros Pageable tienen `@ParameterObject` y `@PageableDefault`?
7. **¿Conformidad con Checkstyle?**
   * ¿Las anotaciones OpenAPI largas se dividieron en varias líneas para respetar el límite de longitud de línea?

# Mejoras Sugeridas para Arrupeteca

Este documento detalla mejoras arquitectónicas y de rendimiento propuestas para el proyecto.

## 1. Búsqueda Tipo "Google" (Sugerencias Instantáneas / Autocomplete)

El objetivo es proporcionar sugerencias al usuario mientras escribe, con latencia mínima y alto rendimiento.

### A. Definición de Proyección (Capa de Repositorio)
Creamos una proyección cerrada para que Spring Data JPA solo traiga los campos necesarios, optimizando el uso de memoria y la velocidad del SQL.

```java
// Ubicación sugerida: com.arrupeteca.persistence.repository.projections
public interface SugerenciaBusqueda {
    Long getId();
    String getNombre();
}
```

### B. Método en el Repositorio
Se utiliza un `SELECT` limitado y filtrado por coincidencia parcial. Es vital usar `Pageable` para limitar los resultados (ej. top 10).

```java
@Query("SELECT a.id as id, a.nombre as nombre FROM Autor a " +
       "WHERE LOWER(a.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) " +
       "AND a.borradoLogico = false")
List<SugerenciaBusqueda> buscarSugerencias(@Param("termino") String termino, Pageable pageable);
```

### C. Endpoint en el Controlador
Un endpoint dedicado e independiente de la búsqueda global detallada.

```java
@GetMapping("/sugerencias")
public ResponseEntity<List<SugerenciaBusqueda>> obtenerSugerencias(@RequestParam String q) {
    // Limitamos a 10 resultados para garantizar velocidad de respuesta
    return ResponseEntity.ok(autorService.obtenerSugerencias(q, PageRequest.of(0, 10)));
}
```

### D. Claves de Rendimiento
1. **Índices en BD:** Asegurarse de que las columnas de búsqueda (ej. `nombre`, `titulo`) tengan índices.
2. **Debounce (Frontend):** Configurar el frontend para esperar ~300ms antes de disparar la petición.
3. **Evitar @Value:** Mantener la proyección "cerrada" (sin SpEL) para que el SQL sea optimizado automáticamente por Spring Data.

---
*Documento generado por Gemini CLI - Abril 2026*

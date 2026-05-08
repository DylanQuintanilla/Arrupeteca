# Estándar de Desarrollo: Servicios Spring Boot (Arrupeteca)

Este documento define la arquitectura y las mejores prácticas para la capa de Servicio, el núcleo de la lógica de negocio del sistema.

## 1. Configuración de Clase
Para garantizar seguridad y observabilidad:

*   **`@Service`**: Registro obligatorio en el contexto de Spring.
*   **`@RequiredArgsConstructor`**: Inyección de dependencias por constructor vía Lombok.
*   **`@Transactional(readOnly = true)`**: Configuración a nivel de clase para asegurar que todas las consultas sean de solo lectura por defecto.
*   **`@Slf4j`**: Implementación de logging para trazabilidad.

## 2. Gestión de Transacciones
*   Los métodos que **modifican** datos (`crear`, `actualizar`, `eliminar`, `cambiarEstado`) deben usar la anotación `@Transactional` para sobrescribir el comportamiento de solo lectura.

## 3. Estrategia de Caché
Para mejorar el rendimiento, se debe aplicar caché en los endpoints de lectura individual (Detalle):

*   **`@Cacheable(value = "entidad", key = "#id")`**: Usar en métodos de consulta por ID.
*   **`@CacheEvict(value = "entidad", key = "#id")`**: Usar en métodos que modifiquen el registro (`actualizar`, `cambiarEstado`) para invalidar el caché obsoleto.

## 4. Validaciones y Relaciones
*   **Fail-Fast**: Validar la existencia de registros antes de operar.
*   **Recolección de Errores**: Al validar múltiples relaciones, usar una `List<String> errores` para recolectar todos los fallos y lanzarlos juntos en una `ValidacionNegocioException(errores)`. Esto mejora enormemente la experiencia del usuario (frontend).

## 5. Ejemplo de Plantilla Maestra

```java
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EjemploServiceImpl implements EjemploService {

    private final EjemploRepository ejemploRepository;
    private final RelacionRepository relacionRepository;
    private final EjemploMapper ejemploMapper;

    @Override
    @Cacheable(value = "ejemplos", key = "#id")
    public EjemploDetalleResponse obtenerPorId(Long id) {
        log.debug("Consultando ejemplo ID: {}", id);
        return ejemploRepository.findById(id)
                .map(ejemploMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No encontrado"));
    }

    @Override
    @Transactional
    public EjemploDetalleResponse crear(EjemploRequest request) {
        log.info("Creando ejemplo: {}", request.nombre());
        // Validaciones de unicidad aquí
        Ejemplo entidad = ejemploMapper.toEntity(request);
        asignarRelaciones(entidad, request);
        return ejemploMapper.toDetalleResponse(ejemploRepository.save(entidad));
    }

    @Override
    @Transactional
    @CacheEvict(value = "ejemplos", key = "#id")
    public EjemploDetalleResponse actualizar(Long id, EjemploRequest request) {
        log.info("Actualizando ejemplo ID: {}", id);
        Ejemplo existente = ejemploRepository.findById(id).orElseThrow(...);
        // Validar versión para bloqueo optimista ocurre automáticamente en el save
        ejemploMapper.updateEntity(request, existente);
        asignarRelaciones(existente, request);
        return ejemploMapper.toDetalleResponse(ejemploRepository.save(existente));
    }

    private void asignarRelaciones(Ejemplo entidad, EjemploRequest request) {
        List<String> errores = new ArrayList<>();
        if (request.idRelacion() != null) {
            relacionRepository.findById(request.idRelacion())
                .ifPresentOrElse(entidad::setRelacion, 
                    () -> errores.add("Relación no existe"));
        }
        if (!errores.isEmpty()) throw new ValidacionNegocioException(errores);
    }
}
```

---
*Este estándar asegura servicios rápidos, transaccionales y con una excelente experiencia de usuario.*

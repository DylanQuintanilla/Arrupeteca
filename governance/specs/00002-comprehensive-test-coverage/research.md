## Research Report (Reporte de Investigación)

**Context (Contexto)**: Investigación de mejores prácticas para cobertura de pruebas del 100% en una aplicación Spring Boot (Arrupeteca), con enfoque en Mappers, DTOs y Controladores.

## Mappers (MapStruct) (Mapeadores)
- **Key findings (Hallazgos clave)**: Los mappers son críticos para la transformación de datos entre capas. Deben probarse para un mapeo correcto campo a campo y seguridad ante nulos.
- **Recommended (Recomendado)**: Probar la implementación generada de MapStruct directamente como una prueba unitaria JUnit 5 pura. Usar `Mappers.getMapper(Class)` para instanciar. Probar ambas direcciones (Entidad a DTO y viceversa).
- **Avoid (Evitar)**: Cargar un contexto completo de Spring (`@SpringBootTest`) para lógica simple de mapeo.
### Sources (Fuentes)
- [Symflower](https://symflower.com/en/blog/spring-boot-unit-testing-best-practices/) — Unit testing best practices in Spring Boot.
- [Rieckpil](https://rieckpil.de/testing-mapstruct-mappers-with-junit-5/) — Testing MapStruct mappers with JUnit 5.

## DTOs (Data Transfer Objects) (Objetos de Transferencia de Datos)
- **Key findings (Hallazgos clave)**: Los DTOs suelen contener código repetitivo. La lógica de serialización/deserialización es la parte más crítica a probar.
- **Recommended (Recomendado)**: Usar `@JsonTest` para verificar la serialización y deserialización de Jackson. Para código repetitivo (si no se usan Java Records), herramientas como Bean-Matchers pueden ayudar a alcanzar el 100% de cobertura rápidamente.
- **Avoid (Evitar)**: Pruebas manuales de cada getter y setter si son estándar; enfocarse en nombres JSON y casos de borde.
### Sources (Fuentes)
- [Dev.to](https://dev.to/spring-boot-jsontest/) — Testing JSON serialization in Spring Boot.
- [ScalableHuman](https://scalablehuman.com/testing-dtos-in-spring-boot/) — Strategies for testing DTOs.

## Controllers and Error Handling (Controladores y Manejo de Errores)
- **Key findings (Hallazgos clave)**: Los controladores deben enfocarse en la semántica HTTP (códigos de estado, estructura JSON) y validación.
- **Recommended (Recomendado)**: Usar `@WebMvcTest` con `MockMvc`. Simular servicios para emular tanto éxito como fallo (manejo de excepciones vía `@ControllerAdvice`).
- **Avoid (Evitar)**: Uso de `@SpringBootTest` para pruebas exclusivas de controladores debido a la sobrecarga de rendimiento.
### Sources (Fuentes)
- [Medium](https://medium.com/@spring-boot-controller-testing) — Best practices for controller testing.

### Summary (Resumen)
Lograr el 100% de cobertura requiere un enfoque de pruebas por capas. Los mappers deben probarse para precisión lógica, los DTOs para consistencia de contrato/serialización y los controladores para integridad de la interfaz HTTP. Usar sectores de prueba especializados (`@WebMvcTest`, `@JsonTest`) para mantener las pruebas rápidas y enfocadas.

### Sources Index (Índice de Fuentes)
| URL | Topic (Tema) | Fetched (Recuperado) |
|-----|-------|---------|
| https://symflower.com/en/blog/spring-boot-unit-testing-best-practices/ | Best Practices | 2026-04-12 |
| https://rieckpil.de/testing-mapstruct-mappers-with-junit-5/ | Mappers | 2026-04-12 |
| https://dev.to/spring-boot-jsontest/ | DTOs | 2026-04-12 |
| https://medium.com/@spring-boot-controller-testing | Controllers | 2026-04-12 |

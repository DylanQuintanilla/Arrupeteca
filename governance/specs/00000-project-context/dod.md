# Deployment and Operations Document (DOD) - Arrupeteca (Documento de Despliegue y Operaciones)

## 1. Introduction (Introducción)
Este documento describe el proceso de despliegue, configuración y mantenimiento operativo del sistema Arrupeteca, así como los criterios de aceptación técnicos (Definition of Done) para cualquier nueva funcionalidad o cambio.

## 2. Infrastructure Requirements (Requisitos de Infraestructura)
- **Runtime**: Java 21 (JDK 21+)
- **Build Tool**: Gradle 8.x
- **Framework**: Spring Boot 3.4.2
- **Database**: MySQL 8.x
- **OS**: Linux / Windows / macOS

## 3. Technical Definition of Done (Criterios de Aceptación Técnicos)
Para que un cambio se considere "Terminado", debe cumplir con:
1. **Compilation**: El código debe compilar sin errores ni advertencias críticas.
2. **Style (Checkstyle)**: Cero violaciones de las 5 Golden Rules de Checkstyle (verificado por `./gradlew checkstyleMain`). *(Nota: Temporalmente obviado durante la refactorización de código).*
3. **Testing (JaCoCo)**: Cobertura de pruebas unitarias e integración del 100% en todas las capas (verificado por `./gradlew check`). *(Nota: Temporalmente obviado durante la refactorización de código).*
4. **Entity Integrity**: Las nuevas entidades deben usar `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` incluyendo solo el `id`.
5. **Documentation**: Los cambios arquitectónicos deben ser reflejados en el SAD solicitando permiso MENCIONANDO QUE SE HARA ESE PROCESO.


## 4. Local Deployment (Despliegue Local)

### 4.1 Database Setup (Configuración de Base de Datos)
1. Iniciar el servicio de MySQL.
2. Crear la base de datos `arrupeteca`.
3. Configurar las credenciales en `arrupeteca/src/main/resources/application.properties`.

### 4.2 Build and Run (Construcción y Ejecución)
Desde la carpeta `arrupeteca`, ejecutar:
```bash
./gradlew bootRun
```

## 5. Environment Configuration (Configuración del Entorno)
- `application.properties`: Configuración base.
- `application-dev.properties`: Perfil de desarrollo (configuración local).

## 6. Operations and Monitoring (Operaciones y Monitoreo)
- **Logging**: Los registros se emiten a la consola estándar mediante SLF4J/Logback.
- **Audit**: Los cambios en los datos se rastrean automáticamente en las tablas correspondientes mediante los campos de auditoría heredados de `Auditable`.

## 7. Backup and Recovery (Respaldo y Recuperación)
- El respaldo de la base de datos MySQL debe programarse mediante tareas `mysqldump` regulares.
- El borrado lógico (flag `borradoLogico`) actúa como la primera línea de defensa para la recuperación de datos ante borrados accidentales.

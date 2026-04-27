package com.arrupeteca.dto.response.detail;

import java.time.LocalDateTime;

public record EditorialDetalleResponse(
    Long id,
    String nombre,
    String direccion,
    String correo,
    String telefono,
    String personaContacto,
    boolean borradoLogico,
    String creadoPor,
    LocalDateTime fechaCreacion,
    String modificadoPor,
    LocalDateTime fechaModificacion
) { }

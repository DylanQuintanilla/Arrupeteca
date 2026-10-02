package com.arrupeteca.prestamo.dto.response.summary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrestamoResumen {
    private Long id;
    private Long idSolicitud;
    private Long idUsuario;
    private String nombreUsuario;
    private Long idEstadoPrestamo;
    private String estadoPrestamo;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaLimiteDevolucion;
    private Boolean borradoLogico;
}

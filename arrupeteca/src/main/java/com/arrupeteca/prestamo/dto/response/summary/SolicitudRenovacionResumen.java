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
public class SolicitudRenovacionResumen {
    private Long id;
    private Long idEjemplarPrestamo;
    private Long idUsuario;
    private String nombreUsuario;
    private Long idEstadoSolicitud;
    private String estadoSolicitud;
    private Integer diasSolicitados;
    private LocalDateTime fechaSolicitud;
    private Boolean borradoLogico;
}

package com.arrupeteca.service;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.enums.EstadoFisico;
import com.arrupeteca.persistence.projection.EjemplarLibroResumen;

import java.util.List;

public interface EjemplarLibroService {

    List<EjemplarLibroResumen> obtenerTodosActivos();

    EjemplarLibroResumen obtenerPorIdActivo(Long id);

    List<EjemplarLibroResumen> busquedaGlobalAvanzada(String palabra, String ordenarPor, String direccion);

    List<EjemplarLibroResumen> obtenerTodos();

    EjemplarLibroResumen obtenerPorId(Long id);

    List<EjemplarLibroResumen> busquedaMaestraAdmin(Boolean esBorrado, Long idLibro, Long idMueble, EstadoFisico estadoFisico,
                                                    Disponibilidad disponibilidad, String termino, String ordenarPor, String direccion);

    EjemplarLibroResumen crear(EjemplarLibroRequest request);

    EjemplarLibroResumen actualizar(Long id, EjemplarLibroRequest request);

    void cambiarBorradoLogico(Long id, Boolean estado);

    Long cantidadEjemplaresActivosPorIdLibroYDisponibilidad(Long idLibro, Disponibilidad disponibilidad);

}

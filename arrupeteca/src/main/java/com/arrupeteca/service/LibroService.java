package com.arrupeteca.service;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.persistence.enums.Ciclo;
import com.arrupeteca.persistence.enums.Grado;
import com.arrupeteca.persistence.projection.LibroResumen;

import java.util.List;

public interface LibroService {

    List<LibroResumen> obtenerTodosActivos();

    LibroResumen obtenerPorIdActivo(Long id);

    List<LibroResumen> busquedaGlobalAvanzada(String palabra, String ordenarPor, String direccion);

    List<LibroResumen> obtenerTodos();

    LibroResumen obtenerPorId(Long id);

    List<LibroResumen> busquedaMaestraAdmin(Boolean esBorrado, Long idObra, Long idEditorial, Long idFormato,
                                            Grado grado, Ciclo ciclo, String termino, Integer anioInicio,
                                            Integer anioFin, String ordenarPor, String direccion);

    LibroResumen crear(LibroRequest request);

    LibroResumen actualizar(Long id, LibroRequest request);

    void cambiarBorradoLogico(Long id, Boolean estado);

}

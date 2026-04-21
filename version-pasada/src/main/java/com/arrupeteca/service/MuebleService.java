package com.arrupeteca.service;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.persistence.projection.MuebleResumen;
import org.springframework.data.domain.Page;

public interface MuebleService {

    Page<MuebleResumen> obtenerTodosActivos(int page, int size, String ordenarPor, String direccion);

    MuebleResumen obtenerPorIdActivo(Long id);

    Page<MuebleResumen> busquedaGlobal(String palabra, int page, int size, String ordenarPor, String direccion);

    Page<MuebleResumen> obtenerTodos(int page, int size, String ordenarPor, String direccion);

    MuebleResumen obtenerPorId(Long id);

    Page<MuebleResumen> busquedaMaestraAdmin(
            Boolean esBorrado, Long idSalon, Long idPlanta, Long idEdificio, String termino,
            int page, int size, String ordenarPor, String direccion);

    MuebleResumen crear(MuebleRequest request);

    MuebleResumen actualizar(Long id, MuebleRequest request);

    void cambiarEstado(Long id, boolean estado);
}

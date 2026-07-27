package com.arrupeteca.service;

import java.util.List;

import com.arrupeteca.dto.request.ResponsableRequest;
import com.arrupeteca.dto.response.ResponsableResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResponsableService {

    Page<ResponsableResponse> obtenerTodosActivos(Pageable pageable);

    ResponsableResponse obtenerPorIdActivo(Long id);

    Page<ResponsableResponse> obtenerTodos(Pageable pageable);

    ResponsableResponse obtenerPorId(Long id);

    List<ResponsableResponse> obtenerPorUsuario(Long idUsuario);

    ResponsableResponse crear(ResponsableRequest request);

    ResponsableResponse actualizar(Long id, ResponsableRequest request);

    void cambiarEstado(Long id, boolean estado);

}

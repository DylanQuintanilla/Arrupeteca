package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EdificioMapper;
import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.projection.EdificioResumen;
import com.arrupeteca.persistence.repository.EdificioRepository;
import com.arrupeteca.service.EdificioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EdificioServiceImpl implements EdificioService {

    private final EdificioRepository edificioRepository;
    private final EdificioMapper edificioMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<EdificioResumen> obtenerTodosActivos(Pageable pageable) {
        return edificioRepository.findByBorradoLogicoFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public EdificioResumen obtenerPorIdActivo(Long id) {
        return edificioRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un edificio con ID: "
                        + id + ", o no está activo"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EdificioResumen> obtenerTodos(Pageable pageable) {
        return edificioRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public EdificioResumen obtenerPorId(Long id) {
        return edificioRepository.findProjectedById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un edificio con ID: " + id));
    }

    @Override
    @Transactional
    public EdificioResumen crear(EdificioRequest request) {
        if (edificioRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("El nombre: " + request.getNombre() + " ya ha sido utilizado");
        }
        Edificio edificioMapeada = edificioMapper.toEntity(request);
        Edificio edificioGuarda = edificioRepository.save(edificioMapeada);
        return obtenerPorId(edificioGuarda.getId());
    }

    @Override
    @Transactional
    public EdificioResumen actualizar(Long id, EdificioRequest request) {
        Edificio edificioEncontrado = edificioRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró edificio con ID: " + id));

        if (!edificioEncontrado.getNombre().equalsIgnoreCase(request.getNombre())
                && edificioRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ValidacionNegocioException("Ya existe un edificio con el nombre: " + request.getNombre());
        }

        edificioMapper.updateEntity(request, edificioEncontrado);
        edificioRepository.save(edificioEncontrado);

        return obtenerPorId(edificioEncontrado.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!edificioRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró edificio con ID: " + id);
        }
        if (estado) {
            edificioRepository.activar(id);
        } else {
            edificioRepository.desactivar(id);
        }
    }
}

package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.GradoRequest;
import com.arrupeteca.dto.response.GradoResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.GradoMapper;
import com.arrupeteca.persistence.entity.Grado;
import com.arrupeteca.persistence.repository.GradoRepository;
import com.arrupeteca.service.GradoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GradoServiceImpl implements GradoService {

    private final GradoRepository gradoRepository;
    private final GradoMapper gradoMapper;

    @Override
    @Cacheable(value = "grados-activos")
    public Page<GradoResponse> obtenerTodosActivos(Pageable pageable) {
        log.debug("Obteniendo todos los grados activos con paginación: {}", pageable);
        return gradoRepository.findByBorradoLogicoFalse(pageable)
                .map(gradoMapper::toResponse);
    }

    @Override
    public GradoResponse obtenerPorIdActivo(Long id) {
        log.debug("Buscando grado activo con ID: {}", id);
        return gradoRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .map(gradoMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un grado activo con ID: " + id));
    }

    @Override
    public Page<GradoResponse> obtenerTodos(Pageable pageable) {
        log.debug("Obteniendo todos los grados (incluyendo borrados) con paginación: {}", pageable);
        return gradoRepository.findAllProjectedBy(pageable)
                .map(gradoMapper::toResponse);
    }

    @Override
    public GradoResponse obtenerPorId(Long id) {
        log.debug("Buscando grado con ID: {}", id);
        return gradoRepository.findProjectedById(id)
                .map(gradoMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un grado con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "grados-activos", allEntries = true)
    public GradoResponse crear(GradoRequest request) {
        log.info("Creando nuevo grado con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        Grado grado = gradoMapper.toEntity(request);
        grado.setNombre(nombreTrimmed);

        Grado guardado = gradoRepository.save(grado);
        log.info("Grado creado exitosamente con ID: {}", guardado.getId());
        return gradoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "grados-activos", allEntries = true)
    public GradoResponse actualizar(Long id, GradoRequest request) {
        log.info("Actualizando grado con ID: {}", id);
        Grado gradoExistente = gradoRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un grado con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, gradoExistente);

        gradoMapper.updateEntity(request, gradoExistente);
        gradoExistente.setNombre(nombreTrimmed);

        Grado actualizado = gradoRepository.save(gradoExistente);
        log.info("Grado con ID: {} actualizado exitosamente", id);
        return gradoMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, Grado gradoExistente) {
        boolean esNuevo = (gradoExistente == null);
        boolean nombreCambio = !esNuevo && !gradoExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && gradoRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar grado: {}", nombreNuevo);
            if (esNuevo) {
                throw new ValidacionNegocioException("Ya existe un grado con el nombre: " + nombreNuevo);
            } else {
                throw new ValidacionNegocioException("Ya existe otro grado con el nombre: " + nombreNuevo);
            }
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "grados-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de grado ID: {} a {}", id, !estado);
        if (gradoRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró un grado con ID: " + id);
        }
        if (estado) {
            gradoRepository.activar(id);
        } else {
            gradoRepository.desactivar(id);
        }
    }
}

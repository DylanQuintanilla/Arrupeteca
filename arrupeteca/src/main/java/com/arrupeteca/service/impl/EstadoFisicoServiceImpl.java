package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EstadoFisicoRequest;
import com.arrupeteca.dto.response.EstadoFisicoResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.EstadoFisicoMapper;
import com.arrupeteca.persistence.entity.EstadoFisico;
import com.arrupeteca.persistence.repository.EstadoFisicoRepository;
import com.arrupeteca.service.EstadoFisicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EstadoFisicoServiceImpl implements EstadoFisicoService {

    private final EstadoFisicoRepository estadoFisicoRepository;
    private final EstadoFisicoMapper estadoFisicoMapper;

    @Override
    @Cacheable(value = "estados-fisicos-activos")
    public List<EstadoFisicoResponse> obtenerTodosActivos() {
        log.debug("Obteniendo todos los estados físicos activos en lista plana");
        return estadoFisicoRepository.findByBorradoLogicoFalseOrderByNombreAsc().stream()
                .map(estadoFisicoMapper::toResponse)
                .toList();
    }

    @Override
    public Page<EstadoFisicoResponse> obtenerTodos(String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de estados físicos con filtros - nombre: {}, borradoLogico: {}, pageable: {}", nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return estadoFisicoRepository.busquedaAdmin(nombreFiltro, borradoLogico, pageable)
                .map(estadoFisicoMapper::toResponse);
    }

    @Override
    public EstadoFisicoResponse obtenerPorId(Long id) {
        log.debug("Buscando estado físico por ID: {}", id);
        return estadoFisicoRepository.findById(id)
                .map(estadoFisicoMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un estado físico con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "estados-fisicos-activos", allEntries = true)
    public EstadoFisicoResponse crear(EstadoFisicoRequest request) {
        log.info("Creando nuevo estado físico con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, null);

        EstadoFisico estadoFisico = estadoFisicoMapper.toEntity(request);
        estadoFisico.setNombre(nombreTrimmed);

        EstadoFisico guardado = estadoFisicoRepository.save(estadoFisico);
        log.info("Estado físico creado exitosamente con ID: {}", guardado.getId());
        return estadoFisicoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "estados-fisicos-activos", allEntries = true)
    public EstadoFisicoResponse actualizar(Long id, EstadoFisicoRequest request) {
        log.info("Actualizando estado físico con ID: {}", id);
        EstadoFisico estadoFisicoExistente = estadoFisicoRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un estado físico con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, estadoFisicoExistente);

        estadoFisicoMapper.updateEntity(request, estadoFisicoExistente);
        estadoFisicoExistente.setNombre(nombreTrimmed);

        EstadoFisico actualizado = estadoFisicoRepository.save(estadoFisicoExistente);
        log.info("Estado físico con ID: {} actualizado exitosamente", id);
        return estadoFisicoMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, EstadoFisico estadoFisicoExistente) {
        boolean esNuevo = (estadoFisicoExistente == null);
        boolean nombreCambio = !esNuevo && !estadoFisicoExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && estadoFisicoRepository.existsByNombreIgnoreCase(nombreNuevo)) {
            log.warn("Intento de duplicar estado físico: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un estado físico con el nombre: " + nombreNuevo);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "estados-fisicos-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de estado físico ID: {} a {}", id, !estado);
        if (estadoFisicoRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró un estado físico con ID: " + id);
        }
        if (estado) {
            estadoFisicoRepository.activar(id);
        } else {
            estadoFisicoRepository.desactivar(id);
        }
    }
}

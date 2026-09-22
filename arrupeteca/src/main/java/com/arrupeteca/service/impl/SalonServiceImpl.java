package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.SalonRequest;
import com.arrupeteca.dto.response.SalonResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.SalonMapper;
import com.arrupeteca.persistence.entity.Salon;
import com.arrupeteca.persistence.repository.SalonRepository;
import com.arrupeteca.service.SalonService;
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
public class SalonServiceImpl implements SalonService {

    private final SalonRepository salonRepository;
    private final SalonMapper salonMapper;

    @Override
    @Cacheable(value = "salones-activos", key = "#nombre != null ? #nombre : 'todos'")
    public List<SalonResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo salones activos(as) (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return salonRepository.buscarActivosPorNombre(filtro).stream()
                .map(salonMapper::toResponse)
                .toList();
    }

    @Override
    public Page<SalonResponse> obtenerTodos(Long idPlanta, String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de salones con filtros - idPlanta: {}, nombre: {}, borradoLogico: {}, pageable: {}", idPlanta, nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return salonRepository.busquedaAdmin(idPlanta, nombreFiltro, borradoLogico, pageable)
                .map(salonMapper::toResponse);
    }

    @Override
    public SalonResponse obtenerPorId(Long id) {
        log.debug("Buscando salones por ID: {}", id);
        return salonRepository.findById(id)
                .map(salonMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) salon con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "salones-activos", allEntries = true)
    public SalonResponse crear(SalonRequest request) {
        log.info("Creando nuevo(a) salon con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, request.idPlanta(), null);

        Salon salon = salonMapper.toEntity(request);
        salon.setNombre(nombreTrimmed);

        Salon guardado = salonRepository.save(salon);
        log.info("Salon creado(a) exitosamente con ID: {}", guardado.getId());
        return salonMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "salones-activos", allEntries = true)
    public SalonResponse actualizar(Long id, SalonRequest request) {
        log.info("Actualizando salon con ID: {}", id);
        Salon salonExistente = salonRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) salon con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, request.idPlanta(), salonExistente);

        salonMapper.updateEntity(request, salonExistente);
        salonExistente.setNombre(nombreTrimmed);

        Salon actualizado = salonRepository.save(salonExistente);
        log.info("Salon con ID: {} actualizado(a) exitosamente", id);
        return salonMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, Long idPlanta, Salon salonExistente) {
        boolean esNuevo = (salonExistente == null);
        boolean nombreCambio = !esNuevo && !salonExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && salonRepository.existsByNombreIgnoreCaseAndPlantaId(nombreNuevo, idPlanta)) {
            log.warn("Intento de duplicar salones: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un(a) salon con ese nombre en el/la planta seleccionado(a)");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "salones-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de salones ID: {} a {}", id, !estado);
        if (!salonRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un(a) salon con ID: " + id);
        }
        if (estado) {
            salonRepository.activar(id);
        } else {
            salonRepository.desactivar(id);
        }
    }
}

package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.PlantaRequest;
import com.arrupeteca.dto.response.PlantaResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.PlantaMapper;
import com.arrupeteca.persistence.entity.Planta;
import com.arrupeteca.persistence.repository.PlantaRepository;
import com.arrupeteca.service.PlantaService;
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
public class PlantaServiceImpl implements PlantaService {

    private final PlantaRepository plantaRepository;
    private final PlantaMapper plantaMapper;

    @Override
    @Cacheable(value = "plantas-activas", key = "#nombre != null ? #nombre : 'todos'")
    public List<PlantaResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo plantas de edificios activos(as) (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return plantaRepository.buscarActivosPorNombre(filtro).stream()
                .map(plantaMapper::toResponse)
                .toList();
    }

    @Override
    public Page<PlantaResponse> obtenerTodos(Long idEdificio, String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de plantas con filtros - idEdificio: {}, nombre: {}, borradoLogico: {}, pageable: {}", idEdificio, nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return plantaRepository.busquedaAdmin(idEdificio, nombreFiltro, borradoLogico, pageable)
                .map(plantaMapper::toResponse);
    }

    @Override
    public PlantaResponse obtenerPorId(Long id) {
        log.debug("Buscando plantas de edificios por ID: {}", id);
        return plantaRepository.findById(id)
                .map(plantaMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) planta con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "plantas-activas", allEntries = true)
    public PlantaResponse crear(PlantaRequest request) {
        log.info("Creando nuevo(a) planta con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, request.idEdificio(), null);

        Planta planta = plantaMapper.toEntity(request);
        planta.setNombre(nombreTrimmed);

        Planta guardado = plantaRepository.save(planta);
        log.info("Planta creado(a) exitosamente con ID: {}", guardado.getId());
        return plantaMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "plantas-activas", allEntries = true)
    public PlantaResponse actualizar(Long id, PlantaRequest request) {
        log.info("Actualizando planta con ID: {}", id);
        Planta plantaExistente = plantaRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) planta con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, request.idEdificio(), plantaExistente);

        plantaMapper.updateEntity(request, plantaExistente);
        plantaExistente.setNombre(nombreTrimmed);

        Planta actualizado = plantaRepository.save(plantaExistente);
        log.info("Planta con ID: {} actualizado(a) exitosamente", id);
        return plantaMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, Long idEdificio, Planta plantaExistente) {
        boolean esNuevo = (plantaExistente == null);
        boolean nombreCambio = !esNuevo && !plantaExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && plantaRepository.existsByNombreIgnoreCaseAndEdificioId(nombreNuevo, idEdificio)) {
            log.warn("Intento de duplicar plantas de edificios: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un(a) planta con ese nombre en el/la edificio seleccionado(a)");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "plantas-activas", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de plantas de edificios ID: {} a {}", id, !estado);
        if (!plantaRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un(a) planta con ID: " + id);
        }
        if (estado) {
            plantaRepository.activar(id);
        } else {
            plantaRepository.desactivar(id);
        }
    }
}

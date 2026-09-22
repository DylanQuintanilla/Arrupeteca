package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.dto.response.MuebleResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.MuebleMapper;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.repository.MuebleRepository;
import com.arrupeteca.service.MuebleService;
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
public class MuebleServiceImpl implements MuebleService {

    private final MuebleRepository muebleRepository;
    private final MuebleMapper muebleMapper;

    @Override
    @Cacheable(value = "muebles-activos", key = "#nombre != null ? #nombre : 'todos'")
    public List<MuebleResponse> obtenerTodosActivos(String nombre) {
        log.debug("Obteniendo muebles activos(as) (filtro nombre: '{}')", nombre);
        String filtro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return muebleRepository.buscarActivosPorNombre(filtro).stream()
                .map(muebleMapper::toResponse)
                .toList();
    }

    @Override
    public Page<MuebleResponse> obtenerTodos(Long idSalon, String nombre, Boolean borradoLogico, Pageable pageable) {
        log.debug("Búsqueda de muebles con filtros - idSalon: {}, nombre: {}, borradoLogico: {}, pageable: {}", idSalon, nombre, borradoLogico, pageable);
        String nombreFiltro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        return muebleRepository.busquedaAdmin(idSalon, nombreFiltro, borradoLogico, pageable)
                .map(muebleMapper::toResponse);
    }

    @Override
    public MuebleResponse obtenerPorId(Long id) {
        log.debug("Buscando muebles por ID: {}", id);
        return muebleRepository.findById(id)
                .map(muebleMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) mueble con ID: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "muebles-activos", allEntries = true)
    public MuebleResponse crear(MuebleRequest request) {
        log.info("Creando nuevo(a) mueble con nombre: {}", request.nombre());
        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, request.idSalon(), null);

        Mueble mueble = muebleMapper.toEntity(request);
        mueble.setNombre(nombreTrimmed);

        Mueble guardado = muebleRepository.save(mueble);
        log.info("Mueble creado(a) exitosamente con ID: {}", guardado.getId());
        return muebleMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    @CacheEvict(value = "muebles-activos", allEntries = true)
    public MuebleResponse actualizar(Long id, MuebleRequest request) {
        log.info("Actualizando mueble con ID: {}", id);
        Mueble muebleExistente = muebleRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró un(a) mueble con ID: " + id));

        String nombreTrimmed = request.nombre().trim();
        validarUnicidad(nombreTrimmed, request.idSalon(), muebleExistente);

        muebleMapper.updateEntity(request, muebleExistente);
        muebleExistente.setNombre(nombreTrimmed);

        Mueble actualizado = muebleRepository.save(muebleExistente);
        log.info("Mueble con ID: {} actualizado(a) exitosamente", id);
        return muebleMapper.toResponse(actualizado);
    }

    private void validarUnicidad(String nombreNuevo, Long idSalon, Mueble muebleExistente) {
        boolean esNuevo = (muebleExistente == null);
        boolean nombreCambio = !esNuevo && !muebleExistente.getNombre().equalsIgnoreCase(nombreNuevo);

        if ((esNuevo || nombreCambio) && muebleRepository.existsByNombreIgnoreCaseAndSalonId(nombreNuevo, idSalon)) {
            log.warn("Intento de duplicar muebles: {}", nombreNuevo);
            throw new ValidacionNegocioException("Ya existe un(a) mueble con ese nombre en el/la salon seleccionado(a)");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "muebles-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        log.info("Cambiando estado de borrado de muebles ID: {} a {}", id, !estado);
        if (!muebleRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró un(a) mueble con ID: " + id);
        }
        if (estado) {
            muebleRepository.activar(id);
        } else {
            muebleRepository.desactivar(id);
        }
    }
}

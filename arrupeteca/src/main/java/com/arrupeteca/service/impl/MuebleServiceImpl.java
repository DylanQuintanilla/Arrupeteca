package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.dto.response.MuebleResponse;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.MuebleMapper;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.repository.MuebleRepository;
import com.arrupeteca.service.MuebleService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MuebleServiceImpl implements MuebleService {

    private final MuebleRepository muebleRepository;
    private final MuebleMapper muebleMapper;

    @Override
    @Cacheable(value = "muebles-activos")
    public Page<MuebleResponse> obtenerTodosActivos(Pageable pageable) {
        return muebleRepository.findByBorradoLogicoFalse(pageable)
                .map(muebleMapper::toResponse);
    }

    @Override
    public MuebleResponse obtenerPorIdActivo(Long id) {
        return muebleRepository.findByIdAndBorradoLogicoFalse(id)
                .map(muebleMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Mueble activo con ID: " + id));
    }

    @Override
    public Page<MuebleResponse> busquedaGlobal(String palabra, Pageable pageable) {
        return muebleRepository.busquedaGlobal(palabra, pageable)
                .map(muebleMapper::toResponse);
    }

    @Override
    public Page<MuebleResponse> obtenerTodos(Pageable pageable) {
        return muebleRepository.findAll(pageable)
                .map(muebleMapper::toResponse);
    }

    @Override
    public MuebleResponse obtenerPorId(Long id) {
        return muebleRepository.findById(id)
                .map(muebleMapper::toResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Mueble con ID: " + id));
    }

    @Override
    public Page<MuebleResponse> busquedaMaestraAdmin(Boolean esBorrado, Long idSalon, String termino, Pageable pageable) {
        return muebleRepository.busquedaMaestraAdmin(esBorrado, idSalon, termino, pageable)
                .map(muebleMapper::toResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = "muebles-activos", allEntries = true)
    public MuebleResponse crear(MuebleRequest request) {
        if (muebleRepository.existsByNombreIgnoreCaseAndSalonId(request.nombre(), request.idSalon())) {
            throw new ValidacionNegocioException("Ya existe un mueble con ese nombre en el salón seleccionado");
        }
        Mueble mueble = muebleMapper.toEntity(request);
        return muebleMapper.toResponse(muebleRepository.save(mueble));
    }

    @Override
    @Transactional
    @CacheEvict(value = "muebles-activos", allEntries = true)
    public MuebleResponse actualizar(Long id, MuebleRequest request) {
        Mueble muebleExistente = muebleRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Mueble con ID: " + id));

        if (!muebleExistente.getNombre().equalsIgnoreCase(request.nombre())
                && muebleRepository.existsByNombreIgnoreCaseAndSalonId(request.nombre(), request.idSalon())) {
            throw new ValidacionNegocioException("Ya existe otro mueble con ese nombre en el salón seleccionado");
        }

        muebleMapper.updateEntity(request, muebleExistente);
        return muebleMapper.toResponse(muebleRepository.save(muebleExistente));
    }

    @Override
    @Transactional
    @CacheEvict(value = "muebles-activos", allEntries = true)
    public void cambiarEstado(Long id, boolean estado) {
        if (muebleRepository.countByIdIncludeDeleted(id) == 0) {
            throw new ValidacionNegocioException("No se encontró Mueble con ID: " + id);
        }
        if (estado) {
            muebleRepository.activar(id);
        } else {
            muebleRepository.desactivar(id);
        }
    }
}

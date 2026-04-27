package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.dto.response.detail.MuebleDetalleResponse;
import com.arrupeteca.dto.response.summary.MuebleResumen;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.MuebleMapper;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.repository.MuebleRepository;
import com.arrupeteca.service.MuebleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MuebleServiceImpl implements MuebleService {

    private final MuebleRepository muebleRepository;
    private final MuebleMapper muebleMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<MuebleResumen> obtenerTodosActivos(Pageable pageable) {
        return muebleRepository.findByBorradoLogicoFalse(pageable)
                .map(muebleMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public MuebleDetalleResponse obtenerPorIdActivo(Long id) {
        return muebleRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .map(muebleMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Mueble activo con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MuebleResumen> busquedaGlobal(String palabra, Pageable pageable) {
        return muebleRepository.busquedaGlobal(palabra, pageable)
                .map(muebleMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MuebleResumen> obtenerTodos(Pageable pageable) {
        return muebleRepository.findAllProjectedBy(pageable)
                .map(muebleMapper::toResumen);
    }

    @Override
    @Transactional(readOnly = true)
    public MuebleDetalleResponse obtenerPorId(Long id) {
        return muebleRepository.findProjectedById(id)
                .map(muebleMapper::toDetalleResponse)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Mueble con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MuebleResumen> busquedaMaestraAdmin(Boolean esBorrado, Long idSalon, String termino, Pageable pageable) {
        return muebleRepository.busquedaMaestraAdmin(esBorrado, idSalon, termino, pageable)
                .map(muebleMapper::toResumen);
    }

    @Override
    @Transactional
    public MuebleDetalleResponse crear(MuebleRequest request) {
        if (muebleRepository.existePorNombreYSalonId(request.getNombre(), request.getIdSalon())) {
            throw new ValidacionNegocioException("Ya existe un mueble con ese nombre en el salón seleccionado");
        }
        Mueble mueble = muebleMapper.toEntity(request);
        return muebleMapper.toDetalleResponse(muebleRepository.save(mueble));
    }

    @Override
    @Transactional
    public MuebleDetalleResponse actualizar(Long id, MuebleRequest request) {
        Mueble muebleExistente = muebleRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Mueble con ID: " + id));

        if (!muebleExistente.getNombre().equalsIgnoreCase(request.getNombre())
                && muebleRepository.existePorNombreYSalonId(request.getNombre(), request.getIdSalon())) {
            throw new ValidacionNegocioException("Ya existe otro mueble con ese nombre en el salón seleccionado");
        }

        muebleMapper.updateEntity(request, muebleExistente);
        return muebleMapper.toDetalleResponse(muebleRepository.save(muebleExistente));
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!muebleRepository.existsById(id)) {
            throw new ValidacionNegocioException("No se encontró Mueble con ID: " + id);
        }
        if (estado) {
            muebleRepository.activar(id);
        } else {
            muebleRepository.desactivar(id);
        }
    }
}

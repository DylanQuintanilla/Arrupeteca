package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.exception.ValidacionNegocioException;
import com.arrupeteca.mapper.MuebleMapper;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.projection.MuebleResumen;
import com.arrupeteca.persistence.repository.MuebleRepository;
import com.arrupeteca.persistence.repository.SalonRepository;
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
    private final SalonRepository salonRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<MuebleResumen> obtenerTodosActivos(Pageable pageable) {
        return muebleRepository.findByBorradoLogicoFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public MuebleResumen obtenerPorIdActivo(Long id) {
        return muebleRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Mueble activo con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MuebleResumen> busquedaGlobal(String palabra, Pageable pageable) {
        return muebleRepository.busquedaGlobal(palabra, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MuebleResumen> obtenerTodos(Pageable pageable) {
        return muebleRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public MuebleResumen obtenerPorId(Long id) {
        return muebleRepository.findProjectedById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Mueble con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MuebleResumen> busquedaMaestraAdmin(
            Boolean esBorrado, Long idSalon, Long idPlanta, Long idEdificio, String termino,
            Pageable pageable) {
        return muebleRepository.busquedaMaestraAdmin(esBorrado, idSalon, idPlanta, idEdificio, termino, pageable);
    }

    @Override
    @Transactional
    public MuebleResumen crear(MuebleRequest request) {
        if (muebleRepository.existePorNombreYSalonId(request.getNombre(), request.getIdSalon())) {
            throw new ValidacionNegocioException("Ya existe un mueble con el nombre '" + request.getNombre() + "' en ese salón.");
        }

        Mueble nuevo = muebleMapper.toEntity(request);
        asignarRelaciones(nuevo, request);

        Mueble guardado = muebleRepository.save(nuevo);
        return obtenerPorId(guardado.getId());
    }

    @Override
    @Transactional
    public MuebleResumen actualizar(Long id, MuebleRequest request) {
        Mueble existente = muebleRepository.findById(id)
                .orElseThrow(() -> new ValidacionNegocioException("No se encontró Mueble con ID: " + id));

        if (!existente.getNombre().equalsIgnoreCase(request.getNombre()) &&
                muebleRepository.existePorNombreYSalonId(request.getNombre(), request.getIdSalon())) {
            throw new ValidacionNegocioException("Ya existe un mueble con el nombre '" + request.getNombre() + "' en ese salón.");
        }

        muebleMapper.updateEntity(request, existente);
        asignarRelaciones(existente, request);

        muebleRepository.save(existente);
        return obtenerPorId(existente.getId());
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

    private void asignarRelaciones(Mueble mueble, MuebleRequest request) {
        if (request.getIdSalon() != null) {
            mueble.setSalon(salonRepository.findById(request.getIdSalon())
                    .orElseThrow(() -> new ValidacionNegocioException("El Salón indicado no existe")));
        }
    }
}

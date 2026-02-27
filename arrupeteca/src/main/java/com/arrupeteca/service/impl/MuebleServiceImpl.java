package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.MuebleRequest;
import com.arrupeteca.mapper.MuebleMapper;
import com.arrupeteca.persistence.entity.Mueble;
import com.arrupeteca.persistence.projection.MuebleResumen;
import com.arrupeteca.persistence.repository.MuebleRepository;
import com.arrupeteca.persistence.repository.SalonRepository;
import com.arrupeteca.service.MuebleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MuebleServiceImpl implements MuebleService {

    private final MuebleMapper muebleMapper;
    private final MuebleRepository muebleRepository;

    private final SalonRepository salonRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MuebleResumen> obtenerTodosActivos() {
        return muebleRepository.findByBorradoLogicoFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public MuebleResumen obtenerPorIdActivo(Long id) {
        return muebleRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new RuntimeException("No existe un mueble activo con el ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MuebleResumen> obtenerTodos() {
        return muebleRepository.findAllProjectedBy();
    }

    @Override
    @Transactional(readOnly = true)
    public MuebleResumen obtenerPorId(Long id) {
        return muebleRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new RuntimeException("No existe un mueble con el ID: " + id));
    }

    @Override
    @Transactional
    public MuebleResumen crear(MuebleRequest request) {
        if (muebleRepository.existsByNombreIgnoreCaseAndSalon_Id(request.getNombre(), request.getIdSalon())) {
            throw new RuntimeException("Ya existe el mueble '" + request.getNombre() + "' en el salón indicado.");
        }

        Mueble muebleCreado = muebleMapper.toEntity(request);
        asignarRelaciones(muebleCreado, request);

        Mueble muebleGuardado = muebleRepository.save(muebleCreado);
        return obtenerPorIdActivo(muebleGuardado.getId());
    }

    @Override
    @Transactional
    public MuebleResumen actualizar(Long id, MuebleRequest request) {
        Mueble muebleEncontrado = muebleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró mueble con ID: " + id));

        boolean cambioNombre = !muebleEncontrado.getNombre().equalsIgnoreCase(request.getNombre());
        boolean cambioSalon = !muebleEncontrado.getSalon().getId().equals(request.getIdSalon());

        if ((cambioNombre || cambioSalon) &&
                muebleRepository.existsByNombreIgnoreCaseAndSalon_Id(request.getNombre(), request.getIdSalon())) {
            throw new RuntimeException("Ya existe un mueble con el nombre '" + request.getNombre() + "' en este salón.");
        }

        muebleMapper.updateEntity(request, muebleEncontrado);
        asignarRelaciones(muebleEncontrado, request);

        Mueble muebleActualizado = muebleRepository.save(muebleEncontrado);
        return obtenerPorIdActivo(muebleActualizado.getId());
    }

    @Override
    @Transactional
    public void cambiarBorradoLogico(Long id, Boolean estado) {
        if (!muebleRepository.existsById(id)) {
            throw new RuntimeException("No se encontró mueble con ID: " + id);
        }

        if (estado) {
            muebleRepository.activarMueble(id);
        } else {
            muebleRepository.desactivarMueble(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MuebleResumen> obtenerPorIdSalon(Long idSalon) {
        if (!salonRepository.existsById(idSalon)) {
            throw new RuntimeException("No existe un salón con ID: " + idSalon);
        }

        return muebleRepository.findProjectedBySalon_IdAndBorradoLogicoFalse(idSalon);
    }

    //------------------

    private void asignarRelaciones(Mueble mueble, MuebleRequest request) {
        if (request.getIdSalon() != null) {
            mueble.setSalon(salonRepository.findById(request.getIdSalon())
                    .orElseThrow(() -> new RuntimeException("No existe un salón con ID: " + request.getIdSalon())));
        }
    }
}
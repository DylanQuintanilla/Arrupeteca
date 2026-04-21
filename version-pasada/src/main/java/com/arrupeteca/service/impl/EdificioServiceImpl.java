package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EdificioRequest;
import com.arrupeteca.mapper.EdificioMapper;
import com.arrupeteca.mapper.GeneroMapper;
import com.arrupeteca.persistence.entity.Edificio;
import com.arrupeteca.persistence.projection.EdificioResumen;
import com.arrupeteca.persistence.repository.EdificioRepository;
import com.arrupeteca.persistence.repository.GeneroRepository;
import com.arrupeteca.service.EdificioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EdificioServiceImpl implements EdificioService {

    private final EdificioRepository edificioRepository;
    
    private final EdificioMapper edificioMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EdificioResumen> obtenerTodosActivos() {

        return edificioRepository.findByBorradoLogicoFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public EdificioResumen obtenerPorIdActivo(Long id) {
        return edificioRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new RuntimeException("No se encontro una Edificio con ID: "+id+", o no esta activo"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EdificioResumen> obtenerTodos() {
        return edificioRepository.findAllProjectedBy();
    }

    @Override
    @Transactional(readOnly = true)
    public EdificioResumen obtenerPorId(Long id) {
        return edificioRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro una Edificio con ID: "+id));

    }

    @Override
    @Transactional
    public EdificioResumen crear(EdificioRequest request) {
        if(edificioRepository.existsByNombreIgnoreCase(request.getNombre())){
            throw new RuntimeException("El nombre: "+request.getNombre()+" ya ha sido utilizado");
        }
        Edificio EdificioMapeada = edificioMapper.toEntity(request);
        Edificio EdificioGuarda = edificioRepository.save(EdificioMapeada);
        return obtenerPorId(EdificioGuarda.getId());
    }

    @Override
    @Transactional
    public EdificioResumen actualizar(Long id, EdificioRequest request) {

        Edificio EdificioEncontrada = edificioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro Edificio con ID: "+id));

        if (!EdificioEncontrada.getNombre().equalsIgnoreCase(request.getNombre())
                && edificioRepository.existsByNombreIgnoreCase(request.getNombre())){
            throw new RuntimeException("Ya existe un Edificio con el nombre: "+request.getNombre());
        }

        edificioMapper.updateEntity(request, EdificioEncontrada);
        Edificio EdificioActualizada = edificioRepository.save(EdificioEncontrada);

        return obtenerPorId(EdificioEncontrada.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!edificioRepository.existsById(id)){
            throw new RuntimeException("No se encontro Edificio con ID: "+id);
        }
        if (estado)
            edificioRepository.activar(id);
        else
            edificioRepository.desactivar(id);
    }
}

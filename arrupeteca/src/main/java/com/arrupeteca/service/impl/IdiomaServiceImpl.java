package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.mapper.IdiomaMapper;
import com.arrupeteca.mapper.GeneroMapper;
import com.arrupeteca.persistence.entity.Idioma;
import com.arrupeteca.persistence.projection.IdiomaResumen;
import com.arrupeteca.persistence.repository.IdiomaRepository;
import com.arrupeteca.persistence.repository.GeneroRepository;
import com.arrupeteca.service.IdiomaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IdiomaServiceImpl implements IdiomaService {

    private final IdiomaRepository idiomaRepository;
    
    private final IdiomaMapper idiomaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<IdiomaResumen> obtenerTodosActivos() {

        return idiomaRepository.findByBorradoLogicoFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public IdiomaResumen obtenerPorIdActivo(Long id) {
        return idiomaRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new RuntimeException("No se encontro una Idioma con ID: "+id+", o no esta activo"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdiomaResumen> obtenerTodos() {
        return idiomaRepository.findAllProjectedBy();
    }

    @Override
    @Transactional(readOnly = true)
    public IdiomaResumen obtenerPorId(Long id) {
        return idiomaRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro una Idioma con ID: "+id));

    }

    @Override
    @Transactional
    public IdiomaResumen crear(IdiomaRequest request) {
        if(idiomaRepository.existsByNombreIgnoreCase(request.getNombre())){
            throw new RuntimeException("El nombre: "+request.getNombre()+" ya ha sido utilizado");
        }
        Idioma IdiomaMapeada = idiomaMapper.toEntity(request);
        Idioma IdiomaGuarda = idiomaRepository.save(IdiomaMapeada);
        return obtenerPorId(IdiomaGuarda.getId());
    }

    @Override
    @Transactional
    public IdiomaResumen actualizar(Long id, IdiomaRequest request) {

        Idioma IdiomaEncontrada = idiomaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro Idioma con ID: "+id));

        if (!IdiomaEncontrada.getNombre().equalsIgnoreCase(request.getNombre())
                && idiomaRepository.existsByNombreIgnoreCase(request.getNombre())){
            throw new RuntimeException("Ya existe un genero con el nombre: "+request.getNombre());
        }

        idiomaMapper.updateEntity(request, IdiomaEncontrada);
        Idioma IdiomaActualizada = idiomaRepository.save(IdiomaEncontrada);

        return obtenerPorId(IdiomaEncontrada.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!idiomaRepository.existsById(id)){
            throw new RuntimeException("No se encontro Idioma con ID: "+id);
        }
        if (estado)
            idiomaRepository.activar(id);
        else
            idiomaRepository.desactivar(id);
    }
}

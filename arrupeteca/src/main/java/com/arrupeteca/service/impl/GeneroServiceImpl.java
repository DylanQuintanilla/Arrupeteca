package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.mapper.GeneroMapper;
import com.arrupeteca.persistence.entity.Genero;
import com.arrupeteca.persistence.entity.Nacionalidad;
import com.arrupeteca.persistence.projection.GeneroResumen;
import com.arrupeteca.persistence.repository.GeneroRepository;
import com.arrupeteca.service.GeneroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneroServiceImpl implements GeneroService {

    private final GeneroRepository generoRepository;
    private final GeneroMapper generoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<GeneroResumen> obtenerTodosActivos() {
        return generoRepository.findByBorradoLogicoFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public GeneroResumen obtenerPorIdActivo(Long id) {
        return generoRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new RuntimeException("No existe un genero con el ID: "+id+", o no esta actvo"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<GeneroResumen> obtenerTodos() {
        return generoRepository.findAllProjectedBy();
    }

    @Override
    @Transactional(readOnly = true)
    public GeneroResumen obtenerPorId(Long id) {
        return generoRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("No existe un genero con el ID: " + id ));
    }

    @Override
    @Transactional
    public GeneroResumen crear(GeneroRequest request) {
        if (generoRepository.existsByNombreIgnoreCase(request.getNombre())){
            throw new RuntimeException("Ya existe un genero con el nombre: "+request.getNombre());
        }

        Genero generoBuscada = generoMapper.toEntity(request);
        Genero generoGuardado = generoRepository.save(generoBuscada);

        return obtenerPorId(generoGuardado.getId());
    }

    @Override
    @Transactional
    public GeneroResumen actuaizar(Long id, GeneroRequest request) {
        Genero generoExistente = generoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe un genero con el ID: "+id));

        if(!generoExistente.getNombre().equalsIgnoreCase(request.getNombre())
                && generoRepository.existsByNombreIgnoreCase(request.getNombre())){
            throw new RuntimeException("Ya existe un genero con el nombre: "+request.getNombre());
        }

        generoMapper.updateEntity(request, generoExistente);
        Genero generoActualizado = generoRepository.save(generoExistente);
        return obtenerPorId(generoExistente.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {

        if (generoRepository.existsById(id)){
            throw new RuntimeException("No existe un genero con ID: "+id);
        }

        if (estado){
            generoRepository.activar(id);
        }
        else {
            generoRepository.desactivar(id);
        }

    }
}

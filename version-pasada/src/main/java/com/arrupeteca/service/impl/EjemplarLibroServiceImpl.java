package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.EjemplarLibroRequest;
import com.arrupeteca.mapper.EjemplarLibroMapper;
import com.arrupeteca.persistence.entity.EjemplarLibro;
import com.arrupeteca.persistence.enums.Disponibilidad;
import com.arrupeteca.persistence.enums.EstadoFisico;
import com.arrupeteca.persistence.projection.EjemplarLibroResumen;
import com.arrupeteca.persistence.repository.EjemplarLibroRepository;
import com.arrupeteca.persistence.repository.LibroRepository;
import com.arrupeteca.persistence.repository.MuebleRepository;
import com.arrupeteca.service.EjemplarLibroService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EjemplarLibroServiceImpl implements EjemplarLibroService {

    private final EjemplarLibroRepository ejemplarLibroRepository;
    private final EjemplarLibroMapper ejemplarLibroMapper;
    private final LibroRepository libroRepository;
    private final MuebleRepository muebleRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<EjemplarLibroResumen> obtenerTodosActivos(int page, int size, String ordenarPor, String direccion) {
        Pageable pageable = construirPageable(page, size, ordenarPor, direccion);
        return ejemplarLibroRepository.findByBorradoLogicoFalse(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public EjemplarLibroResumen obtenerPorIdActivo(Long id) {
        return ejemplarLibroRepository.findProjectedByIdAndBorradoLogicoFalse(id)
                .orElseThrow(() -> new RuntimeException("No se encontró Ejemplar activo con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EjemplarLibroResumen> busquedaGlobal(String palabra, int page, int size, String ordenarPor, String direccion) {
        Pageable pageable = construirPageable(page, size, ordenarPor, direccion);
        return ejemplarLibroRepository.busquedaGlobal(palabra, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EjemplarLibroResumen> obtenerTodos(int page, int size, String ordenarPor, String direccion) {
        Pageable pageable = construirPageable(page, size, ordenarPor, direccion);
        return ejemplarLibroRepository.findAllProjectedBy(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public EjemplarLibroResumen obtenerPorId(Long id) {
        return ejemplarLibroRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró Ejemplar con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EjemplarLibroResumen> busquedaMaestraAdmin(
            Boolean esBorrado, Long idLibro, Long idMueble, String estadoFisicoStr, String disponibilidadStr,
            String termino, int page, int size, String ordenarPor, String direccion) {

        Pageable pageable = construirPageable(page, size, ordenarPor, direccion);

        EstadoFisico estadoFisico = (estadoFisicoStr != null && !estadoFisicoStr.isEmpty())
                ? EstadoFisico.valueOf(estadoFisicoStr) : null;

        Disponibilidad disponibilidad = (disponibilidadStr != null && !disponibilidadStr.isEmpty())
                ? Disponibilidad.valueOf(disponibilidadStr) : null;

        return ejemplarLibroRepository.busquedaMaestraAdmin(esBorrado, idLibro, idMueble, estadoFisico,
                disponibilidad, termino, pageable);
    }

    @Override
    @Transactional
    public EjemplarLibroResumen crear(EjemplarLibroRequest request) {
        EjemplarLibro nuevo = ejemplarLibroMapper.toEntity(request);
        asignarRelaciones(nuevo, request);
        EjemplarLibro guardado = ejemplarLibroRepository.save(nuevo);
        return obtenerPorId(guardado.getId());
    }

    @Override
    @Transactional
    public EjemplarLibroResumen actualizar(Long id, EjemplarLibroRequest request) {
        EjemplarLibro existente = ejemplarLibroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró Ejemplar con ID: " + id));

        ejemplarLibroMapper.updateEntity(request, existente);
        asignarRelaciones(existente, request);

        EjemplarLibro actualizado = ejemplarLibroRepository.save(existente);
        return obtenerPorId(actualizado.getId());
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, boolean estado) {
        if (!ejemplarLibroRepository.existsById(id)) {
            throw new RuntimeException("No se encontró Ejemplar con ID: " + id);
        }
        if (estado) ejemplarLibroRepository.activar(id);
        else ejemplarLibroRepository.desactivar(id);
    }

    private void asignarRelaciones(EjemplarLibro ejemplar, EjemplarLibroRequest request) {
        if (request.getIdLibro() != null) {
            ejemplar.setLibro(libroRepository.findById(request.getIdLibro())
                    .orElseThrow(() -> new RuntimeException("El Libro indicado no existe")));
        }
        if (request.getIdMueble() != null) {
            ejemplar.setMueble(muebleRepository.findById(request.getIdMueble())
                    .orElseThrow(() -> new RuntimeException("El Mueble indicado no existe")));
        }
    }

    private Pageable construirPageable(int page, int size, String ordenarPor, String direccion) {
        String campoOrden = (ordenarPor == null || ordenarPor.trim().isEmpty()) ? "id" : ordenarPor;

        if ("libro".equalsIgnoreCase(campoOrden)) campoOrden = "libro.obra.titulo";
        if ("mueble".equalsIgnoreCase(campoOrden)) campoOrden = "mueble.nombre";
        if ("isbn".equalsIgnoreCase(campoOrden)) campoOrden = "libro.isbn";

        Sort.Direction direction = (direccion != null && direccion.equalsIgnoreCase("DESC"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, campoOrden);
        return PageRequest.of(page, size, sort);
    }
}
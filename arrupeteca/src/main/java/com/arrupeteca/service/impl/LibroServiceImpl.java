package com.arrupeteca.service.impl;

import com.arrupeteca.dto.request.LibroRequest;
import com.arrupeteca.mapper.LibroMapper;
import com.arrupeteca.persistence.entity.Idioma;
import com.arrupeteca.persistence.entity.Libro;
import com.arrupeteca.persistence.enums.Ciclo;
import com.arrupeteca.persistence.enums.Grado;
import com.arrupeteca.persistence.projection.LibroResumen;
import com.arrupeteca.persistence.repository.*;
import com.arrupeteca.service.LibroService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LibroServiceImpl implements LibroService {

    private final LibroMapper libroMapper;
    private final LibroRepository libroRepository;

    private final ObraRepository obraRepository;
    private final EditorialRepository editorialRepository;
    private final FormatoRepository formatoRepository;
    private final IdiomaRepository idiomaRepository;


    @Override
    @Transactional(readOnly = true)
    public List<LibroResumen> obtenerTodosActivos() {
        return libroRepository.findByBorradoLogicoFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public LibroResumen obtenerPorIdActivo(Long id) {
        return libroRepository.findProjectedByIdAndBorradoLogicoFalse(id).
                orElseThrow(() -> new RuntimeException("El libro no se encuentra con el ID: "+ id + "o no esta activo"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroResumen> busquedaGlobalAvanzada(String palabra, String ordenarPor, String direccion) {
        Sort sort = construirSort(ordenarPor, direccion);
        return libroRepository.busquedaGlobalAvanzada(palabra, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroResumen> obtenerTodos() {

        return libroRepository.findAllProjectedBy();
    }

    @Override
    @Transactional(readOnly = true)
    public LibroResumen obtenerPorId(Long id) {

        return libroRepository.findProjectedById(id)
                .orElseThrow(() -> new RuntimeException("El libro no se encuentra con el ID: "+ id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroResumen> busquedaMaestraAdmin(Boolean esBorrado, Long idObra, Long idEditorial, Long idFormato,
                                                   Grado grado, Ciclo ciclo, String termino, Integer anioInicio,
                                                   Integer anioFin, String ordenarPor, String direccion) {
        Sort sort = construirSort(ordenarPor, direccion);
        return libroRepository.busquedaMaestraAdmin(esBorrado, idObra, idEditorial, idFormato, grado, ciclo, termino, anioInicio, anioFin, sort);
    }

    @Override
    @Transactional
    public LibroResumen crear(LibroRequest request) {
        if (request.getIsbn() != null && !request.getIsbn().isBlank() &&
                libroRepository.existsByIsbnIgnoreCase(request.getIsbn())) {
            throw new RuntimeException("Ya existe un libro con el ISBN: " + request.getIsbn());
        }

        Libro libroNuevo = libroMapper.toEntity(request);
        asignarRelaciones(libroNuevo, request);
        Libro libroGuardado = libroRepository.save(libroNuevo);
        return obtenerPorId(libroGuardado.getId());
    }

    @Override
    @Transactional
    public LibroResumen actualizar(Long id, LibroRequest request) {

        Libro libroExistente = libroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe un libro con ID: "+id));

        if (request.getIsbn() != null && !request.getIsbn().isBlank()) {
            if ((libroExistente.getIsbn() == null || !libroExistente.getIsbn().equalsIgnoreCase(request.getIsbn()))
                    && libroRepository.existsByIsbnIgnoreCase(request.getIsbn())) {
                throw new RuntimeException("Ya existe un libro con el ISBN: " + request.getIsbn());
            }
        }
        libroMapper.updateEntity(request, libroExistente);
        asignarRelaciones(libroExistente, request);
        Libro libroActualizado = libroRepository.save(libroExistente);

        return obtenerPorId(libroActualizado.getId());
    }

    @Override
    @Transactional
    public void cambiarBorradoLogico(Long id, Boolean estado) {

        if (!libroRepository.existsById(id)){
            throw new RuntimeException("No se encontro un libro con ID: "+id);
        }

        if (estado){
            libroRepository.activar(id);
        }
        else {
            libroRepository.desactivar(id);
        }

    }

    //-------------

    private void asignarRelaciones(Libro libro, LibroRequest request) {

        if (request.getIdFormato() != null) {
            libro.setFormato(formatoRepository.findById(request.getIdFormato())
                    .orElseThrow(() -> new RuntimeException("El formato indicado no existe")));
        }

        if (request.getIdObra() != null) {
            libro.setObra(obraRepository.findById(request.getIdObra())
                    .orElseThrow(() -> new RuntimeException("La obra indicada no existe")));
        }

        if (request.getIdEditorial() != null) {
            libro.setEditorial(editorialRepository.findById(request.getIdEditorial())
                    .orElseThrow(() -> new RuntimeException("La editorial indicada no existe")));
        }


        libro.getIdiomas().clear();

        if (request.getIdIdiomas() != null && !request.getIdIdiomas().isEmpty()) {

            List<Idioma> idiomasEncontrados = idiomaRepository.findAllById(request.getIdIdiomas());

            if (idiomasEncontrados.size() != request.getIdIdiomas().size()) {
                throw new RuntimeException("Uno o más idiomas indicados no existen en la base de datos");
            }

            libro.getIdiomas().addAll(idiomasEncontrados);
        }
    }

    private Sort construirSort(String ordenarPor, String direccion) {

        String campoOrden = "obra.titulo";
        if (ordenarPor != null && !ordenarPor.trim().isEmpty()) {

            if (ordenarPor.equalsIgnoreCase("titulo")) {
                campoOrden = "obra.titulo";
            }
            else if (ordenarPor.equalsIgnoreCase("editorial")) {
                campoOrden = "editorial.nombre";
            }
            else {
                campoOrden = ordenarPor;
            }
        }

        Sort.Direction direction = (direccion != null && direccion.equalsIgnoreCase("DESC"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        return Sort.by(direction, campoOrden);
    }
}

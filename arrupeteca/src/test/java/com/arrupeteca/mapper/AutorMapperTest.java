package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.AutorRequest;
import com.arrupeteca.persistence.entity.Autor;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AutorMapperTest {

    private final AutorMapper mapper = Mappers.getMapper(AutorMapper.class);

    @Test
    void toEntityWithNonNullRequestShouldMapCorrectly() {
        AutorRequest request = AutorRequest.builder()
                .nombre("Gabriel Garcia Marquez")
                .seudonimo("Gabo")
                .anioNacimiento(1927)
                .biografia("Escritor colombiano...")
                .build();

        Autor entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals(request.getNombre(), entity.getNombre());
        assertEquals(request.getSeudonimo(), entity.getSeudonimo());
        assertEquals(request.getAnioNacimiento(), entity.getAnioNacimiento());
        assertEquals(request.getBiografia(), entity.getBiografia());
    }

    @Test
    void toEntityWithNullRequestShouldReturnNull() {
        Autor entity = mapper.toEntity(null);

        assertNull(entity);
    }

    @Test
    void updateEntityWithNonNullArgumentsShouldUpdateCorrectly() {
        AutorRequest request = AutorRequest.builder()
                .nombre("Gabo Updated")
                .biografia("Biografia Actualizada")
                .build();
        Autor entity = new Autor();
        entity.setNombre("Gabo Old");
        entity.setBiografia("Bio Old");

        mapper.updateEntity(request, entity);

        assertEquals(request.getNombre(), entity.getNombre());
        assertEquals(request.getBiografia(), entity.getBiografia());
    }

    @Test
    void updateEntityWithNullRequestShouldNotUpdate() {
        Autor entity = new Autor();
        entity.setNombre("Gabo");

        mapper.updateEntity(null, entity);

        assertEquals("Gabo", entity.getNombre());
    }
}

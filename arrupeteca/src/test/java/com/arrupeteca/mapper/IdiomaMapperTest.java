package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.IdiomaRequest;
import com.arrupeteca.persistence.entity.Idioma;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class IdiomaMapperTest {

    private final IdiomaMapper mapper = Mappers.getMapper(IdiomaMapper.class);

    @Test
    void toEntityWithNonNullRequestShouldMapCorrectly() {
        IdiomaRequest request = IdiomaRequest.builder()
                .nombre("Español")
                .build();

        Idioma entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals(request.getNombre(), entity.getNombre());
    }

    @Test
    void toEntityWithNullRequestShouldReturnNull() {
        Idioma entity = mapper.toEntity(null);

        assertNull(entity);
    }

    @Test
    void updateEntityWithNonNullArgumentsShouldUpdateCorrectly() {
        IdiomaRequest request = IdiomaRequest.builder()
                .nombre("Inglés")
                .build();
        Idioma entity = new Idioma();
        entity.setNombre("Español");

        mapper.updateEntity(request, entity);

        assertEquals(request.getNombre(), entity.getNombre());
    }

    @Test
    void updateEntityWithNullRequestShouldNotUpdate() {
        Idioma entity = new Idioma();
        entity.setNombre("Español");

        mapper.updateEntity(null, entity);

        assertEquals("Español", entity.getNombre());
    }
}

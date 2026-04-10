package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.GeneroRequest;
import com.arrupeteca.persistence.entity.Genero;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class GeneroMapperTest {

    private final GeneroMapper mapper = Mappers.getMapper(GeneroMapper.class);

    @Test
    void toEntityWithNonNullRequestShouldMapCorrectly() {
        GeneroRequest request = GeneroRequest.builder()
                .nombre("Drama")
                .build();

        Genero entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals(request.getNombre(), entity.getNombre());
    }

    @Test
    void toEntityWithNullRequestShouldReturnNull() {
        Genero entity = mapper.toEntity(null);

        assertNull(entity);
    }

    @Test
    void updateEntityWithNonNullArgumentsShouldUpdateCorrectly() {
        GeneroRequest request = GeneroRequest.builder()
                .nombre("Comedia")
                .build();
        Genero entity = new Genero();
        entity.setNombre("Drama");

        mapper.updateEntity(request, entity);

        assertEquals(request.getNombre(), entity.getNombre());
    }

    @Test
    void updateEntityWithNullRequestShouldNotUpdate() {
        Genero entity = new Genero();
        entity.setNombre("Drama");

        mapper.updateEntity(null, entity);

        assertEquals("Drama", entity.getNombre());
    }
}

package com.arrupeteca.mapper;

import com.arrupeteca.dto.request.CategoriaRequest;
import com.arrupeteca.persistence.entity.Categoria;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CategoriaMapperTest {

    private final CategoriaMapper mapper = Mappers.getMapper(CategoriaMapper.class);

    @Test
    void toEntityWithNonNullRequestShouldMapCorrectly() {
        CategoriaRequest request = CategoriaRequest.builder()
                .nombre("Novela")
                .build();

        Categoria entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals(request.getNombre(), entity.getNombre());
    }

    @Test
    void toEntityWithNullRequestShouldReturnNull() {
        Categoria entity = mapper.toEntity(null);

        assertNull(entity);
    }

    @Test
    void updateEntityWithNonNullArgumentsShouldUpdateCorrectly() {
        CategoriaRequest request = CategoriaRequest.builder()
                .nombre("Novela Actualizada")
                .build();
        Categoria entity = new Categoria();
        entity.setNombre("Novela Antigua");

        mapper.updateEntity(request, entity);

        assertEquals(request.getNombre(), entity.getNombre());
    }

    @Test
    void updateEntityWithNullRequestShouldNotUpdate() {
        Categoria entity = new Categoria();
        entity.setNombre("Novela");

        mapper.updateEntity(null, entity);

        assertEquals("Novela", entity.getNombre());
    }
}

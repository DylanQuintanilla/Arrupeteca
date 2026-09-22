package com.arrupeteca.mapper.util;

import org.openapitools.jackson.nullable.JsonNullable;
import org.mapstruct.Condition;
import org.springframework.stereotype.Component;

@Component
public class JsonNullableMapper {

    @Condition
    public boolean isPresent(JsonNullable<?> nullable) {
        return nullable != null && nullable.isPresent();
    }

    public <T> T unwrap(JsonNullable<T> nullable) {
        return nullable == null ? null : nullable.orElse(null);
    }
}

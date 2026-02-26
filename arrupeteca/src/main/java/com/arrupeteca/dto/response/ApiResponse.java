package com.arrupeteca.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private boolean exito;

    private String mensaje;

    private T data;

    public static <T> ApiResponse<T> exito(String mensaje, T data) {
        return ApiResponse.<T>builder()
                .exito(true)
                .mensaje(mensaje)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> exito(String mensaje) {
        return ApiResponse.<T>builder()
                .exito(true)
                .mensaje(mensaje)
                .build();
    }

    public static <T> ApiResponse<T> error(String mensaje) {
        return ApiResponse.<T>builder()
                .exito(false)
                .mensaje(mensaje)
                .build();
    }

    public static <T> ApiResponse<T> error(String mensaje, T data) {
        return ApiResponse.<T>builder()
                .exito(false)
                .mensaje(mensaje)
                .data(data)
                .build();
    }
}

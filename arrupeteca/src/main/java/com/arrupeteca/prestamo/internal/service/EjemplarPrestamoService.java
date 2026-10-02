package com.arrupeteca.prestamo.internal.service;

import com.arrupeteca.prestamo.dto.request.EjemplarPrestamoRequest;
import com.arrupeteca.prestamo.dto.response.detail.EjemplarPrestamoDetalleResponse;

import java.util.List;

public interface EjemplarPrestamoService {

    List<EjemplarPrestamoDetalleResponse> obtenerPorPrestamo(Long idPrestamo);

    EjemplarPrestamoDetalleResponse obtenerPorId(Long id);

    EjemplarPrestamoDetalleResponse agregarEjemplar(EjemplarPrestamoRequest request);

    EjemplarPrestamoDetalleResponse actualizarEjemplar(Long id, EjemplarPrestamoRequest request);

    void cambiarEstado(Long id, boolean estado);

    void pagarMora(Long id);

    void exentarMora(Long id);
}

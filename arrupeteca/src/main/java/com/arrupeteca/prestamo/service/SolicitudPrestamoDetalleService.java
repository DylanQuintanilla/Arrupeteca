package com.arrupeteca.prestamo.service;

import com.arrupeteca.prestamo.dto.request.SolicitudPrestamoDetalleRequest;
import com.arrupeteca.prestamo.dto.response.detail.SolicitudPrestamoDetalleItemResponse;

import java.util.List;

public interface SolicitudPrestamoDetalleService {

    List<SolicitudPrestamoDetalleItemResponse> obtenerPorSolicitud(Long idSolicitud);

    SolicitudPrestamoDetalleItemResponse obtenerPorId(Long id);

    SolicitudPrestamoDetalleItemResponse agregarDetalle(SolicitudPrestamoDetalleRequest request);

    SolicitudPrestamoDetalleItemResponse actualizarDetalle(Long id, SolicitudPrestamoDetalleRequest request);

    void cambiarEstado(Long id, boolean estado);
}

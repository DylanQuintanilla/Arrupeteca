package com.arrupeteca.config;

/**
 * Constantes centralizadas para los nombres canónicos de catálogos de estado y disponibilidad.
 */
public final class CatalogoEstadosConstants {

    private CatalogoEstadosConstants() {
    }

    public static final class Disponibilidad {
        public static final String DISPONIBLE = "DISPONIBLE";
        public static final String PRESTADO = "PRESTADO";
        public static final String RESERVADO = "RESERVADO";
        public static final String MANTENIMIENTO = "MANTENIMIENTO";
        public static final String PERDIDO = "PERDIDO";
        public static final String BOTADO = "BOTADO";
        public static final String DONADO = "DONADO";

        private Disponibilidad() {
        }
    }

    public static final class EstadoSolicitud {
        public static final String PENDIENTE = "PENDIENTE";
        public static final String APROBADA = "APROBADA";
        public static final String RECHAZADA = "RECHAZADA";
        public static final String ENTREGADA = "ENTREGADA";
        public static final String CANCELADA = "CANCELADA";

        private EstadoSolicitud() {
        }
    }

    public static final class EstadoPrestamo {
        public static final String ACTIVO = "ACTIVO";
        public static final String ENTREGADO = "ENTREGADO";
        public static final String FINALIZADO = "FINALIZADO";
        public static final String CON_MORA = "CON_MORA";
        public static final String VENCIDO = "VENCIDO";
        public static final String CANCELADO = "CANCELADO";

        private EstadoPrestamo() {
        }
    }

    public static final class EstadoEjemplarPrestamo {
        public static final String EN_PRESTAMO = "EN_PRESTAMO";
        public static final String DEVUELTO = "DEVUELTO";
        public static final String PERDIDO = "PERDIDO";
        public static final String REPUESTO = "REPUESTO";
        public static final String DANADO = "DANADO";

        private EstadoEjemplarPrestamo() {
        }
    }

    public static final class EstadoSolicitudTransferencia {
        public static final String PENDIENTE = "PENDIENTE";
        public static final String APROBADA = "APROBADA";
        public static final String RECHAZADA = "RECHAZADA";
        public static final String FINALIZADA = "FINALIZADA";

        private EstadoSolicitudTransferencia() {
        }
    }
}

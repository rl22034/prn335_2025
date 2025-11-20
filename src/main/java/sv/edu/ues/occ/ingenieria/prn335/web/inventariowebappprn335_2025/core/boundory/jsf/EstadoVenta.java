package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

/**
 * Estados posibles para una venta en el sistema de inventario.
 */
public enum EstadoVenta {
    PENDIENTE("Pendiente"),           // La venta fue creada pero no procesada
    CONFIRMADA("Confirmada"),         // Cliente confirmó la venta
    PREPARANDO("Preparando"),         // Se están preparando los productos
    DESPACHADA("Despachada"),         // Productos enviados al cliente
    CANCELADA("Cancelada");           // La venta fue cancelada

    private final String descripcion;

    EstadoVenta(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la descripción del estado
     * @return Descripción legible del estado
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Convierte un String al enum correspondiente
     * @param valor Valor como String (ej: "PENDIENTE")
     * @return EstadoVenta correspondiente, o PENDIENTE si no se encuentra
     */
    public static EstadoVenta fromString(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return PENDIENTE;
        }
        try {
            return EstadoVenta.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDIENTE;
        }
    }
}

package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

/**
 * Estados posibles para un detalle de venta en el sistema de inventario.
 */
public enum EstadoVentaDetalle {
    PENDIENTE("Pendiente"),           // El detalle está pendiente de preparar
    PREPARANDO("Preparando"),         // El producto se está preparando
    DESPACHADO("Despachado"),         // El producto fue despachado al cliente
    DEVUELTO("Devuelto"),             // El cliente devolvió el producto
    CANCELADO("Cancelado");           // El detalle fue cancelado

    private final String descripcion;

    EstadoVentaDetalle(String descripcion) {
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
     * @return EstadoVentaDetalle correspondiente, o PENDIENTE si no se encuentra
     */
    public static EstadoVentaDetalle fromString(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return PENDIENTE;
        }
        try {
            return EstadoVentaDetalle.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDIENTE;
        }
    }
}

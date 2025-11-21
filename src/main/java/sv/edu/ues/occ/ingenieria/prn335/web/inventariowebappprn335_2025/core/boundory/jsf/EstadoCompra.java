package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

/**
 * Estados posibles para una compra en el sistema de inventario.
 */
public enum EstadoCompra {
    PENDIENTE("Pendiente"),           // La compra fue creada pero no procesada
    EN_PROCESO("En Proceso"),         // Se están recibiendo/validando productos
    COMPLETADA("Completada"),         // Todo recibido y validado
    PAGADA("Pagada"),                 // La compra ha sido pagada
    CANCELADA("Cancelada");           // La compra fue cancelada

    private final String descripcion;

    EstadoCompra(String descripcion) {
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
     * @return EstadoCompra correspondiente, o PENDIENTE si no se encuentra
     */
    public static EstadoCompra fromString(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return PENDIENTE;
        }
        try {
            return EstadoCompra.valueOf(valor.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDIENTE;
        }
    }
}

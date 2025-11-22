package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

public enum EstadoCompraDetalle {
    PENDIENTE("Pendiente"),           // El detalle está pendiente de recibir
    RECIBIDO("Recibido"),             // El producto fue recibido
    CANCELADO("Cancelado");           // El detalle fue cancelado

    private final String descripcion;

    EstadoCompraDetalle(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

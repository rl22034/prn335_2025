package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ClienteDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.NotificadorKardex;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.VentaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.VentaDetalleDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Cliente;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Producto;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Venta;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.VentaDetalle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Named("ventaBean")
@ViewScoped
public class VentaFrm extends DefaultFrm<Venta> implements Serializable {

    @Inject
    private VentaDAO ventaDAO;

    @Inject
    private ClienteDAO clienteDAO;

    @Inject
    private VentaDetalleDAO ventaDetalleDAO;

    @Inject
    private ProductoDAO productoDAO;

    @Inject
    private NotificadorKardex notificadorKardex;

    private List<VentaDetalle> detallesDeLaVenta = new ArrayList<>();
    private VentaDetalle detalleSeleccionado;
    private List<Producto> productosDisponibles = new ArrayList<>();

    @PostConstruct
    public void init() {
        super.init();
        cargarProductosDisponibles();
    }

    @Override
    protected VentaDAO obtenerDAO() {
        return ventaDAO;
    }

    @Override
    protected void validarAntesDeCrear(Venta entidad) throws Exception {
        if (entidad.getFecha() == null || entidad.getIdCliente() == null || entidad.getEstado() == null) {
            throw new Exception("validacion.venta.campos.requeridos");
        }

        // Generar UUID para la nueva venta
        if (entidad.getId() == null) {
            entidad.setId(UUID.randomUUID());
        }
    }

    @Override
    protected void validarAntesDeActualizar(Venta entidad) throws Exception {
        if (entidad.getFecha() == null || entidad.getIdCliente() == null || entidad.getEstado() == null) {
            throw new Exception("validacion.venta.campos.requeridos");
        }
    }

    /**
     * Hook ejecutado antes de eliminar. Valida que no tenga productos asociados.
     * Las validaciones de existencia ya las hace DefaultFrm automáticamente.
     */
    @Override
    protected void validarAntesDeEliminar(Venta entidad, Venta original) throws Exception {
        List<VentaDetalle> detalles = ventaDetalleDAO.getDetallesPorVenta(entidad.getId());
        if (detalles != null && !detalles.isEmpty()) {
            throw new Exception("validacion.venta.tiene.productos");
        }
    }

    /**
     * Buscar ventas excluyendo las DESPACHADAS (para que no aparezcan en esta tabla)
     * Las ventas DESPACHADAS solo se ven en DespachoVenta.xhtml
     */
    @Override
    protected List<Venta> buscarEntidades(int first, int pageSize) throws Exception {
        return ventaDAO.findExcluyendoEstado("DESPACHADA", first, pageSize);
    }

    /**
     * Contar ventas excluyendo las DESPACHADAS
     */
    @Override
    protected Long contarEntidades() throws Exception {
        return ventaDAO.countExcluyendoEstado("DESPACHADA");
    }

    @Override
    protected Venta instanciarEntidad() {
        Venta nueva = new Venta();
        nueva.setFecha(OffsetDateTime.now(ZoneId.of("America/El_Salvador")));
        nueva.setEstado("PENDIENTE");
        return nueva;
    }

    /**
     * Sobrescribe el btnNuevo de DefaultFrm
     * Limpia los detalles al crear una nueva venta
     */
    @Override
    public void btnNuevo() {
        super.btnNuevo();
        this.detallesDeLaVenta = new ArrayList<>();
        this.detalleSeleccionado = null;
    }

    /**
     * Override onRowSelect para cargar los detalles cuando se selecciona una fila
     * Esto soluciona el bug de ver los mismos productos en todas las ventas
     */
    @Override
    public void onRowSelect(org.primefaces.event.SelectEvent<Venta> event) {
        super.onRowSelect(event);  // Cambia estado a MODIFICAR
        cargarDetallesVenta();      // Carga los detalles específicos de esta venta
    }

    /**
     * Obtiene los clientes para el dropdown
     * @return Lista de clientes activos
     */
    public List<Cliente> getClientesActivos() {
        try {
            if (clienteDAO == null) {
                throw new IllegalStateException("ClienteDAO no fue inyectado.");
            }
            return clienteDAO.findRange(0, 1000);
        } catch (Exception e) {
            mostrarError("mensaje.cargar.error", e);
            return new ArrayList<>();
        }
    }

    /**
     * Verifica si la venta actual es nueva (se está creando)
     * @return true si el estado es CREAR, false en caso contrario
     */
    public boolean isVentaNueva() {
        return this.getEstado() == CRUD.CREAR;
    }

    /**
     * Carga los detalles de la venta seleccionada
     */
    public void cargarDetallesVenta() {
        if (this.filaSeleccionada != null && this.filaSeleccionada.getId() != null) {
            try {
                if (ventaDetalleDAO == null) {
                    throw new IllegalStateException("VentaDetalleDAO no fue inyectado.");
                }
                this.detallesDeLaVenta = ventaDetalleDAO.getDetallesPorVenta(this.filaSeleccionada.getId());
            } catch (Exception e) {
                mostrarError("mensaje.cargar.error", e);
                this.detallesDeLaVenta = new ArrayList<>();
            }
        } else {
            this.detallesDeLaVenta = new ArrayList<>();
        }
    }

    /**
     * Prepara un detalle existente para editar
     */
    public void prepararEditarDetalle(VentaDetalle detalle) {
        this.detalleSeleccionado = detalle;
    }

    /**
     * Guarda el detalle (CREAR o ACTUALIZAR según si existe en BD)
     */
    public void guardarDetalle() {
        try {
            if (ventaDetalleDAO == null) {
                throw new IllegalStateException("VentaDetalleDAO no fue inyectado.");
            }

            VentaDetalle existente = ventaDetalleDAO.finById(this.detalleSeleccionado.getId());

            if (existente == null) {
                ventaDetalleDAO.crear(this.detalleSeleccionado);
                MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.crear.exito");
            } else {
                ventaDetalleDAO.update(this.detalleSeleccionado);
                MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.actualizar.exito");
            }

            cargarDetallesVenta();
            PrimeFaces.current().executeScript("PF('dlgVentaDetalle').hide()");

        } catch (Exception e) {
            mostrarError("mensaje.crear.error", e);
        }
    }

    /**
     * Elimina un detalle de la venta
     */
    public void eliminarDetalle(VentaDetalle detalle) {
        try {
            if (ventaDetalleDAO == null) {
                throw new IllegalStateException("VentaDetalleDAO no fue inyectado.");
            }
            ventaDetalleDAO.delete(detalle);
            cargarDetallesVenta();
            MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.eliminar.exito");
        } catch (Exception e) {
            mostrarError("mensaje.eliminar.error", e);
        }
    }

    /**
     * Verifica si el detalle seleccionado es nuevo (no existe en BD)
     * @return true si es nuevo, false si ya existe
     */
    public boolean isDetalleNuevo() {
        if (this.detalleSeleccionado == null || this.detalleSeleccionado.getId() == null) {
            return true;
        }
        try {
            VentaDetalle existente = ventaDetalleDAO.finById(this.detalleSeleccionado.getId());
            return existente == null;
        } catch (Exception e) {
            return true;
        }
    }

    private void cargarProductosDisponibles() {
        try {
            if (productoDAO == null) {
                throw new IllegalStateException("ProductoDAO no fue inyectado.");
            }
            this.productosDisponibles = productoDAO.findRange(0, 1000);
        } catch (Exception e) {
            mostrarError("mensaje.cargar.error", e);
            this.productosDisponibles = new ArrayList<>();
        }
    }

    /**
     * Prepara un nuevo detalle para agregar a la venta
     */
    public void prepararNuevoDetalle() {
        this.detalleSeleccionado = new VentaDetalle();
        this.detalleSeleccionado.setId(UUID.randomUUID());
        this.detalleSeleccionado.setIdVenta(this.filaSeleccionada);
        this.detalleSeleccionado.setCantidad(BigDecimal.ONE);
        this.detalleSeleccionado.setPrecio(BigDecimal.ZERO);
        this.detalleSeleccionado.setEstado("PENDIENTE");
    }

    /**
     * Marca la venta como DESPACHADA y notifica via JMS/WebSocket
     * La venta desaparece de Venta.xhtml y aparece en DespachoVenta.xhtml
     */
    public void notificarDespacho() {
        try {
            if (this.filaSeleccionada != null && this.filaSeleccionada.getId() != null) {
                this.filaSeleccionada.setEstado(EstadoVenta.DESPACHADA.name());
                actualizarEntidad(filaSeleccionada);
                MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.actualizar.exito");

                // Enviar notificación asíncrona via JMS
                notificadorKardex.notificarCambioKardex("Venta despachada ID: " + this.filaSeleccionada.getId());

                filaSeleccionada = instanciarEntidad();
                estado = CRUD.NINGUNO;
            }
        } catch (Exception e) {
            mostrarError("mensaje.actualizar.error", e);
        }
    }

    public VentaDAO getVentaDAO() {
        return ventaDAO;
    }

    public void setVentaDAO(VentaDAO ventaDAO) {
        this.ventaDAO = ventaDAO;
    }

    public ClienteDAO getClienteDAO() {
        return clienteDAO;
    }

    public void setClienteDAO(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public VentaDetalleDAO getVentaDetalleDAO() {
        return ventaDetalleDAO;
    }

    public void setVentaDetalleDAO(VentaDetalleDAO ventaDetalleDAO) {
        this.ventaDetalleDAO = ventaDetalleDAO;
    }

    public ProductoDAO getProductoDAO() {
        return productoDAO;
    }

    public void setProductoDAO(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    public List<VentaDetalle> getDetallesDeLaVenta() {
        return detallesDeLaVenta;
    }

    public void setDetallesDeLaVenta(List<VentaDetalle> detallesDeLaVenta) {
        this.detallesDeLaVenta = detallesDeLaVenta;
    }

    public VentaDetalle getDetalleSeleccionado() {
        if (detalleSeleccionado == null) {
            detalleSeleccionado = new VentaDetalle();
        }
        return detalleSeleccionado;
    }

    public void setDetalleSeleccionado(VentaDetalle detalleSeleccionado) {
        this.detalleSeleccionado = detalleSeleccionado;
    }

    public List<Producto> getProductosDisponibles() {
        return productosDisponibles;
    }

    public void setProductosDisponibles(List<Producto> productosDisponibles) {
        this.productosDisponibles = productosDisponibles;
    }

    /**
     * Propiedad transient para p:calendar (que solo acepta LocalDateTime)
     * Convierte desde/hacia OffsetDateTime de la entidad
     */
    public LocalDateTime getFechaLocal() {
        if (this.filaSeleccionada == null || this.filaSeleccionada.getFecha() == null) {
            return null;
        }
        return this.filaSeleccionada.getFecha()
                .atZoneSameInstant(ZoneId.of("America/El_Salvador"))
                .toLocalDateTime();
    }

    public void setFechaLocal(LocalDateTime fechaLocal) {
        if (this.filaSeleccionada != null) {
            if (fechaLocal == null) {
                this.filaSeleccionada.setFecha(null);
            } else {
                OffsetDateTime offsetDateTime = fechaLocal
                        .atZone(ZoneId.of("America/El_Salvador"))
                        .toOffsetDateTime();
                this.filaSeleccionada.setFecha(offsetDateTime);
            }
        }
    }

    /**
     * Obtiene los estados disponibles para el dropdown de venta
     * @return Lista de SelectItem con los estados de venta
     */
    public List<SelectItem> getEstadosDisponibles() {
        List<SelectItem> estados = new ArrayList<>();
        for (EstadoVenta estado : EstadoVenta.values()) {
            estados.add(new SelectItem(estado.name(), estado.getDescripcion()));
        }
        return estados;
    }

    /**
     * Obtiene los estados disponibles para el dropdown de detalles
     * @return Lista de SelectItem con los estados del detalle
     */
    public List<SelectItem> getEstadosDisponiblesDetalle() {
        List<SelectItem> estados = new ArrayList<>();
        for (EstadoVentaDetalle estado : EstadoVentaDetalle.values()) {
            estados.add(new SelectItem(estado.name(), estado.getDescripcion()));
        }
        return estados;
    }
}
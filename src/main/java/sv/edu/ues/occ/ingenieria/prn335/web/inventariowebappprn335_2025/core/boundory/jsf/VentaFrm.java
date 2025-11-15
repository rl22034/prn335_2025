package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ClienteDAO;
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

    @Override
    protected Venta instanciarEntidad() {
        Venta nueva = new Venta();
        nueva.setId(UUID.randomUUID());
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
}
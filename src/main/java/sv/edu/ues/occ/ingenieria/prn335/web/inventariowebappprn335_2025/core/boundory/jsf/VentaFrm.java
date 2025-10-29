package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

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
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Named("ventaBean")
@ViewScoped
public class VentaFrm extends DefaultFrm<Venta> implements Serializable {

    // --- Inyecciones DAO Maestro ---
    @Inject
    private VentaDAO ventaDAO;

    @Inject
    private ClienteDAO clienteDAO;

    // --- Inyecciones DAO Detalle ---
    @Inject
    private VentaDetalleDAO ventaDetalleDAO;

    @Inject
    private ProductoDAO productoDAO;

    // --- Propiedades para el Detalle ---
// En VentaFrm.java
// ...
    // --- Propiedades para el Detalle ---
    private List<VentaDetalle> detallesDeLaVenta = new ArrayList<>();
    private VentaDetalle detalleSeleccionado;
    private List<Producto> productosDisponibles = new ArrayList<>();
// ...

    // ========================================================== //
    //    MÉTODOS ABSTRACTOS (LÓGICA MAESTRO: VENTA)
    // ========================================================== //

    @Override
    protected void crearEntidad(Venta entidad) throws Exception {
        if (entidad.getFecha() == null || entidad.getIdCliente() == null || entidad.getEstado() == null) {
            throw new Exception("Los campos fecha, cliente y estado son obligatorios");
        }
        // El ID (UUID) se generó en instanciarEntidad()
        ventaDAO.crear(entidad);
    }

    @Override
    protected void actualizarEntidad(Venta entidad) throws Exception {
        if (entidad.getFecha() == null || entidad.getIdCliente() == null || entidad.getEstado() == null) {
            throw new Exception("Los campos fecha, cliente y estado son obligatorios");
        }
        ventaDAO.update(entidad);
    }

    @Override
    protected void eliminarEntidad(Venta entidad) throws Exception {
        // Verificamos que no tenga detalles antes de borrar
        List<VentaDetalle> detalles = ventaDetalleDAO.getDetallesPorVenta(entidad.getId());
        if (detalles != null && !detalles.isEmpty()) {
            throw new Exception("No se puede eliminar la venta, tiene productos asociados.");
        }
        ventaDAO.delete(entidad);
    }

    @Override
    protected List<Venta> buscarEntidades(int first, int pageSize) throws Exception {
        // Comprobamos que el DAO no sea nulo
        if (ventaDAO == null) {
            throw new IllegalStateException("VentaDAO no fue inyectado.");
        }
        return ventaDAO.findRange(first, pageSize);
    }

    @Override
    protected Long contarEntidades() throws Exception {
        // Comprobamos que el DAO no sea nulo
        if (ventaDAO == null) {
            throw new IllegalStateException("VentaDAO no fue inyectado.");
        }
        return ventaDAO.count();
    }

    @Override
    protected Object obtenerIdEntidad(Venta entidad) {
        return entidad.getId();
    }

    @Override
    protected Venta instanciarEntidad() {
        Venta nueva = new Venta();
        // Generamos el UUID aquí porque no es autoincremental
        nueva.setId(UUID.randomUUID());
        nueva.setFecha(OffsetDateTime.now(ZoneId.of("America/El_Salvador")));
        nueva.setEstado("PENDIENTE");
        return nueva;
    }

    // ========================================================== //
    //    MÉTODOS DEL BEAN (LÓGICA MAESTRO: VENTA)
    // ========================================================== //

    /**
     * Sobrescribe el btnNuevo de DefaultFrm.
     * Limpia los detalles al crear una nueva venta.
     */
    @Override
    public void btnNuevo() {
        super.btnNuevo(); // Esto llama a instanciarEntidad()
        // Limpiamos la lista de detalles para la nueva venta
        this.detallesDeLaVenta = new ArrayList<>();
        this.detalleSeleccionado = null;
    }

    /**
     * Obtiene los clientes para el dropdown.
     */
    public List<Cliente> getClientesActivos() {
        try {
            if (clienteDAO == null) {
                throw new IllegalStateException("ClienteDAO no fue inyectado.");
            }
            // Sería ideal tener un findActivos(), pero findRange funciona
            return clienteDAO.findRange(0, 1000);
        } catch (Exception e) {
            MessageHelper.addErrorMessage("mensaje.titulo.error", "Error al cargar clientes");
            return new ArrayList<>();
        }
    }


    public boolean isVentaNueva() {
        // Usamos el estado gestionado por la clase padre DefaultFrm
        return this.getEstado() == CRUD.CREAR;
    }

    // ========================================================== //
    //    MÉTODOS DEL BEAN (LÓGICA DETALLE: VENTA_DETALLE)
    // ========================================================== //

    /**
     * Carga los detalles (VentaDetalle) de la venta que se está viendo.
     * Se llama desde el <p:ajax event="tabChange">
     */
    public void cargarDetallesVenta() {
        if (this.filaSeleccionada != null && this.filaSeleccionada.getId() != null) {
            try {
                if (ventaDetalleDAO == null) {
                    throw new IllegalStateException("VentaDetalleDAO no fue inyectado.");
                }
                this.detallesDeLaVenta = ventaDetalleDAO.getDetallesPorVenta(this.filaSeleccionada.getId());
            } catch (Exception e) {
                MessageHelper.addErrorMessage("mensaje.titulo.error", "Error al cargar detalles de venta");
                this.detallesDeLaVenta = new ArrayList<>();
            }
        } else {
            this.detallesDeLaVenta = new ArrayList<>();
        }
    }

    /**
     * Prepara un nuevo objeto VentaDetalle para el diálogo "Añadir Producto".
     */
    public void prepararNuevoDetalle() {
        this.detalleSeleccionado = new VentaDetalle();

        // Generamos el UUID para el detalle
        this.detalleSeleccionado.setId(UUID.randomUUID());

        // ¡Vínculo Maestro-Detalle!
        this.detalleSeleccionado.setIdVenta(this.filaSeleccionada);

        // Valores por defecto
        this.detalleSeleccionado.setCantidad(BigDecimal.ONE);
        this.detalleSeleccionado.setPrecio(BigDecimal.ZERO);

        // Cargar productos para el dropdown
        try {
            if (productoDAO == null) {
                throw new IllegalStateException("ProductoDAO no fue inyectado.");
            }
            this.productosDisponibles = productoDAO.findRange(0, 1000);
        } catch (Exception e) {
            MessageHelper.addErrorMessage("mensaje.titulo.error", "Error al cargar productos");
            this.productosDisponibles = new ArrayList<>();
        }
    }

    /**
     * Guarda el nuevo VentaDetalle en la BBDD.
     */
    public void guardarDetalle() {
        try {
            // Validaciones
            if (this.detalleSeleccionado.getIdProducto() == null) {
                MessageHelper.addErrorMessage("mensaje.titulo.error", "Debe seleccionar un producto");
                return;
            }
            if (this.detalleSeleccionado.getCantidad() == null || this.detalleSeleccionado.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                MessageHelper.addErrorMessage("mensaje.titulo.error", "La cantidad debe ser mayor a cero");
                return;
            }

            if (ventaDetalleDAO == null) {
                throw new IllegalStateException("VentaDetalleDAO no fue inyectado.");
            }

            // Crear el registro
            ventaDetalleDAO.crear(this.detalleSeleccionado);

            // Refrescar la tabla de detalles en la vista
            cargarDetallesVenta();

            MessageHelper.addInfoMessage("mensaje.titulo.exito", "Producto añadido a la venta");

            // Ocultar el diálogo (asumiendo widgetVar="dlgVentaDetalle")
            PrimeFaces.current().executeScript("PF('dlgVentaDetalle').hide()");

        } catch (Exception e) {
            MessageHelper.addErrorMessage("mensaje.titulo.error", "Error al guardar detalle: " + e.getMessage());
        }
    }

    /**
     * Elimina un producto (VentaDetalle) de la venta.
     */
    public void eliminarDetalle(VentaDetalle detalle) {
        try {
            if (ventaDetalleDAO == null) {
                throw new IllegalStateException("VentaDetalleDAO no fue inyectado.");
            }
            ventaDetalleDAO.delete(detalle);
            cargarDetallesVenta(); // Refrescar la tabla
            MessageHelper.addInfoMessage("mensaje.titulo.exito", "Producto eliminado de la venta");
        } catch (Exception e) {
            MessageHelper.addErrorMessage("mensaje.titulo.error", "Error al eliminar detalle: " + e.getMessage());
        }
    }


    // ========================================================== //
    //    GETTERS Y SETTERS (GENERADOS)
    // ========================================================== //

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
        // Asegurarse de que nunca sea nulo para el formulario
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
}
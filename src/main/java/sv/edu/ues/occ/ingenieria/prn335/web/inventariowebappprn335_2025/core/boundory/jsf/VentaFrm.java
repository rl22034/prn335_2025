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
    protected VentaDAO obtenerDAO() {
        return ventaDAO;
    }

    @Override
    protected void validarAntesDeCrear(Venta entidad) throws Exception {
        if (entidad.getFecha() == null || entidad.getIdCliente() == null || entidad.getEstado() == null) {
            throw new Exception("Los campos fecha, cliente y estado son obligatorios");
        }
    }

    @Override
    protected void validarAntesDeActualizar(Venta entidad) throws Exception {
        if (entidad.getFecha() == null || entidad.getIdCliente() == null || entidad.getEstado() == null) {
            throw new Exception("Los campos fecha, cliente y estado son obligatorios");
        }
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
    public void prepararEditarDetalle(VentaDetalle detalle) {
        // 1. Asigna el detalle existente al objeto del formulario
        this.detalleSeleccionado = detalle;

        // 2. Carga la lista de productos para el dropdown
        cargarProductosDisponibles();
    }
    /**
     * Guarda el nuevo VentaDetalle en la BBDD.
     */
    public void guardarDetalle() {
        try {
            // ... (tus validaciones de producto y cantidad) ...

            if (ventaDetalleDAO == null) {
                throw new IllegalStateException("VentaDetalleDAO no fue inyectado.");
            }

            // --- LÓGICA "INTELIGENTE" ---
            // El ID de VentaDetalle se genera en prepararNuevoDetalle (UUID.randomUUID())
            // Para saber si es nuevo, verificamos si ya existe en la BD
            // Nota: Es mejor usar el estado CREAR/MODIFICAR si lo tienes en el bean de detalle.
            // Una forma más simple es chequear si el ID ya existe en la lista 'detallesDeLaVenta'.
            // Pero la forma más robusta es verificar la BD.

            // Vamos a usar una lógica más simple basada en si el DAO lo encuentra
            VentaDetalle existente = ventaDetalleDAO.finById(this.detalleSeleccionado.getId());

            if (existente == null) {
                // 1. CREAR: Si no existe, es un registro nuevo
                ventaDetalleDAO.crear(this.detalleSeleccionado);
                MessageHelper.addInfoMessage("mensaje.titulo.exito", "Producto añadido");
            } else {
                // 2. ACTUALIZAR: Si ya existe, es una edición
                ventaDetalleDAO.update(this.detalleSeleccionado);
                MessageHelper.addInfoMessage("mensaje.titulo.exito", "Producto actualizado");
            }
            // --- FIN DE LA LÓGICA ---

            // Refrescar la tabla y cerrar el diálogo
            cargarDetallesVenta();
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

    private void cargarProductosDisponibles() {
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
    public void prepararNuevoDetalle() {
        this.detalleSeleccionado = new VentaDetalle();
        this.detalleSeleccionado.setId(UUID.randomUUID());
        this.detalleSeleccionado.setIdVenta(this.filaSeleccionada);
        this.detalleSeleccionado.setCantidad(BigDecimal.ONE);
        this.detalleSeleccionado.setPrecio(BigDecimal.ZERO);

        // Llamamos al método centralizado
        cargarProductosDisponibles();
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
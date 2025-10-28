package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CompraDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CompraDetalleDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProveedorDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Compra;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.CompraDetalle;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Producto;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Proveedor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Named("compraBean")
@ViewScoped
public class CompraFrm extends DefaultFrm<Compra> implements Serializable {

    @Inject
    private CompraDAO compraDAO;
    // --- AÑADIR ESTAS INYECCIONES ---
    @Inject
    private CompraDetalleDAO compraDetalleDAO;

    @Inject
    private ProductoDAO productoDAO;

    // --- AÑADIR ESTAS PROPIEDADES ---
    private List<CompraDetalle> detallesDeLaCompra;
    private CompraDetalle detalleSeleccionado;
    private List<Producto> productosDisponibles;
    @Inject
    private ProveedorDAO proveedorDAO;


    @Override // Sobreescribe este método de DefaultFrm
    public void btnNuevo() {
        super.btnNuevo(); // Llama al método original para instanciar la entidad
        // Al crear una nueva compra, sus detalles deben estar vacíos
        this.detallesDeLaCompra = new ArrayList<>();
        this.detalleSeleccionado = null; // No hay detalle seleccionado inicialmente
    }

    @Override
    protected void crearEntidad(Compra entidad) throws Exception {
        // Validaciones básicas
        if (entidad.getFecha() == null || entidad.getProveedor() == null || entidad.getEstado() == null) {
            throw new Exception("Los campos fecha, proveedor y estado son obligatorios");
        }

        // CRÍTICO: Por el DDL, el id_compra DEBE ser igual al id_proveedor
        if (entidad.getProveedor() != null && entidad.getProveedor().getId() != null) {
            Long idProveedor = entidad.getProveedor().getId().longValue();
            entidad.setId(idProveedor);
        } else {
            throw new Exception("Debe seleccionar un proveedor válido");
        }

        // Verificar que no exista ya una compra para ese proveedor
        Compra existente = compraDAO.finById(entidad.getId());
        if (existente != null) {
            throw new Exception("Ya existe una compra para el proveedor seleccionado. Cada proveedor solo puede tener una compra.");
        }

        compraDAO.crear(entidad);
    }

    @Override
    protected void actualizarEntidad(Compra entidad) throws Exception {
        if (entidad.getFecha() == null || entidad.getProveedor() == null || entidad.getEstado() == null) {
            throw new Exception("Los campos fecha, proveedor y estado son obligatorios");
        }

        // No se puede cambiar el proveedor porque cambiaría el ID
        Compra original = compraDAO.finById(entidad.getId());
        if (original == null) {
            throw new Exception("La compra no existe");
        }

        if (!original.getProveedor().getId().equals(entidad.getProveedor().getId())) {
            throw new Exception("No se puede cambiar el proveedor de una compra existente");
        }

        compraDAO.update(entidad);
    }

    @Override
    protected void eliminarEntidad(Compra entidad) throws Exception {
        Compra original = compraDAO.finById(entidad.getId());
        if (original == null) {
            throw new Exception("La compra no existe en la base de datos");
        }

        compraDAO.delete(entidad);
    }

    @Override
    protected List<Compra> buscarEntidades(int first, int pageSize) throws Exception {
        return compraDAO.findRange(first, pageSize);
    }

    @Override
    protected Long contarEntidades() throws Exception {
        return compraDAO.count();
    }

    @Override
    protected Object obtenerIdEntidad(Compra entidad) {
        return entidad.getId();
    }

    public CompraDetalleDAO getCompraDetalleDAO() {
        return compraDetalleDAO;
    }

    public ProductoDAO getProductoDAO() {
        return productoDAO;
    }

    public void setProductoDAO(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    public void setCompraDetalleDAO(CompraDetalleDAO compraDetalleDAO) {
        this.compraDetalleDAO = compraDetalleDAO;
    }

    public boolean isCompraNueva() {
        return this.filaSeleccionada != null && this.filaSeleccionada.getId() == null;
    }

    @Override
    protected Compra instanciarEntidad() {
        Compra nuevo = new Compra();

        // NO asignamos ID aquí - se asignará cuando se seleccione el proveedor
        // Valores por defecto
        nuevo.setFecha(OffsetDateTime.now(ZoneId.of("America/El_Salvador")));
        nuevo.setEstado("PENDIENTE");

        return nuevo;
    }


    public void cargarDetallesCompra() {
        if (this.filaSeleccionada != null && this.filaSeleccionada.getId() != null) {
            // Usamos el método corregido del DAO
            this.detallesDeLaCompra = compraDetalleDAO.getDetallesPorCompra(this.filaSeleccionada.getId());
        } else {
            this.detallesDeLaCompra = new ArrayList<>();
        }
    }

    /**
     * Prepara un nuevo objeto CompraDetalle para el diálogo.
     * Se llama desde el botón "Nuevo" en la pestaña de detalle.
     */
    public void prepararNuevoDetalle() {
        this.detalleSeleccionado = new CompraDetalle();

        // Generamos el UUID ya que no es autoincremental [cite: 3]
        this.detalleSeleccionado.setId(UUID.randomUUID());

        // ¡Vínculo Maestro-Detalle!
        this.detalleSeleccionado.setIdCompra(this.filaSeleccionada);

        // Seteamos valores por defecto si lo deseas
        this.detalleSeleccionado.setCantidad(BigDecimal.ONE);
        this.detalleSeleccionado.setPrecio(BigDecimal.ZERO);

        // Cargamos la lista de productos para el dropdown
        try {
            // Asumiendo que quieres mostrar todos los productos.
            // Sería mejor un productoDAO.findActivos()
            this.productosDisponibles = productoDAO.findRange(0, 1000);
        } catch (Exception e) {
            mostrarError("mensaje.cargar.error", e);
            this.productosDisponibles = new ArrayList<>();
        }
    }

    /**
     * Guarda el nuevo CompraDetalle en la BBDD.
     */
    public void guardarDetalle() {
        try {
            // Validaciones
            if (this.detalleSeleccionado.getIdProducto() == null) {
                MessageHelper.addErrorMessage("mensaje.titulo.error", "Debe seleccionar un producto");
                return;
            }
            if (this.detalleSeleccionado.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                MessageHelper.addErrorMessage("mensaje.titulo.error", "La cantidad debe ser mayor a cero");
                return;
            }

            // Crear el registro
            compraDetalleDAO.crear(this.detalleSeleccionado);

            // Refrescar la tabla de detalles
            cargarDetallesCompra();

            MessageHelper.addInfoMessage("mensaje.titulo.exito", "Producto añadido a la compra");

            // Ocultar el diálogo
            PrimeFaces.current().executeScript("PF('dlgCompraDetalle').hide()");

        } catch (Exception e) {
            mostrarError("mensaje.crear.error", e);
        }
    }

    /**
     * Elimina un detalle de la compra.
     * (Puedes añadir un p:commandButton en la tabla para llamar a este método)
     */
    public void eliminarDetalle(CompraDetalle detalle) {
        try {
            compraDetalleDAO.delete(detalle);
            cargarDetallesCompra(); // Refrescar la tabla
            MessageHelper.addInfoMessage("mensaje.titulo.exito", "Producto eliminado de la compra");
        } catch (Exception e) {
            mostrarError("mensaje.eliminar.error", e);
        }
    }


    // --- AÑADIR GETTERS Y SETTERS PARA LAS NUEVAS PROPIEDADES ---

    public List<CompraDetalle> getDetallesDeLaCompra() {
        return detallesDeLaCompra;
    }

    public void setDetallesDeLaCompra(List<CompraDetalle> detallesDeLaCompra) {
        this.detallesDeLaCompra = detallesDeLaCompra;
    }

    public CompraDetalle getDetalleSeleccionado() {
        return detalleSeleccionado;
    }

    public void setDetalleSeleccionado(CompraDetalle detalleSeleccionado) {
        this.detalleSeleccionado = detalleSeleccionado;
    }

    public List<Producto> getProductosDisponibles() {
        return productosDisponibles;
    }

    public void setProductosDisponibles(List<Producto> productosDisponibles) {
        this.productosDisponibles = productosDisponibles;
    }
    /**
     * Obtiene solo los proveedores activos
     */
    public List<Proveedor> getProveedoresActivos() {
        try {
            return proveedorDAO.findProveedoresActivos();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public ProveedorDAO getProveedorDAO() {
        return proveedorDAO;
    }

    public void setProveedorDAO(ProveedorDAO proveedorDAO) {
        this.proveedorDAO = proveedorDAO;
    }

    public Compra getFilaSeleccionada() {
        return super.getFilaSeleccionada();
    }

    public void setFilaSeleccionada(Compra filaSeleccionada) {
        super.setFilaSeleccionada(filaSeleccionada);
    }

    public List<Compra> getEntidadesList() {
        return super.getEntidadesList();
    }

    public void setEntidadesList(List<Compra> entidadesList) {
        super.setEntidadesList(entidadesList);
    }

    public CompraDAO getCompraDAO() {
        return compraDAO;
    }

    public void setCompraDAO(CompraDAO compraDAO) {
        this.compraDAO = compraDAO;
    }
}
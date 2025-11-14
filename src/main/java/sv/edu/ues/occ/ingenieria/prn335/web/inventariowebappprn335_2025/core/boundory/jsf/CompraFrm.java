package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.annotation.PostConstruct;
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

    @Inject
    private CompraDetalleDAO compraDetalleDAO;

    @Inject
    private ProductoDAO productoDAO;

    @Inject
    private ProveedorDAO proveedorDAO;

    // Propiedades para maestro-detalle
    private List<CompraDetalle> detallesDeLaCompra;
    private CompraDetalle detalleSeleccionado;

    // Cachés para mejorar rendimiento (evitan consultas repetidas a BD)
    private List<Proveedor> proveedoresActivos;
    private List<Producto> productosDisponibles;

    /**
     * Inicializa los cachés al crear el bean.
     * Se ejecuta una sola vez cuando se crea el bean ViewScoped.
     * IMPORTANTE: Debe llamar a super.init() para inicializar estado y filaSeleccionada.
     */
    @PostConstruct
    public void init() {
        super.init();  // ← CRÍTICO: Inicializa estado = NINGUNO y filaSeleccionada
        cargarProveedoresActivos();
    }

    /**
     * Carga los proveedores activos desde la BD y los guarda en caché.
     * Este método solo se ejecuta una vez al inicializar el bean.
     */
    private void cargarProveedoresActivos() {
        try {
            this.proveedoresActivos = proveedorDAO.findProveedoresActivos();
        } catch (Exception e) {
            this.proveedoresActivos = new ArrayList<>();
        }
    }

    @Override
    protected CompraDAO obtenerDAO() {
        return compraDAO;
    }

    @Override
    protected void validarAntesDeCrear(Compra entidad) throws Exception {
        // Validaciones básicas
        if (entidad.getFecha() == null || entidad.getProveedor() == null || entidad.getEstado() == null) {
            throw new Exception("validacion.compra.campos.requeridos");
        }

        // Validar que el proveedor sea válido
        if (entidad.getProveedor().getId() == null) {
            throw new Exception("validacion.compra.proveedor.invalido");
        }

        // CRÍTICO: Por el DDL, el id_compra DEBE ser igual al id_proveedor
        Long idProveedor = entidad.getProveedor().getId().longValue();
        entidad.setId(idProveedor);

        // Verificar que no exista ya una compra para ese proveedor
        Compra existente = compraDAO.finById(entidad.getId());
        if (existente != null) {
            throw new Exception("validacion.compra.existe");
        }
    }

    @Override
    protected void validarAntesDeActualizar(Compra entidad) throws Exception {
        // Validaciones básicas de campos requeridos
        if (entidad.getFecha() == null || entidad.getProveedor() == null || entidad.getEstado() == null) {
            throw new Exception("validacion.compra.campos.requeridos");
        }

        // NOTA: Ya NO necesitamos verificar si existe - DefaultFrm lo hace automáticamente
        // Obtenemos el original desde DefaultFrm que ya lo buscó
        Compra original = compraDAO.finById(entidad.getId());

        // No se puede cambiar el proveedor porque cambiaría el ID
        if (!original.getProveedor().getId().equals(entidad.getProveedor().getId())) {
            throw new Exception("validacion.compra.proveedor.nomodificable");
        }
    }

    @Override
    public void btnNuevo() {
        super.btnNuevo();
        // Al crear una nueva compra, sus detalles deben estar vacíos
        this.detallesDeLaCompra = new ArrayList<>();
        this.detalleSeleccionado = null;
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

        // Generamos el UUID ya que no es autoincremental
        this.detalleSeleccionado.setId(UUID.randomUUID());

        // Vínculo Maestro-Detalle
        this.detalleSeleccionado.setIdCompra(this.filaSeleccionada);

        // Valores por defecto
        this.detalleSeleccionado.setCantidad(BigDecimal.ONE);
        this.detalleSeleccionado.setPrecio(BigDecimal.ZERO);

        // Lazy loading: solo carga los productos si aún no están en caché
        cargarProductosDisponibles();
    }

    /**
     * Carga los productos disponibles desde la BD usando lazy loading.
     * Solo carga si el caché está vacío o es null.
     */
    private void cargarProductosDisponibles() {
        if (this.productosDisponibles == null || this.productosDisponibles.isEmpty()) {
            try {
                this.productosDisponibles = productoDAO.findRange(0, 1000);
            } catch (Exception e) {
                mostrarError("mensaje.cargar.error", e);
                this.productosDisponibles = new ArrayList<>();
            }
        }
    }

    /**
     * Guarda el nuevo CompraDetalle en la BBDD.
     * Maneja tanto CREAR como ACTUALIZAR según el patrón de TipoUnidadMedidaFrm.
     */
    public void guardarDetalle() {
        try {
            // Validaciones
            if (this.detalleSeleccionado.getIdProducto() == null) {
                MessageHelper.addErrorMessage("mensaje.titulo.error", "validacion.producto.requerido");
                return;
            }
            if (this.detalleSeleccionado.getCantidad() == null ||
                this.detalleSeleccionado.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                MessageHelper.addErrorMessage("mensaje.titulo.error", "validacion.cantidad.mayor.cero");
                return;
            }
            if (this.detalleSeleccionado.getPrecio() == null ||
                this.detalleSeleccionado.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
                MessageHelper.addErrorMessage("mensaje.titulo.error", "validacion.precio.invalido");
                return;
            }

            // Crear o actualizar según el patrón de TipoUnidadMedidaFrm
            if (this.detalleSeleccionado.getId() == null) {
                // CREAR: Si el ID es nulo, es un registro nuevo
                this.detalleSeleccionado.setId(UUID.randomUUID());
                compraDetalleDAO.crear(this.detalleSeleccionado);
                MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.crear.exito");
            } else {
                // ACTUALIZAR: Si el ID ya existe, es una edición
                compraDetalleDAO.update(this.detalleSeleccionado);
                MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.actualizar.exito");
            }

            // Refrescar la tabla de detalles
            cargarDetallesCompra();

            // Ocultar el diálogo
            PrimeFaces.current().executeScript("PF('dlgCompraDetalle').hide()");

        } catch (Exception e) {
            mostrarError("mensaje.crear.error", e);
        }
    }

    /**
     * Elimina un detalle de la compra.
     * Sigue el patrón de TipoUnidadMedidaFrm.
     */
    public void eliminarDetalle(CompraDetalle detalle) {
        try {
            compraDetalleDAO.delete(detalle);
            cargarDetallesCompra(); // Refrescar la tabla
            MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.eliminar.exito");
        } catch (Exception e) {
            mostrarError("mensaje.eliminar.error", e);
        }
    }

    /**
     * Prepara para editar un detalle existente.
     * Sigue el patrón de TipoUnidadMedidaFrm.
     */
    public void prepararEditarDetalle(CompraDetalle detalle) {
        this.detalleSeleccionado = detalle;
        // El caché de productos ya está cargado
    }

    // Getters y Setters

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
     * Obtiene los proveedores activos desde el caché.
     * La lista se carga una sola vez al inicializar el bean (@PostConstruct).
     */
    public List<Proveedor> getProveedoresActivos() {
        return proveedoresActivos;
    }

    /**
     * Verifica si estamos creando una compra nueva.
     * Usa el estado del formulario (CREAR) en lugar de verificar el ID.
     * Esto es más confiable porque el ID se asigna antes de guardar (id = id_proveedor).
     */
    public boolean isCompraNueva() {
        return this.getEstado() == CRUD.CREAR;
    }
}
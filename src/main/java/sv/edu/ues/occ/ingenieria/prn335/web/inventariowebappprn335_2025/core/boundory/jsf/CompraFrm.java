package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.AlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CompraDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CompraDetalleDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.KardexDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.NotificadorKardex;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProveedorDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Almacen;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Compra;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.CompraDetalle;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Producto;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Proveedor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Inject
    private NotificadorKardex notificadorKardex;

    @Inject
    private KardexDAO kardexDAO;

    @Inject
    private AlmacenDAO almacenDAO;

    private List<CompraDetalle> detallesDeLaCompra;
    private CompraDetalle detalleSeleccionado;
    private List<Proveedor> proveedoresActivos;
    private List<Producto> productosDisponibles;

    @PostConstruct
    public void init() {
        super.init();
        cargarProveedoresActivos();
        cargarProductosDisponibles();
    }

    private void cargarProveedoresActivos() {
        try {
            this.proveedoresActivos = proveedorDAO.findProveedoresActivos();
        } catch (Exception e) {
            this.proveedoresActivos = new ArrayList<>();
        }
    }

    private void cargarProductosDisponibles() {
        try {
            this.productosDisponibles = productoDAO.findRange(0, 1000);
        } catch (Exception e) {
            this.productosDisponibles = new ArrayList<>();
        }
    }

    @Override
    protected CompraDAO obtenerDAO() {
        return compraDAO;
    }

    @Override
    protected void validarAntesDeCrear(Compra entidad) throws Exception {
        if (entidad.getFecha() == null || entidad.getProveedor() == null || entidad.getEstado() == null) {
            throw new Exception("validacion.compra.campos.requeridos");
        }

        if (entidad.getProveedor().getId() == null) {
            throw new Exception("validacion.compra.proveedor.invalido");
        }

        Long idProveedor = entidad.getProveedor().getId().longValue();
        entidad.setId(idProveedor);

        Compra existente = compraDAO.finById(entidad.getId());
        if (existente != null) {
            throw new Exception("validacion.compra.existe");
        }
    }

    @Override
    protected void validarAntesDeActualizar(Compra entidad) throws Exception {
        if (entidad.getFecha() == null || entidad.getProveedor() == null || entidad.getEstado() == null) {
            throw new Exception("validacion.compra.campos.requeridos");
        }

        Compra original = compraDAO.finById(entidad.getId());

        if (!original.getProveedor().getId().equals(entidad.getProveedor().getId())) {
            throw new Exception("validacion.compra.proveedor.nomodificable");
        }
    }

    @Override
    public void btnNuevo() {
        super.btnNuevo();
        this.detallesDeLaCompra = new ArrayList<>();
        this.detalleSeleccionado = null;
    }

    /**
     * Carga los detalles cuando se selecciona una compra de la tabla
     */
    @Override
    public void onRowSelect(SelectEvent<Compra> event) {
        super.onRowSelect(event);
        cargarDetallesCompra();
    }

    @Override
    protected Compra instanciarEntidad() {
        Compra nuevo = new Compra();
        nuevo.setFecha(OffsetDateTime.now(ZoneId.of("America/El_Salvador")));
        nuevo.setEstado("PENDIENTE");
        return nuevo;
    }

    /**
     * Buscar compras excluyendo las PAGADAS (para que no aparezcan en esta tabla)
     * Las compras PAGADAS solo se ven en RecepcionKardex.xhtml
     */
    @Override
    protected List<Compra> buscarEntidades(int first, int pageSize) throws Exception {
        return compraDAO.findExcluyendoEstado("PAGADA", first, pageSize);
    }

    /**
     * Contar compras excluyendo las PAGADAS
     */
    @Override
    protected Long contarEntidades() throws Exception {
        return compraDAO.countExcluyendoEstado("PAGADA");
    }
    public void cargarDetallesCompra() {
        if (this.filaSeleccionada != null && this.filaSeleccionada.getId() != null) {
            this.detallesDeLaCompra = compraDetalleDAO.getDetallesPorCompra(this.filaSeleccionada.getId());
        } else {
            this.detallesDeLaCompra = new ArrayList<>();
        }
    }

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

    public List<Proveedor> getProveedoresActivos() {
        return proveedoresActivos;
    }

    /**
     * Verifica si la compra actual es nueva (se está creando)
     * @return true si el estado es CREAR, false en caso contrario
     */
    public boolean isCompraNueva() {
        return this.getEstado() == CRUD.CREAR;
    }

    /**
     * Obtiene los estados disponibles para el dropdown
     * @return Lista de SelectItem con los estados de compra
     */
    public List<SelectItem> getEstadosDisponibles() {
        List<SelectItem> estados = new ArrayList<>();
        for (EstadoCompra estado : EstadoCompra.values()) {
            estados.add(new SelectItem(estado.name(), estado.getDescripcion()));
        }
        return estados;
    }

    /**
     * Calcula el total de la compra actual
     * @return Total de la compra (suma de cantidad * precio de todos los detalles)
     */
    public BigDecimal getTotalCompra() {
        if (this.filaSeleccionada == null || this.filaSeleccionada.getId() == null) {
            return BigDecimal.ZERO;
        }
        return compraDetalleDAO.calcularTotalCompra(this.filaSeleccionada.getId());
    }

    /**
     * Prepara un nuevo detalle para agregar a la compra
     */
    public void prepararNuevoDetalle() {
        this.detalleSeleccionado = new CompraDetalle();
        this.detalleSeleccionado.setIdCompra(this.filaSeleccionada);
        this.detalleSeleccionado.setId(UUID.randomUUID());
        this.detalleSeleccionado.setCantidad(BigDecimal.ONE);
        this.detalleSeleccionado.setPrecio(BigDecimal.ZERO);
        this.detalleSeleccionado.setEstado("PENDIENTE");
    }

    /**
     * Prepara un detalle existente para editar
     */
    public void prepararEditarDetalle(CompraDetalle detalle) {
        this.detalleSeleccionado = detalle;
    }

    /**
     * Guarda el detalle (CREAR o ACTUALIZAR según si existe en BD)
     */
    public void guardarDetalle() {
        try {
            if (detalleSeleccionado.getIdCompra() == null) {
                throw new Exception("validacion.compra.requerida");
            }

            if (detalleSeleccionado.getIdProducto() == null) {
                throw new Exception("validacion.producto.requerido");
            }

            if (detalleSeleccionado.getCantidad() == null ||
                detalleSeleccionado.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                throw new Exception("validacion.cantidad.mayor.cero");
            }

            if (detalleSeleccionado.getPrecio() == null ||
                detalleSeleccionado.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
                throw new Exception("validacion.precio.invalido");
            }

            CompraDetalle existente = compraDetalleDAO.finById(this.detalleSeleccionado.getId());

            if (existente == null) {
                compraDetalleDAO.crear(this.detalleSeleccionado);
                MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.crear.exito");
            } else {
                compraDetalleDAO.update(this.detalleSeleccionado);
                MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.actualizar.exito");
            }

            cargarDetallesCompra();
            PrimeFaces.current().executeScript("PF('dlgCompraDetalle').hide()");

        } catch (Exception e) {
            mostrarError("mensaje.crear.error", e);
        }
    }

    /**
     * Elimina un detalle de la compra
     */
    public void eliminarDetalle(CompraDetalle detalle) {
        try {
            compraDetalleDAO.delete(detalle);
            cargarDetallesCompra();
            MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.eliminar.exito");
        } catch (Exception e) {
            mostrarError("mensaje.eliminar.error", e);
        }
    }

    /**
     * Obtiene los estados disponibles para el dropdown de detalles
     * @return Lista de SelectItem con los estados del detalle
     */
    public List<SelectItem> getEstadosDisponiblesDetalle() {
        List<SelectItem> estados = new ArrayList<>();
        for (EstadoCompraDetalle estado : EstadoCompraDetalle.values()) {
            estados.add(new SelectItem(estado.name(), estado.getDescripcion()));
        }
        return estados;
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
            CompraDetalle existente = compraDetalleDAO.finById(this.detalleSeleccionado.getId());
            return existente == null;
        } catch (Exception e) {
            return true;
        }
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
     * Marca la compra como PAGADA, crea registros Kardex y envía notificación JMS
     */
    public void notificarCambioKardex() {
        try {
            if (this.filaSeleccionada != null && this.filaSeleccionada.getId() != null) {
                // 1. Actualizar estado a PAGADA
                this.filaSeleccionada.setEstado(EstadoCompra.PAGADA.name());
                actualizarEntidad(filaSeleccionada);

                // 2. Obtener el primer almacén disponible (o null si no hay)
                List<Almacen> almacenes = almacenDAO.findRange(0, 1);
                Integer idAlmacen = almacenes.isEmpty() ? null : almacenes.get(0).getId();

                // 3. Crear registros Kardex para cada detalle de la compra
                List<CompraDetalle> detalles = compraDetalleDAO.getDetallesPorCompra(this.filaSeleccionada.getId());
                for (CompraDetalle detalle : detalles) {
                    UUID idProducto = detalle.getIdProducto().getId();
                    BigDecimal cantidadAnterior = kardexDAO.obtenerCantidadActual(idProducto);
                    BigDecimal cantidadNueva = cantidadAnterior.add(detalle.getCantidad());

                    kardexDAO.crearKardexEntrada(
                            UUID.randomUUID(),           // id_kardex
                            idProducto,                  // id_producto
                            detalle.getCantidad(),       // cantidad
                            detalle.getPrecio(),         // precio
                            cantidadNueva,               // cantidad_actual
                            detalle.getPrecio(),         // precio_actual
                            detalle.getId(),             // id_compra_detalle
                            "Compra #" + this.filaSeleccionada.getId(), // observaciones
                            idAlmacen                    // id_almacen
                    );
                }

                // 4. Notificar via JMS
                notificadorKardex.notificarCambioKardex("Compra #" + this.filaSeleccionada.getId() + " pagada");

                MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.actualizar.exito");
                filaSeleccionada = instanciarEntidad();
                estado = CRUD.NINGUNO;
            }
        } catch (Exception e) {
            mostrarError("mensaje.actualizar.error", e);
        }
    }
}
package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CompraDetalleDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CompraDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Compra;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.CompraDetalle;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Producto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Bean para gestionar CompraDetalle como entidad independiente.
 * Sigue el patrón de UnidadMedidaFrm.
 */
@Named("compraDetalleBean")
@ViewScoped
public class CompraDetalleFrm extends DefaultFrm<CompraDetalle> implements Serializable {

    @Inject
    private CompraDetalleDAO compraDetalleDAO;

    @Inject
    private ProductoDAO productoDAO;

    @Inject
    private CompraDAO compraDAO;

    // Caché de productos disponibles
    private List<Producto> productosDisponibles;

    // Propiedad para filtrar por compra (opcional)
    private Long idCompra;

    @PostConstruct
    public void init() {
        super.init();
        cargarProductosDisponibles();
    }

    @Override
    protected CompraDetalleDAO obtenerDAO() {
        return compraDetalleDAO;
    }

    @Override
    protected void validarAntesDeCrear(CompraDetalle entidad) throws Exception {
        // Validar compra
        if (entidad.getIdCompra() == null) {
            throw new Exception("validacion.compra.requerida");
        }

        // Validar producto
        if (entidad.getIdProducto() == null) {
            throw new Exception("validacion.producto.requerido");
        }

        // Validar cantidad
        if (entidad.getCantidad() == null || entidad.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("validacion.cantidad.mayor.cero");
        }

        // Validar precio
        if (entidad.getPrecio() == null || entidad.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("validacion.precio.invalido");
        }
    }

    @Override
    protected void validarAntesDeActualizar(CompraDetalle entidad) throws Exception {
        validarAntesDeCrear(entidad);
    }

    @Override
    protected List<CompraDetalle> buscarEntidades(int first, int pageSize) throws Exception {
        // Filtrar por compra si está seleccionada
        if (idCompra != null) {
            List<CompraDetalle> todos = compraDetalleDAO.getDetallesPorCompra(idCompra);
            int toIndex = Math.min(first + pageSize, todos.size());
            return todos.subList(first, toIndex);
        }
        return compraDetalleDAO.findRange(first, pageSize);
    }

    @Override
    protected Long contarEntidades() throws Exception {
        // Contar por compra si está seleccionada
        if (idCompra != null) {
            return (long) compraDetalleDAO.getDetallesPorCompra(idCompra).size();
        }
        return compraDetalleDAO.count();
    }

    @Override
    protected CompraDetalle instanciarEntidad() {
        CompraDetalle nuevo = new CompraDetalle();
        nuevo.setId(UUID.randomUUID());
        nuevo.setCantidad(BigDecimal.ONE);
        nuevo.setPrecio(BigDecimal.ZERO);
        nuevo.setEstado("PENDIENTE");

        // Si hay un filtro de compra, asignarla automáticamente
        if (idCompra != null) {
            try {
                Compra compra = compraDAO.finById(idCompra);
                if (compra != null) {
                    nuevo.setIdCompra(compra);
                }
            } catch (Exception e) {
                // Log error
            }
        }

        return nuevo;
    }

    /**
     * Carga los productos disponibles usando lazy loading con caché.
     */
    private void cargarProductosDisponibles() {
        if (this.productosDisponibles == null || this.productosDisponibles.isEmpty()) {
            try {
                this.productosDisponibles = productoDAO.findRange(0, 1000);
            } catch (Exception e) {
                this.productosDisponibles = new ArrayList<>();
            }
        }
    }

    // Getters y Setters

    public List<Producto> getProductosDisponibles() {
        return productosDisponibles;
    }

    public Long getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Long idCompra) {
        this.idCompra = idCompra;
        // Cuando cambia la compra, reinicializar la tabla
        if (idCompra != null) {
            initLazyModel();
        }
    }
}
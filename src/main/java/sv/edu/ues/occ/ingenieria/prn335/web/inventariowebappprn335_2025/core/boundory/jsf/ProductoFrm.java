package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Producto;
import java.io.Serializable;
import java.util.List;

@Named("productoBean")
@ViewScoped
public class ProductoFrm extends DefaultFrm<Producto> implements Serializable {

    @Inject
    private ProductoDAO productoDAO;

    @Override
    protected ProductoDAO obtenerDAO() {
        return productoDAO;
    }

    @Override
    protected void validarAntesDeCrear(Producto entidad) throws Exception {
        if (entidad.getNombreProducto() == null || entidad.getNombreProducto().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
    }

    @Override
    protected void validarAntesDeActualizar(Producto entidad) throws Exception {
        if (entidad.getNombreProducto() == null || entidad.getNombreProducto().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
    }

    @Override
    protected void validarAntesDeEliminar(Producto entidad, Producto original)
            throws Exception {
        if (!entidad.getNombreProducto().equals(original.getNombreProducto())) {
            throw new Exception("validacion.nombre.cambiado");
        }
    }

    @Override
    protected Producto instanciarEntidad() {
        Producto nuevo = new Producto();
        nuevo.setActivo(true);
        return nuevo;
    }

    public List<Producto> getProductosDeCompra(Long idCompra) {
        return productoDAO.findByCompra(idCompra);
    }

    public ProductoDAO getProductoDAO() {
        return productoDAO;
    }

    public void setProductoDAO(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }
}














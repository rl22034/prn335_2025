package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CompraDetalleDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.CompraDetalle;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Named("compraDetalleBean")
@ViewScoped
public class CompraDetalleFrm extends DefaultFrm<CompraDetalle> implements Serializable {

    @Inject
    private CompraDetalleDAO compraDetalleDAO;

    @Override
    protected CompraDetalleDAO obtenerDAO() {
        return compraDetalleDAO;
    }

    @Override
    protected void validarAntesDeCrear(CompraDetalle entidad) throws Exception {
        if (entidad.getIdCompra() == null || entidad.getIdProducto() == null) {
            throw new Exception("validacion.compra.producto.requerido");
        }
    }

    @Override
    protected void validarAntesDeActualizar(CompraDetalle entidad) throws Exception {
        if (entidad.getIdCompra() == null || entidad.getIdProducto() == null) {
            throw new Exception("validacion.compra.producto.requerido");
        }
    }

    @Override
    protected CompraDetalle instanciarEntidad() {
        return new CompraDetalle();
    }

    // Getters y setters del DAO

    public CompraDetalleDAO getCompraDetalleDAO() {
        return compraDetalleDAO;
    }

    public void setCompraDetalleDAO(CompraDetalleDAO compraDetalleDAO) {
        this.compraDetalleDAO = compraDetalleDAO;
    }
}
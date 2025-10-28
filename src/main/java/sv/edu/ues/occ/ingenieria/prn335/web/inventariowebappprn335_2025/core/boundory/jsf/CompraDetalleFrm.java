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
    protected void crearEntidad(CompraDetalle entidad) throws Exception {
        if (entidad.getIdCompra() == null || entidad.getIdProducto() == null) {
            throw new Exception("validacion.compra.producto.requerido");
        }
        compraDetalleDAO.crear(entidad);
    }

    @Override
    protected void actualizarEntidad(CompraDetalle entidad) throws Exception {
        if (entidad.getIdCompra() == null || entidad.getIdProducto() == null) {
            throw new Exception("validacion.compra.producto.requerido");
        }
        compraDetalleDAO.update(entidad);
    }

    @Override
    protected void eliminarEntidad(CompraDetalle entidad) throws Exception {
        CompraDetalle original = compraDetalleDAO.finById(entidad.getId());
        if (original == null) {
            throw new Exception("validacion.registro.no.existe");
        }
        compraDetalleDAO.delete(entidad);
    }

    @Override
    protected List<CompraDetalle> buscarEntidades(int first, int pageSize) throws Exception {
        return compraDetalleDAO.findRange(first, pageSize);
    }

    @Override
    protected Long contarEntidades() throws Exception {
        return compraDetalleDAO.count();
    }

    @Override
    protected Object obtenerIdEntidad(CompraDetalle entidad) {
        return entidad.getId();
    }

    @Override
    protected CompraDetalle instanciarEntidad() {
        return new CompraDetalle();
    }


    // Getters y setters

    public CompraDetalle getFilaSeleccionada() {
        return super.getFilaSeleccionada();
    }

    public void setFilaSeleccionada(CompraDetalle filaSeleccionada) {
        super.setFilaSeleccionada(filaSeleccionada);
    }

    public List<CompraDetalle> getEntidadesList() {
        return super.getEntidadesList();
    }

    public void setEntidadesList(List<CompraDetalle> entidadesList) {
        super.setEntidadesList(entidadesList);
    }

    public CompraDetalleDAO getCompraDetalleDAO() {
        return compraDetalleDAO;
    }

    public void setCompraDetalleDAO(CompraDetalleDAO compraDetalleDAO) {
        this.compraDetalleDAO = compraDetalleDAO;
    }
}
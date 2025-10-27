package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Producto;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProducto;

import java.io.Serializable;
import java.util.List;

@Named("productoBean")
@ViewScoped
public class ProductoFrm extends DefaultFrm<Producto> implements Serializable {

    @Inject
    private ProductoDAO productoDAO;

    @Override
    protected void crearEntidad(Producto entidad) throws Exception {
        if (entidad.getNombreProducto() == null || entidad.getNombreProducto().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
        productoDAO.crear(entidad);
    }

    @Override
    protected void actualizarEntidad(Producto entidad) throws Exception {
        if (entidad.getNombreProducto() == null || entidad.getNombreProducto().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
        productoDAO.update(entidad);
    }

    @Override
    protected void eliminarEntidad(Producto entidad) throws Exception {
        Producto original = productoDAO.finById(entidad.getId());
        if (original ==  null){
            throw new Exception("validacion.registro.no.existe");
        }

        if(!entidad.getNombreProducto().equals(original.getNombreProducto())){
            throw new Exception("validacion.nombre.cambiado");
        }
        productoDAO.delete(entidad);
    }

    @Override
    protected List<Producto> buscarEntidades(int first, int pageSize) throws Exception {
        return productoDAO.findRange(first, pageSize);
    }

    @Override
    protected Long contarEntidades() throws Exception {
        return productoDAO.count();
    }

    @Override
    protected Object obtenerIdEntidad(Producto entidad) {
        return entidad.getId();
    }

    @Override
    protected Producto instanciarEntidad() {
        Producto nuevo = new Producto();
        nuevo.setActivo(true);
        return nuevo;
    }

    public Producto getFilaSeleccionada() {
        return super.getFilaSeleccionada();
    }

    public void setFilaSeleccionada(Producto filaSeleccionada) {
        super.setFilaSeleccionada(filaSeleccionada);
    }

    public List<Producto> getEntidadesList() {
        return super.getEntidadesList();
    }

    public void setEntidadesList(List<Producto> almacenList) {
        super.setEntidadesList(entidadesList);
    }

    public ProductoDAO getProductoDAO() {
        return productoDAO;
    }

    public void setProductoDAO(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }
}















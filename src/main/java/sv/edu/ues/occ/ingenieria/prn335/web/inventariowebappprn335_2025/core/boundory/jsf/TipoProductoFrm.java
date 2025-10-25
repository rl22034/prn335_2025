package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProducto;

import java.io.Serializable;
import java.util.List;

@Named("tipoProductoBean")
@ViewScoped
public class TipoProductoFrm extends DefaultFrm<TipoProducto> implements Serializable {

    @Inject
    private TipoProductoDAO tipoProductoDAO;

    @Override
    protected void crearEntidad(TipoProducto entidad) throws Exception {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
        tipoProductoDAO.crear(entidad);
    }

    @Override
    protected void actualizarEntidad(TipoProducto entidad) throws Exception {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
        tipoProductoDAO.update(entidad);
    }

    @Override
    protected void eliminarEntidad(TipoProducto entidad) throws Exception {

        TipoProducto original = tipoProductoDAO.finById(entidad.getId());
        if (original ==  null){
            throw new Exception("validacion.registro.no.existe");
        }

        if(!entidad.getNombre().equals(original.getNombre())){
            throw new Exception("validacion.nombre.cambiado");
        }
        tipoProductoDAO.delete(entidad);
    }

    @Override
    protected List<TipoProducto> buscarEntidades(int first, int pageSize) throws Exception {
        return tipoProductoDAO.findRange(first, pageSize);
    }

    @Override
    protected Long contarEntidades() throws Exception {
        return tipoProductoDAO.count();
    }

    @Override
    protected Object obtenerIdEntidad(TipoProducto entidad) {
        return entidad.getId();
    }

    @Override
    protected TipoProducto instanciarEntidad() {
        TipoProducto nuevo = new TipoProducto();
        nuevo.setActivo(true);
        return nuevo;
    }

    // Getters y setters

    public TipoProducto getFilaSeleccionada() {
        return super.getFilaSeleccionada();
    }

    public void setFilaSeleccionada(TipoProducto filaSeleccionada) {
        super.setFilaSeleccionada(filaSeleccionada);
    }

    public List<TipoProducto> getEntidadesList() {
        return super.getEntidadesList();
    }

    public void setEntidadesList(List<TipoProducto> entidadesList) {
        super.setEntidadesList(entidadesList);
    }

    public TipoProductoDAO getTipoProductoDAO() {
        return tipoProductoDAO;
    }

    public void setTipoProductoDAO(TipoProductoDAO tipoProductoDAO) {
        this.tipoProductoDAO = tipoProductoDAO;
    }
}

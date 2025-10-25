package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoUnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoUnidadMedida;

import java.io.Serializable;
import java.util.List;

@Named("tipoUnidadMedidaBean")
@ViewScoped
public class TipoUnidadMedidaFrm extends DefaultFrm<TipoUnidadMedida> implements Serializable {

    @Inject
    private TipoUnidadMedidaDAO tipoUnidadMedidaDAO;

    @Override
    protected void crearEntidad(TipoUnidadMedida entidad) throws Exception {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
        tipoUnidadMedidaDAO.crear(entidad);
    }

    @Override
    protected void actualizarEntidad(TipoUnidadMedida entidad) throws Exception {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
        tipoUnidadMedidaDAO.update(entidad);
    }

    @Override
    protected void eliminarEntidad(TipoUnidadMedida entidad) throws Exception {
        TipoUnidadMedida original = tipoUnidadMedidaDAO.finById(entidad.getId());

        if (original == null) {
            throw new Exception("validacion.registro.no.existe");
        }

        if (!entidad.getNombre().equals(original.getNombre())) {
            throw new Exception("validacion.nombre.cambiado");
        }

        tipoUnidadMedidaDAO.delete(entidad);
    }

    @Override
    protected List<TipoUnidadMedida> buscarEntidades(int first, int pageSize) throws Exception {
        return tipoUnidadMedidaDAO.findRange(first, pageSize);
    }

    @Override
    protected Long contarEntidades() throws Exception {
        return tipoUnidadMedidaDAO.count();
    }

    @Override
    protected Object obtenerIdEntidad(TipoUnidadMedida entidad) {
        return entidad.getId();
    }

    @Override
    protected TipoUnidadMedida instanciarEntidad() {
        TipoUnidadMedida nuevo = new TipoUnidadMedida();
        nuevo.setActivo(true);
        return nuevo;
    }

    // Getters y setters

    public TipoUnidadMedida getFilaSeleccionada() {
        return super.getFilaSeleccionada();
    }

    public void setFilaSeleccionada(TipoUnidadMedida filaSeleccionada) {
        super.setFilaSeleccionada(filaSeleccionada);
    }

    public List<TipoUnidadMedida> getEntidadesList() {
        return super.getEntidadesList();
    }

    public void setEntidadesList(List<TipoUnidadMedida> almacenList) {
        super.setEntidadesList(entidadesList);
    }

    public TipoUnidadMedidaDAO getTipoUnidadMedidaDAO() {
        return tipoUnidadMedidaDAO;
    }

    public void setTipoUnidadMedidaDAO(TipoUnidadMedidaDAO tipoUnidadMedidaDAO) {
        this.tipoUnidadMedidaDAO = tipoUnidadMedidaDAO;
    }
}

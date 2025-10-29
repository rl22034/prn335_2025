package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoUnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoUnidadMedida;

import java.io.Serializable;
import java.util.List;

@Named("caracteristicaBean")
@ViewScoped
public class CaracteristicaFrm extends DefaultFrm<Caracteristica> implements Serializable {

    @Inject
    private CaracteristicaDAO caracteristicaDAO;

    @Inject
    private TipoUnidadMedidaDAO tipoUnidadMedidaDAO;

    private List<TipoUnidadMedida> tipoUnidadMedidaList;

    @Override
    protected void crearEntidad(Caracteristica entidad) throws Exception {
        // Validar TipoUnidadMedida
        if (entidad.getIdTipoUnidadMedida() == null) {
            throw new Exception("validacion.tipounidadmedida.requerido");
        }

        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }

        caracteristicaDAO.crear(entidad);
    }

    @Override
    protected void actualizarEntidad(Caracteristica entidad) throws Exception {
        // Validar TipoUnidadMedida
        if (entidad.getIdTipoUnidadMedida() == null) {
            throw new Exception("validacion.tipounidadmedida.requerido");
        }


        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }

        caracteristicaDAO.update(entidad);
    }

    @Override
    protected void eliminarEntidad(Caracteristica entidad) throws Exception {
        Caracteristica original = caracteristicaDAO.finById(entidad.getId());

        if (original == null) {
            throw new Exception("validacion.registro.no.existe");
        }

        if (!entidad.getNombre().equals(original.getNombre())) {
            throw new Exception("validacion.nombre.cambiado");
        }

        caracteristicaDAO.delete(entidad);
    }

    @Override
    protected List<Caracteristica> buscarEntidades(int first, int pageSize) throws Exception {
        return caracteristicaDAO.findRange(first, pageSize);
    }

    @Override
    protected Long contarEntidades() throws Exception {
        return caracteristicaDAO.count();
    }

    @Override
    protected Object obtenerIdEntidad(Caracteristica entidad) {
        return entidad.getId();
    }

    @Override
    protected Caracteristica instanciarEntidad() {
        Caracteristica nuevo = new Caracteristica();
        nuevo.setActivo(true);
        return nuevo;
    }

    /**
     * Carga la lista de TipoUnidadMedida para el dropdown
     */
    public List<TipoUnidadMedida> getTipoUnidadMedidaList() {
        if (tipoUnidadMedidaList == null) {
            try {
                tipoUnidadMedidaList = tipoUnidadMedidaDAO.findRange(0, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tipoUnidadMedidaList;
    }

    public void setTipoUnidadMedidaList(List<TipoUnidadMedida> tipoUnidadMedidaList) {
        this.tipoUnidadMedidaList = tipoUnidadMedidaList;
    }

    // Getters y setters

    public Caracteristica getFilaSeleccionada() {
        return super.getFilaSeleccionada();
    }

    public void setFilaSeleccionada(Caracteristica filaSeleccionada) {
        super.setFilaSeleccionada(filaSeleccionada);
    }

    public List<Caracteristica> getEntidadesList() {
        return super.getEntidadesList();
    }

    public void setEntidadesList(List<Caracteristica> entidadesList) {
        super.setEntidadesList(entidadesList);
    }

    public CaracteristicaDAO getCaracteristicaDAO() {
        return caracteristicaDAO;
    }

    public void setCaracteristicaDAO(CaracteristicaDAO caracteristicaDAO) {
        this.caracteristicaDAO = caracteristicaDAO;
    }

    public TipoUnidadMedidaDAO getTipoUnidadMedidaDAO() {
        return tipoUnidadMedidaDAO;
    }

    public void setTipoUnidadMedidaDAO(TipoUnidadMedidaDAO tipoUnidadMedidaDAO) {
        this.tipoUnidadMedidaDAO = tipoUnidadMedidaDAO;
    }
}

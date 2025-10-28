package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.UnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoUnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.UnidadMedida;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoUnidadMedida;

import java.io.Serializable;
import java.util.List;

@Named("unidadMedidaBean")
@ViewScoped
public class UnidadMedidaFrm extends DefaultFrm<UnidadMedida> implements Serializable {

    @Inject
    private UnidadMedidaDAO unidadMedidaDAO;

    @Inject
    private TipoUnidadMedidaDAO tipoUnidadMedidaDAO;

    private List<TipoUnidadMedida> tipoUnidadMedidaList;

    @Override
    protected void crearEntidad(UnidadMedida entidad) throws Exception {

        // Validar equivalencia
        if (entidad.getEquivalencia() == null) {
            throw new Exception("validacion.equivalencia.requerida");
        }

        unidadMedidaDAO.crear(entidad);
    }

    @Override
    protected void actualizarEntidad(UnidadMedida entidad) throws Exception {

        // Validar equivalencia
        if (entidad.getEquivalencia() == null) {
            throw new Exception("validacion.equivalencia.requerida");
        }

        unidadMedidaDAO.update(entidad);
    }

    @Override
    protected void eliminarEntidad(UnidadMedida entidad) throws Exception {
        UnidadMedida original = unidadMedidaDAO.finById(entidad.getId());

        if (original == null) {
            throw new Exception("validacion.registro.no.existe");
        }

        unidadMedidaDAO.delete(entidad);
    }

    @Override
    protected List<UnidadMedida> buscarEntidades(int first, int pageSize) throws Exception {
        return unidadMedidaDAO.findRange(first, pageSize);
    }

    @Override
    protected Long contarEntidades() throws Exception {
        return unidadMedidaDAO.count();
    }

    @Override
    protected Object obtenerIdEntidad(UnidadMedida entidad) {
        return entidad.getId();
    }

    @Override
    protected UnidadMedida instanciarEntidad() {
        UnidadMedida nuevo = new UnidadMedida();
        nuevo.setActivo(true);
        return nuevo;
    }

    /**
     * Carga la lista de TipoUnidadMedida ACTIVOS para el dropdown
     */
    public List<TipoUnidadMedida> getTipoUnidadMedidaList() {
        if (tipoUnidadMedidaList == null) {
            try {
                tipoUnidadMedidaList = tipoUnidadMedidaDAO.findRange(0, 1000)
                        .stream()
                        .filter(t -> t.getActivo() != null && t.getActivo())  // ← FILTRA SOLO ACTIVOS
                        .collect(java.util.stream.Collectors.toList());
            } catch (Exception e) {
                e.printStackTrace();
                tipoUnidadMedidaList = new java.util.ArrayList<>();  // ← EVITA NULL
            }
        }
        return tipoUnidadMedidaList;
    }
    // Getters y setters

    public UnidadMedida getFilaSeleccionada() {
        return super.getFilaSeleccionada();
    }

    public void setFilaSeleccionada(UnidadMedida filaSeleccionada) {
        super.setFilaSeleccionada(filaSeleccionada);
    }

    public List<UnidadMedida> getEntidadesList() {
        return super.getEntidadesList();
    }

    public void setEntidadesList(List<UnidadMedida> entidadesList) {
        super.setEntidadesList(entidadesList);
    }

    public UnidadMedidaDAO getUnidadMedidaDAO() {
        return unidadMedidaDAO;
    }

    public void setUnidadMedidaDAO(UnidadMedidaDAO unidadMedidaDAO) {
        this.unidadMedidaDAO = unidadMedidaDAO;
    }

    public TipoUnidadMedidaDAO getTipoUnidadMedidaDAO() {
        return tipoUnidadMedidaDAO;
    }

    public void setTipoUnidadMedidaDAO(TipoUnidadMedidaDAO tipoUnidadMedidaDAO) {
        this.tipoUnidadMedidaDAO = tipoUnidadMedidaDAO;
    }
}
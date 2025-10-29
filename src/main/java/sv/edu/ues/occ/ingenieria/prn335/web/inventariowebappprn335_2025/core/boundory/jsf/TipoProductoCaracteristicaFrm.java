package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoProductoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProductoCaracteristica;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProducto;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

@Named("tipoProductoCaracteristicaBean")
@ViewScoped
public class TipoProductoCaracteristicaFrm extends DefaultFrm<TipoProductoCaracteristica> implements Serializable {

    @Inject
    private TipoProductoCaracteristicaDAO tipoProductoCaracteristicaDAO;

    @Inject
    private CaracteristicaDAO caracteristicaDAO;

    private List<Caracteristica> caracteristicasList;
    private TipoProducto tipoProductoActual;

    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    protected void crearEntidad(TipoProductoCaracteristica entidad) throws Exception {
        if (entidad.getIdCaracteristica() == null) {
            throw new Exception("Debe seleccionar una característica");
        }
        entidad.setIdTipoProducto(tipoProductoActual);
        if (entidad.getFechaCreacion() == null) {
            entidad.setFechaCreacion(OffsetDateTime.now());
        }
        tipoProductoCaracteristicaDAO.crear(entidad);
    }

    @Override
    protected void actualizarEntidad(TipoProductoCaracteristica entidad) throws Exception {
        if (entidad.getIdCaracteristica() == null) {
            throw new Exception("Debe seleccionar una característica");
        }
        tipoProductoCaracteristicaDAO.update(entidad);
    }

    @Override
    protected void eliminarEntidad(TipoProductoCaracteristica entidad) throws Exception {
        TipoProductoCaracteristica original = tipoProductoCaracteristicaDAO.finById(entidad.getId());
        if (original == null) {
            throw new Exception("validacion.registro.no.existe");
        }
        tipoProductoCaracteristicaDAO.delete(entidad);
    }

    @Override
    protected List<TipoProductoCaracteristica> buscarEntidades(int first, int pageSize) throws Exception {
        return tipoProductoCaracteristicaDAO.findRange(first, pageSize);
    }

    @Override
    protected Long contarEntidades() throws Exception {
        return tipoProductoCaracteristicaDAO.count();
    }

    @Override
    protected Object obtenerIdEntidad(TipoProductoCaracteristica entidad) {
        return entidad.getId();
    }

    @Override
    protected TipoProductoCaracteristica instanciarEntidad() {
        TipoProductoCaracteristica nuevo = new TipoProductoCaracteristica();
        nuevo.setObligatorio(false);
        return nuevo;
    }

    public void onRowSelect() {
        if (filaSeleccionada != null) {
            estado = CRUD.MODIFICAR;
        }
    }

    public List<Caracteristica> getCaracteristicasList() {
        if (caracteristicasList == null) {
            try {
                caracteristicasList = caracteristicaDAO.findRange(0, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return caracteristicasList;
    }

    public void setCaracteristicasList(List<Caracteristica> caracteristicasList) {
        this.caracteristicasList = caracteristicasList;
    }

    public TipoProducto getTipoProductoActual() {
        return tipoProductoActual;
    }

    public void setTipoProductoActual(TipoProducto tipoProductoActual) {
        this.tipoProductoActual = tipoProductoActual;
    }

}














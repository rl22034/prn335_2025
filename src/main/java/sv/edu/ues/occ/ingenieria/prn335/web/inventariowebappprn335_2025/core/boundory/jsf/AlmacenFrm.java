package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.AlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoAlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Almacen;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoAlmacen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("almacenBean")
@ViewScoped
public class AlmacenFrm extends DefaultFrm<Almacen> implements Serializable {

    @Inject
    private AlmacenDAO almacenDAO;

    @Inject
    private TipoAlmacenDAO tipoAlmacenDAO;


    @Override
    protected void crearEntidad(Almacen entidad) throws Exception {
        try {
            if (entidad.getIdTipoAlmacen() == null) {
                throw new Exception("validacion.tipoalmacen.requerido");
            }

            almacenDAO.crear(entidad);

        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    protected void actualizarEntidad(Almacen entidad) throws Exception {
        if (entidad.getIdTipoAlmacen() == null) {
            throw new Exception("validacion.tipoalmacen.requerido");
        }

        try {
            almacenDAO.update(entidad);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    protected void eliminarEntidad(Almacen entidad) throws Exception {
        try {
            Almacen original = almacenDAO.finById(entidad.getId());

            if (original == null) {
                throw new Exception("validacion.registro.no.existe");
            }

            if (!entidad.getIdTipoAlmacen().getId().equals(original.getIdTipoAlmacen().getId())) {
                throw new Exception("validacion.tipoalmacen.cambiado");
            }

            almacenDAO.delete(entidad);

        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    protected List<Almacen> buscarEntidades(int first, int pageSize) throws Exception {
        try {
            return almacenDAO.findRange(first, pageSize);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    protected Long contarEntidades() throws Exception {
        try {
            return almacenDAO.count();
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    protected Object obtenerIdEntidad(Almacen entidad) {
        return entidad.getId();
    }


    /**
     * Obtiene solo los tipos de almacén activos
     */
    public List<TipoAlmacen> getTiposAlmacenActivos() {
        try {
            return tipoAlmacenDAO.findTiposActivos();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public TipoAlmacenDAO getTipoAlmacenDAO() {
        return tipoAlmacenDAO;
    }

    public void setTipoAlmacenDAO(TipoAlmacenDAO tipoAlmacenDAO) {
        this.tipoAlmacenDAO = tipoAlmacenDAO;
    }

    @Override
    protected Almacen instanciarEntidad() {
        Almacen nuevo = new Almacen();
        nuevo.setActivo(true);
        return nuevo;
    }

    public Almacen getFilaSeleccionada() {
        return super.getFilaSeleccionada();
    }

    public void setFilaSeleccionada(Almacen filaSeleccionada) {
        super.setFilaSeleccionada(filaSeleccionada);
    }

    public List<Almacen> getEntidadesList() {
        return super.getEntidadesList();
    }

    public void setEntidadesList(List<Almacen> entidadesList) {
        super.setEntidadesList(entidadesList);
    }

    public AlmacenDAO getAlmacenDAO() {
        return almacenDAO;
    }

    public void setAlmacenDAO(AlmacenDAO almacenDAO) {
        this.almacenDAO = almacenDAO;
    }
}
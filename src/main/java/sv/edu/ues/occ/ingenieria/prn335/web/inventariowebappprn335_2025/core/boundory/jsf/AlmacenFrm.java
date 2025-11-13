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

    @Inject
    private TipoAlmacenFrm tipoAlmacenBean;

    @Override
    protected AlmacenDAO obtenerDAO() {
        return almacenDAO;
    }

    @Override
    protected void validarAntesDeCrear(Almacen entidad) throws Exception {
        if (entidad.getIdTipoAlmacen() == null) {
            throw new Exception("validacion.tipoalmacen.requerido");
        }
    }

    @Override
    protected void validarAntesDeActualizar(Almacen entidad) throws Exception {
        if (entidad.getIdTipoAlmacen() == null) {
            throw new Exception("validacion.tipoalmacen.requerido");
        }
    }

    @Override
    protected void validarAntesDeEliminar(Almacen entidad, Almacen original)
            throws Exception {
        if (!entidad.getIdTipoAlmacen().getId().equals(original.getIdTipoAlmacen().getId())) {
            throw new Exception("validacion.tipoalmacen.cambiado");
        }
    }

    @Override
    protected List<Almacen> buscarEntidades(int first, int pageSize) throws Exception {
        try {
            // Obtener el TipoAlmacen seleccionado del bean padre (si existe)
            TipoAlmacen tipoSeleccionado = tipoAlmacenBean != null ? tipoAlmacenBean.getFilaSeleccionada() : null;

            // Si hay un tipo seleccionado, filtrar por ese tipo (maestro-detalle)
            if (tipoSeleccionado != null && tipoSeleccionado.getId() != null) {
                return almacenDAO.findByTipoAlmacen(tipoSeleccionado.getId(), first, pageSize);
            }

            // Si no hay tipo seleccionado, mostrar una lista vacía
            return new ArrayList<>();

        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    protected Long contarEntidades() throws Exception {
        try {
            // Obtener el TipoAlmacen seleccionado del bean padre (si existe)
            TipoAlmacen tipoSeleccionado = tipoAlmacenBean != null ? tipoAlmacenBean.getFilaSeleccionada() : null;

            // Si hay un tipo seleccionado, contar solo los de ese tipo (maestro-detalle)
            if (tipoSeleccionado != null && tipoSeleccionado.getId() != null) {
                return almacenDAO.countByTipoAlmacen(tipoSeleccionado.getId());
            }

            // Si no hay tipo seleccionado, contar TODOS los almacenes (página independiente)
            return almacenDAO.count();

        } catch (Exception e) {
            throw e;
        }
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

    @Override
    protected Almacen instanciarEntidad() {
        Almacen nuevo = new Almacen();
        nuevo.setActivo(true);

        // Si hay un tipo seleccionado, asignarlo automáticamente (maestro-detalle)
        TipoAlmacen tipoSeleccionado = tipoAlmacenBean != null ? tipoAlmacenBean.getFilaSeleccionada() : null;
        if (tipoSeleccionado != null && tipoSeleccionado.getId() != null) {
            nuevo.setIdTipoAlmacen(tipoSeleccionado);
        }

        return nuevo;
    }

    // Getters y Setters de los DAOs

    public AlmacenDAO getAlmacenDAO() {
        return almacenDAO;
    }

    public void setAlmacenDAO(AlmacenDAO almacenDAO) {
        this.almacenDAO = almacenDAO;
    }

    public TipoAlmacenDAO getTipoAlmacenDAO() {
        return tipoAlmacenDAO;
    }

    public void setTipoAlmacenDAO(TipoAlmacenDAO tipoAlmacenDAO) {
        this.tipoAlmacenDAO = tipoAlmacenDAO;
    }
}
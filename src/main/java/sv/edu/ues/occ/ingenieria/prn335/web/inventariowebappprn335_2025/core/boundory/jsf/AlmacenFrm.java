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
            // Obtener el TipoAlmacen seleccionado del bean padre (si existe)
            TipoAlmacen tipoSeleccionado = tipoAlmacenBean != null ? tipoAlmacenBean.getFilaSeleccionada() : null;

            // Si hay un tipo seleccionado, filtrar por ese tipo (maestro-detalle)
            if (tipoSeleccionado != null && tipoSeleccionado.getId() != null) {
                return almacenDAO.findByTipoAlmacen(tipoSeleccionado.getId(), first, pageSize);
            }

            // Si no hay tipo seleccionado, mostrar TODOS los almacenes (página independiente)
            return almacenDAO.findRange(first, pageSize);

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

    // Getters y Setters
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

    public TipoAlmacenDAO getTipoAlmacenDAO() {
        return tipoAlmacenDAO;
    }

    public void setTipoAlmacenDAO(TipoAlmacenDAO tipoAlmacenDAO) {
        this.tipoAlmacenDAO = tipoAlmacenDAO;
    }
}
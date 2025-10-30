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

    // INYECTAR EL BEAN PADRE
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

    // MODIFICAR PARA FILTRAR POR TIPO ALMACEN
    @Override
    protected List<Almacen> buscarEntidades(int first, int pageSize) throws Exception {
        try {
            // Obtener el TipoAlmacen seleccionado del bean padre
            TipoAlmacen tipoSeleccionado = tipoAlmacenBean != null ? tipoAlmacenBean.getFilaSeleccionada() : null;

            if (tipoSeleccionado != null && tipoSeleccionado.getId() != null) {
                // Filtrar por el TipoAlmacen seleccionado
                return almacenDAO.findByTipoAlmacen(tipoSeleccionado.getId(), first, pageSize);
            }

            // Si no hay selección, devolver lista vacía
            return new ArrayList<>();

        } catch (Exception e) {
            throw e;
        }
    }

    // ⭐ MODIFICAR PARA CONTAR SOLO LOS DEL TIPO SELECCIONADO
    @Override
    protected Long contarEntidades() throws Exception {
        try {
            // Obtener el TipoAlmacen seleccionado del bean padre
            TipoAlmacen tipoSeleccionado = tipoAlmacenBean != null ? tipoAlmacenBean.getFilaSeleccionada() : null;

            if (tipoSeleccionado != null && tipoSeleccionado.getId() != null) {
                // Contar solo los del TipoAlmacen seleccionado
                return almacenDAO.countByTipoAlmacen(tipoSeleccionado.getId());
            }

            // Si no hay selección, devolver 0
            return 0L;

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

        // ASIGNAR AUTOMÁTICAMENTE EL TIPO ALMACEN SELECCIONADO
        TipoAlmacen tipoSeleccionado = tipoAlmacenBean != null ? tipoAlmacenBean.getFilaSeleccionada() : null;
        if (tipoSeleccionado != null) {
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
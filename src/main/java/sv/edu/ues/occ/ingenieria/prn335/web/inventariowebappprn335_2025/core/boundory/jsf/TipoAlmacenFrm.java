package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoAlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoAlmacen;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.MessageHelper;

import java.io.Serializable;
import java.util.List;

@Named("tipoAlmacenBean")
@ViewScoped
public class TipoAlmacenFrm extends DefaultFrm<TipoAlmacen> implements Serializable {

    @Inject
    private TipoAlmacenDAO tipoAlmacenDAO;

    @Override
    protected TipoAlmacenDAO obtenerDAO() {
        return tipoAlmacenDAO;
    }

    @Override
    protected void validarAntesDeCrear(TipoAlmacen entidad) throws Exception {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
    }

    @Override
    protected void validarAntesDeActualizar(TipoAlmacen entidad) throws Exception {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
    }

    @Override
    protected void validarAntesDeEliminar(TipoAlmacen entidad, TipoAlmacen original)
            throws Exception {
        if (!entidad.getNombre().equals(original.getNombre())) {
            throw new Exception("validacion.nombre.cambiado");
        }
    }

    @Override
    protected TipoAlmacen instanciarEntidad() {
        TipoAlmacen nuevo = new TipoAlmacen();
        nuevo.setActivo(true);
        return nuevo;
    }

    // Getters y setters del DAO

    public TipoAlmacenDAO getTipoAlmacenDAO() {
        return tipoAlmacenDAO;
    }

    public void setTipoAlmacenDAO(TipoAlmacenDAO tipoAlmacenDAO) {
        this.tipoAlmacenDAO = tipoAlmacenDAO;
    }
}
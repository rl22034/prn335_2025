package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProveedorDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Proveedor;

import java.io.Serializable;
import java.util.List;

@Named("proveedorBean")
@ViewScoped
public class ProveedorFrm extends DefaultFrm<Proveedor> implements Serializable {

    @Inject
    private ProveedorDAO proveedorDAO;

    @Override
    protected ProveedorDAO obtenerDAO() {
        return proveedorDAO;
    }

    @Override
    protected void validarAntesDeCrear(Proveedor entidad) throws Exception {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
    }

    @Override
    protected void validarAntesDeActualizar(Proveedor entidad) throws Exception {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
    }

    @Override
    protected void validarAntesDeEliminar(Proveedor entidad, Proveedor original) throws Exception {
        if (!entidad.getNombre().equals(original.getNombre())) {
            throw new Exception("validacion.nombre.cambiado");
        }
    }

    @Override
    protected Proveedor instanciarEntidad() {
        Proveedor nuevo = new Proveedor();
        nuevo.setActivo(true);
        return nuevo;
    }

    // Getters y setters del DAO

    public ProveedorDAO getProveedorDAO() {
        return proveedorDAO;
    }

    public void setProveedorDAO(ProveedorDAO proveedorDAO) {
        this.proveedorDAO = proveedorDAO;
    }
}
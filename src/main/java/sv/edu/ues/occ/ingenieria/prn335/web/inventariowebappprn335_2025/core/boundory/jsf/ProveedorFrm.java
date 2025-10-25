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
    protected void crearEntidad(Proveedor entidad) throws Exception {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
        proveedorDAO.crear(entidad);
    }

    @Override
    protected void actualizarEntidad(Proveedor entidad) throws Exception {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
        proveedorDAO.update(entidad);
    }

    @Override
    protected void eliminarEntidad(Proveedor entidad) throws Exception {
        Proveedor original = proveedorDAO.finById(entidad.getId());

        if (original == null) {
            throw new Exception("validacion.registro.no.existe");
        }

        if (!entidad.getNombre().equals(original.getNombre())) {
            throw new Exception("validacion.nombre.cambiado");
        }

        proveedorDAO.delete(entidad);
    }

    @Override
    protected List<Proveedor> buscarEntidades(int first, int pageSize) throws Exception {
        return proveedorDAO.findRange(first, pageSize);
    }

    @Override
    protected Long contarEntidades() throws Exception {
        return proveedorDAO.count();
    }

    @Override
    protected Object obtenerIdEntidad(Proveedor entidad) {
        return entidad.getId();
    }

    @Override
    protected Proveedor instanciarEntidad() {
        Proveedor nuevo = new Proveedor();
        nuevo.setActivo(true);
        return nuevo;
    }

    // Getters y setters

    public Proveedor getFilaSeleccionada() {
        return super.getFilaSeleccionada();
    }

    public void setFilaSeleccionada(Proveedor filaSeleccionada) {
        super.setFilaSeleccionada(filaSeleccionada);
    }

    public List<Proveedor> getEntidadesList() {
        return super.getEntidadesList();
    }

    public void setEntidadesList(List<Proveedor> entidadesList) {
        super.setEntidadesList(entidadesList);
    }

    public ProveedorDAO getProveedorDAO() {
        return proveedorDAO;
    }

    public void setProveedorDAO(ProveedorDAO proveedorDAO) {
        this.proveedorDAO = proveedorDAO;
    }
}
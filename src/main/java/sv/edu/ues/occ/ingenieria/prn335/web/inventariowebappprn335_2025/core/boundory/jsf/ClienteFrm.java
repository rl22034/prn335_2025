package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ClienteDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Cliente;

import java.io.Serializable;
import java.util.List;

@Named("clienteBean")
@ViewScoped
public class ClienteFrm extends DefaultFrm<Cliente> implements Serializable {

    @Inject
    private ClienteDAO clienteDAO;

    @Override
    protected void crearEntidad(Cliente entidad) throws Exception {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
        clienteDAO.crear(entidad);
    }

    @Override
    protected void actualizarEntidad(Cliente entidad) throws Exception {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
        clienteDAO.update(entidad);
    }

    @Override
    protected void eliminarEntidad(Cliente entidad) throws Exception {
        Cliente original = clienteDAO.finById(entidad.getId());

        if (original == null) {
            throw new Exception("validacion.registro.no.existe");
        }

        if (!entidad.getNombre().equals(original.getNombre())) {
            throw new Exception("validacion.nombre.cambiado");
        }

        clienteDAO.delete(entidad);
    }

    @Override
    protected List<Cliente> buscarEntidades(int first, int pageSize) throws Exception {
        return clienteDAO.findRange(first, pageSize);
    }

    @Override
    protected Long contarEntidades() throws Exception {
        return clienteDAO.count();
    }

    @Override
    protected Object obtenerIdEntidad(Cliente entidad) {
        return entidad.getId();
    }

    @Override
    protected Cliente instanciarEntidad() {
        Cliente nuevo = new Cliente();
        nuevo.setActivo(true);
        return nuevo;
    }

    // Getters y setters

    public Cliente getFilaSeleccionada() {
        return super.getFilaSeleccionada();
    }

    public void setFilaSeleccionada(Cliente filaSeleccionada) {
        super.setFilaSeleccionada(filaSeleccionada);
    }

    public List<Cliente> getEntidadesList() {
        return super.getEntidadesList();
    }

    public void setEntidadesList(List<Cliente> entidadesList) {
        super.setEntidadesList(entidadesList);
    }

    public ClienteDAO getClienteDAO() {
        return clienteDAO;
    }

    public void setClienteDAO(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }
}
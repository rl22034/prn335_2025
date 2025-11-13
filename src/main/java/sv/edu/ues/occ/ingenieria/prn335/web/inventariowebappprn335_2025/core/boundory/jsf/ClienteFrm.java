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
    protected ClienteDAO obtenerDAO() {
        return clienteDAO;
    }

    @Override
    protected void validarAntesDeCrear(Cliente entidad) throws Exception {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
    }

    @Override
    protected void validarAntesDeActualizar(Cliente entidad) throws Exception {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new Exception("validacion.nombre.requerido");
        }
    }

    @Override
    protected void validarAntesDeEliminar(Cliente entidad, Cliente original)
            throws Exception {
        if (!entidad.getNombre().equals(original.getNombre())) {
            throw new Exception("validacion.nombre.cambiado");
        }
    }

    @Override
    protected Cliente instanciarEntidad() {
        Cliente nuevo = new Cliente();
        nuevo.setActivo(true);
        return nuevo;
    }

    // Getters y setters del DAO

    public ClienteDAO getClienteDAO() {
        return clienteDAO;
    }

    public void setClienteDAO(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }
}
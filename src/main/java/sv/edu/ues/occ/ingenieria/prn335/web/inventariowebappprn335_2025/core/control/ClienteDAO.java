package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Cliente;

import java.io.Serializable;
import java.util.List; // <-- ¡Asegúrate de importar List!

@LocalBean
@Stateless
public class ClienteDAO extends InventarioDefaultDataAccess<Cliente> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    public ClienteDAO() {
        super(Cliente.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}
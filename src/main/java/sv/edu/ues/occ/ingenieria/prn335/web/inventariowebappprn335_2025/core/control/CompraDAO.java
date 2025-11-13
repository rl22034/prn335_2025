package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Compra;

import java.io.Serializable;
@Stateless
@LocalBean
public class CompraDAO extends InventarioDefaultDataAccess<Compra> implements Serializable {
    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    public CompraDAO(){super(Compra.class);}

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}

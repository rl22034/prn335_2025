package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Venta;

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class VentaDAO extends InventarioDefaultDataAccess<Venta> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    public VentaDAO() {
        super(Venta.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}
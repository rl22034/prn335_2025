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

    // Sobrescribimos los métodos básicos para que el EJB los exponga

    @Override
    public void crear(Venta entidad) {
        super.crear(entidad);
    }

    @Override
    public void delete(Venta entidad) {
        super.delete(entidad);
    }

    @Override
    public Venta finById(Object id) {
        return super.finById(id);
    }

    @Override
    public Venta update(Venta entidad) {
        return super.update(entidad);
    }
    @Override
    public List<Venta> findRange(int first, int pageSize) {
        return super.findRange(first, pageSize);
    }

    @Override
    public Long count() {
        return super.count();
    }
}
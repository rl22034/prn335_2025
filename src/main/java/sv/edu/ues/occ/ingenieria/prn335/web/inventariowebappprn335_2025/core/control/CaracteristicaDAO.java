package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Caracteristica; // Asegúrate que la entidad exista

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class CaracteristicaDAO extends InventarioDefaultDataAccess<Caracteristica> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    public CaracteristicaDAO() {
        super(Caracteristica.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    // Sobreescribimos los métodos para exponerlos como EJB
    @Override
    public void crear(Caracteristica entidad) {
        super.crear(entidad);
    }

    @Override
    public void delete(Caracteristica entidad) {
        super.delete(entidad);
    }

    @Override
    public Caracteristica finById(Object id) {
        // El ID de Caracteristica es Integer según el DDL
        return super.finById(id);
    }

    @Override
    public Caracteristica update(Caracteristica entidad) {
        return super.update(entidad);
    }

    @Override
    public List<Caracteristica> findRange(int first, int pageSize) {
        return super.findRange(first, pageSize);
    }

    @Override
    public Long count() {
        return super.count();
    }
}
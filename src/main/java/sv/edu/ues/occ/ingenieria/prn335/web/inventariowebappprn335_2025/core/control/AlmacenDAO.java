package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Almacen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Stateless
@LocalBean
public class AlmacenDAO extends InventarioDefaultDataAccess<Almacen> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    public AlmacenDAO() {
        super(Almacen.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void delete(Almacen entidad) {
        super.delete(entidad);
    }

    @Override
    public void crear(Almacen entidad) {
        super.crear(entidad);
    }

    @Override
    public Almacen finById(Object id) {
        // ✅ Cargar con TipoAlmacen
        try {
            return em.createQuery(
                            "SELECT a FROM Almacen a LEFT JOIN FETCH a.idTipoAlmacen WHERE a.id = :id",
                            Almacen.class
                    )
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Almacen> findRange(int first, int pageSize) {
        try {
            return em.createQuery(
                            "SELECT a FROM Almacen a LEFT JOIN FETCH a.idTipoAlmacen ORDER BY a.id ASC",
                            Almacen.class
                    )
                    .setFirstResult(first)
                    .setMaxResults(pageSize)
                    .getResultList();
        } catch (Exception e) {
            return List.of();
        }
    }

    public List<Almacen> findByTipoAlmacen(Integer idTipoAlmacen, int first, int pageSize) {
        try {
            return em.createQuery(
                            "SELECT a FROM Almacen a WHERE a.idTipoAlmacen.id = :idTipo ORDER BY a.id",
                            Almacen.class)
                    .setParameter("idTipo", idTipoAlmacen)
                    .setFirstResult(first)
                    .setMaxResults(pageSize)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Cuenta los almacenes de un tipo específico
     */
    public Long countByTipoAlmacen(Integer idTipoAlmacen) {
        try {
            return em.createQuery(
                            "SELECT COUNT(a) FROM Almacen a WHERE a.idTipoAlmacen.id = :idTipo",
                            Long.class)
                    .setParameter("idTipo", idTipoAlmacen)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
}

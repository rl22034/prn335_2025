package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProducto;

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class TipoProductoDAO extends InventarioDefaultDataAccess<TipoProducto> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    public TipoProductoDAO() {
        super(TipoProducto.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    // ⭐ SOBRESCRIBIR findRange para cargar la relación padre
    @Override
    public List<TipoProducto> findRange(int first, int max) {
        try {
            TypedQuery<TipoProducto> query = em.createQuery(
                    "SELECT DISTINCT tp FROM TipoProducto tp " +
                            "LEFT JOIN FETCH tp.idTipoProductoPadre " +
                            "ORDER BY tp.id",
                    TipoProducto.class
            );
            query.setFirstResult(first);
            query.setMaxResults(max);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener tipos de producto", e);
        }
    }

    @Override
    public void delete(TipoProducto entidad) {
        super.delete(entidad);
    }

    @Override
    public void crear(TipoProducto entidad) {
        super.crear(entidad);
    }

    @Override
    public TipoProducto finById(Object id) {
        return super.finById(id);
    }
}

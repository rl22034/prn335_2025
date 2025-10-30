package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.UnidadMedida;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Stateless
@LocalBean
public class UnidadMedidaDAO extends InventarioDefaultDataAccess<UnidadMedida> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    /**
     * Constructor que especifica la clase de entidad
     */
    public UnidadMedidaDAO() {
        super(UnidadMedida.class);
    }

    /**
     * Implementación requerida del método abstracto
     * Devuelve el EntityManager inyectado
     */
    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void delete(UnidadMedida entidad) {
        super.delete(entidad);
    }

    @Override
    public void crear(UnidadMedida entidad) {
        super.crear(entidad);
    }

    @Override
    public UnidadMedida finById(Object id) {
        return super.finById(id);
    }

    /**
     * Cuenta las UnidadMedida de un tipo específico
     * @param idTipoUnidadMedida ID del tipo de unidad de medida
     * @return Cantidad de registros
     */
    public Long countByTipoUnidadMedida(Integer idTipoUnidadMedida) {
        if (idTipoUnidadMedida == null) {
            return 0L;
        }
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(um) FROM UnidadMedida um " +
                            "WHERE um.idTipoUnidadMedida.id = :idTipo",
                    Long.class
            );
            query.setParameter("idTipo", idTipoUnidadMedida);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * Busca UnidadMedida de un tipo específico con paginación
     * @param idTipoUnidadMedida ID del tipo de unidad de medida
     * @param first Primer registro
     * @param max Máximo de registros
     * @return Lista paginada de UnidadMedida
     */
    public List<UnidadMedida> findByTipoUnidadMedida(Integer idTipoUnidadMedida, int first, int max) {
        if (idTipoUnidadMedida == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<UnidadMedida> query = em.createQuery(
                    "SELECT um FROM UnidadMedida um " +
                            "WHERE um.idTipoUnidadMedida.id = :idTipo " +
                            "ORDER BY um.id",
                    UnidadMedida.class
            );
            query.setParameter("idTipo", idTipoUnidadMedida);
            query.setFirstResult(first);
            query.setMaxResults(max);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
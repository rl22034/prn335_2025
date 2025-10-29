package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.ProductoTipoProductoCaracteristica;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProductoCaracteristica;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * DAO para gestionar las características de ProductoTipoProducto
 */
@Stateless
@LocalBean
public class ProductoTipoProductoCaracteristicaDAO extends InventarioDefaultDataAccess<ProductoTipoProductoCaracteristica> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    public ProductoTipoProductoCaracteristicaDAO() {
        super(ProductoTipoProductoCaracteristica.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void crear(ProductoTipoProductoCaracteristica entidad) {
        super.crear(entidad);
    }

    @Override
    public void delete(ProductoTipoProductoCaracteristica entidad) {
        super.delete(entidad);
    }

    @Override
    public ProductoTipoProductoCaracteristica finById(Object id) {
        return super.finById(id);
    }

    /**
     * Busca todas las características asignadas a un ProductoTipoProducto
     * @param idProductoTipoProducto ID del ProductoTipoProducto (UUID)
     * @return Lista de ProductoTipoProductoCaracteristica
     */
    public List<ProductoTipoProductoCaracteristica> findByProductoTipoProducto(java.util.UUID idProductoTipoProducto) {
        try {
            TypedQuery<ProductoTipoProductoCaracteristica> query = em.createQuery(
                    "SELECT ptpc FROM ProductoTipoProductoCaracteristica ptpc " +
                            "JOIN FETCH ptpc.idTipoProductoCaracteristica tpc " +
                            "JOIN FETCH tpc.idCaracteristica c " +
                            "WHERE ptpc.idProductoTipoProducto.id = :idProductoTipoProducto " +
                            "ORDER BY c.nombre",
                    ProductoTipoProductoCaracteristica.class
            );
            query.setParameter("idProductoTipoProducto", idProductoTipoProducto);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Busca una característica específica de un ProductoTipoProducto
     * @param idProductoTipoProducto ID del ProductoTipoProducto (UUID)
     * @param idTipoProductoCaracteristica ID de TipoProductoCaracteristica (Long)
     * @return ProductoTipoProductoCaracteristica o null si no existe
     */
    public ProductoTipoProductoCaracteristica findByProductoTipoProductoAndCaracteristica(
            java.util.UUID idProductoTipoProducto, Long idTipoProductoCaracteristica) {
        try {
            TypedQuery<ProductoTipoProductoCaracteristica> query = em.createQuery(
                    "SELECT ptpc FROM ProductoTipoProductoCaracteristica ptpc " +
                            "WHERE ptpc.idProductoTipoProducto.id = :idProductoTipoProducto " +
                            "AND ptpc.idTipoProductoCaracteristica.id = :idTipoProductoCaracteristica",
                    ProductoTipoProductoCaracteristica.class
            );
            query.setParameter("idProductoTipoProducto", idProductoTipoProducto);
            query.setParameter("idTipoProductoCaracteristica", idTipoProductoCaracteristica);
            List<ProductoTipoProductoCaracteristica> resultados = query.getResultList();
            return resultados.isEmpty() ? null : resultados.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Busca las características disponibles (no asignadas) para un TipoProducto
     * Este método devuelve las TipoProductoCaracteristica que NO están asignadas al ProductoTipoProducto
     * @param idProductoTipoProducto ID del ProductoTipoProducto (UUID)
     * @param idTipoProducto ID del TipoProducto (Long)
     * @return Lista de TipoProductoCaracteristica disponibles
     */
    public List<TipoProductoCaracteristica> findCaracteristicasDisponibles(
            java.util.UUID idProductoTipoProducto, Long idTipoProducto) {
        try {
            TypedQuery<TipoProductoCaracteristica> query = em.createQuery(
                    "SELECT tpc FROM TipoProductoCaracteristica tpc " +
                            "JOIN FETCH tpc.idCaracteristica c " +
                            "WHERE tpc.idTipoProducto.id = :idTipoProducto " +
                            "AND tpc.id NOT IN (" +
                            "   SELECT ptpc.idTipoProductoCaracteristica.id " +
                            "   FROM ProductoTipoProductoCaracteristica ptpc " +
                            "   WHERE ptpc.idProductoTipoProducto.id = :idProductoTipoProducto" +
                            ") " +
                            "ORDER BY c.nombre",
                    TipoProductoCaracteristica.class
            );
            query.setParameter("idProductoTipoProducto", idProductoTipoProducto);
            query.setParameter("idTipoProducto", idTipoProducto);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Elimina todas las características de un ProductoTipoProducto
     * @param idProductoTipoProducto ID del ProductoTipoProducto (UUID)
     */
    public void deleteByProductoTipoProducto(java.util.UUID idProductoTipoProducto) {
        try {
            em.createQuery(
                            "DELETE FROM ProductoTipoProductoCaracteristica ptpc " +
                                    "WHERE ptpc.idProductoTipoProducto.id = :idProductoTipoProducto"
                    )
                    .setParameter("idProductoTipoProducto", idProductoTipoProducto)
                    .executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar características", e);
        }
    }
}
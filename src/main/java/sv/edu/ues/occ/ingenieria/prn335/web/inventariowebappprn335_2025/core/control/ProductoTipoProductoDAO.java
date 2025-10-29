package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.ProductoTipoProducto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Stateless
@LocalBean
public class ProductoTipoProductoDAO extends InventarioDefaultDataAccess<ProductoTipoProducto> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    public ProductoTipoProductoDAO() {
        super(ProductoTipoProducto.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void delete(ProductoTipoProducto entidad) {
        super.delete(entidad);
    }

    @Override
    public void crear(ProductoTipoProducto entidad) {
        super.crear(entidad);
    }

    @Override
    public ProductoTipoProducto finById(Object id) {
        return super.finById(id);
    }

    /**
     * Busca todos los TipoProducto asignados a un Producto específico
     * @param idProducto ID del producto (UUID)
     * @return Lista de ProductoTipoProducto
     */
    public List<ProductoTipoProducto> findByProducto(UUID idProducto) {
        try {
            TypedQuery<ProductoTipoProducto> query = em.createQuery(
                    "SELECT ptp FROM ProductoTipoProducto ptp " +
                            "WHERE ptp.idProducto.id = :idProducto " +
                            "ORDER BY ptp.fechaCreacion DESC",
                    ProductoTipoProducto.class
            );
            query.setParameter("idProducto", idProducto);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Busca un ProductoTipoProducto específico por producto y tipo
     * @param idProducto ID del producto (UUID)
     * @param idTipoProducto ID del tipo de producto (Long)
     * @return ProductoTipoProducto o null si no existe
     */
    public ProductoTipoProducto findByProductoAndTipo(UUID idProducto, Long idTipoProducto) {
        try {
            TypedQuery<ProductoTipoProducto> query = em.createQuery(
                    "SELECT ptp FROM ProductoTipoProducto ptp " +
                            "WHERE ptp.idProducto.id = :idProducto " +
                            "AND ptp.idTipoProducto.id = :idTipoProducto",
                    ProductoTipoProducto.class
            );
            query.setParameter("idProducto", idProducto);
            query.setParameter("idTipoProducto", idTipoProducto);
            List<ProductoTipoProducto> resultados = query.getResultList();
            return resultados.isEmpty() ? null : resultados.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Verifica si ya existe una relación entre producto y tipo
     * @param idProducto ID del producto (UUID)
     * @param idTipoProducto ID del tipo de producto (Long)
     * @return true si existe, false si no
     */
    public boolean existeRelacion(UUID idProducto, Long idTipoProducto) {
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(ptp) FROM ProductoTipoProducto ptp " +
                            "WHERE ptp.idProducto.id = :idProducto " +
                            "AND ptp.idTipoProducto.id = :idTipoProducto",
                    Long.class
            );
            query.setParameter("idProducto", idProducto);
            query.setParameter("idTipoProducto", idTipoProducto);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
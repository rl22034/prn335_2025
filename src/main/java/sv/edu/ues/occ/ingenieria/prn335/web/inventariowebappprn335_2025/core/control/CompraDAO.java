package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Compra;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * Buscar todas las compras de un proveedor específico
     * @param idProveedor ID del proveedor
     * @return Lista de compras del proveedor, ordenadas por fecha descendente
     */
    public List<Compra> findByProveedor(Integer idProveedor) {
        if (idProveedor == null) {
            return new ArrayList<>();
        }
        try {
            return em.createQuery(
                "SELECT c FROM Compra c WHERE c.proveedor.id = :idProveedor ORDER BY c.fecha DESC",
                Compra.class
            ).setParameter("idProveedor", idProveedor)
             .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Buscar compras por estado
     * @param estado Estado de la compra (PENDIENTE, EN_PROCESO, COMPLETADA, CANCELADA)
     * @return Lista de compras con ese estado, ordenadas por fecha descendente
     */
    public List<Compra> findByEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return em.createQuery(
                "SELECT c FROM Compra c WHERE c.estado = :estado ORDER BY c.fecha DESC",
                Compra.class
            ).setParameter("estado", estado)
             .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}

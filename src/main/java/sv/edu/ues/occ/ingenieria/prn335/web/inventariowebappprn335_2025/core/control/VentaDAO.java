package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Venta;

import java.io.Serializable;
import java.util.ArrayList;
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

    /**
     * Buscar ventas por estado
     * @param estado Estado de la venta (PENDIENTE, DESPACHADA, CANCELADA)
     * @return Lista de ventas con ese estado, ordenadas por fecha descendente
     */
    public List<Venta> findByEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return em.createQuery(
                "SELECT v FROM Venta v WHERE v.estado = :estado ORDER BY v.fecha DESC",
                Venta.class
            ).setParameter("estado", estado)
             .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Buscar ventas excluyendo un estado específico (para ocultar DESPACHADAS de la tabla principal)
     * @param estadoExcluido Estado a excluir (generalmente "DESPACHADA")
     * @param first Primer registro
     * @param pageSize Cantidad de registros
     * @return Lista de ventas que NO tienen ese estado
     */
    public List<Venta> findExcluyendoEstado(String estadoExcluido, int first, int pageSize) {
        if (estadoExcluido == null || estadoExcluido.trim().isEmpty()) {
            return findRange(first, pageSize);
        }
        try {
            return em.createQuery(
                "SELECT v FROM Venta v WHERE v.estado <> :estado ORDER BY v.fecha DESC",
                Venta.class
            ).setParameter("estado", estadoExcluido)
             .setFirstResult(first)
             .setMaxResults(pageSize)
             .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Contar ventas excluyendo un estado específico
     * @param estadoExcluido Estado a excluir
     * @return Cantidad de ventas que NO tienen ese estado
     */
    public Long countExcluyendoEstado(String estadoExcluido) {
        if (estadoExcluido == null || estadoExcluido.trim().isEmpty()) {
            return count();
        }
        try {
            return em.createQuery(
                "SELECT COUNT(v) FROM Venta v WHERE v.estado <> :estado",
                Long.class
            ).setParameter("estado", estadoExcluido)
             .getSingleResult();
        } catch (Exception e) {
            return 0L;
        }
    }
}
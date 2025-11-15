package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.CompraDetalle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Stateless
@LocalBean
public class CompraDetalleDAO extends InventarioDefaultDataAccess<CompraDetalle> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    public CompraDetalleDAO() {
        super(CompraDetalle.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Obtiene todos los detalles de una compra específica
     * @param idCompra ID de la compra
     * @return Lista de detalles de la compra, o lista vacía si no tiene detalles o hay error
     */
    public List<CompraDetalle> getDetallesPorCompra(Long idCompra) {
        if (idCompra == null) {
            return java.util.Collections.emptyList();
        }
        try {
            return getEntityManager()
                    .createQuery("SELECT cd FROM CompraDetalle cd WHERE cd.idCompra.id = :idCompra", CompraDetalle.class)
                    .setParameter("idCompra", idCompra)
                    .getResultList();
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Calcula el total de una compra (suma de cantidad * precio de todos sus detalles)
     * @param idCompra ID de la compra
     * @return Total de la compra, o BigDecimal.ZERO si no tiene detalles o hay error
     */
    public BigDecimal calcularTotalCompra(Long idCompra) {
        if (idCompra == null) {
            return BigDecimal.ZERO;
        }
        try {
            BigDecimal total = getEntityManager().createQuery(
                "SELECT SUM(cd.cantidad * cd.precio) FROM CompraDetalle cd WHERE cd.idCompra.id = :idCompra",
                BigDecimal.class
            ).setParameter("idCompra", idCompra)
             .getSingleResult();
            return total != null ? total : BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
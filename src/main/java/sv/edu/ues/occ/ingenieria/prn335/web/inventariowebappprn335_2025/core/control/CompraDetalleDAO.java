package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.CompraDetalle;

import java.io.Serializable;
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

    public List<CompraDetalle> getDetallesPorCompra(Long idCompra) {
        if (idCompra == null) {
            return java.util.Collections.emptyList();
        }
        try {
            return getEntityManager()
                    // La consulta JPQL usa el nombre de la propiedad en la entidad: idCompra.id
                    .createQuery("SELECT cd FROM CompraDetalle cd WHERE cd.idCompra.id = :idCompra", CompraDetalle.class)
                    .setParameter("idCompra", idCompra)
                    .getResultList();
        } catch (Exception e) {
            // Manejar la excepción (log, etc.)
            return java.util.Collections.emptyList();
        }
    }
}
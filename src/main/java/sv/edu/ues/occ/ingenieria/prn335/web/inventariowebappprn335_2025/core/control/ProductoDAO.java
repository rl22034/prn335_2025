package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Producto;

import java.io.Serializable;
import java.util.List;

@Stateless
@LocalBean
public class ProductoDAO extends InventarioDefaultDataAccess<Producto> implements Serializable {
    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    public ProductoDAO(){super(Producto.class);}

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Producto> findByCompra(Long idCompra) {
        return getEntityManager()
                .createQuery("SELECT cd.idProducto FROM CompraDetalle cd WHERE cd.idCompra.id = :idCompra", Producto.class)
                .setParameter("idCompra", idCompra)
                .getResultList();
    }
}

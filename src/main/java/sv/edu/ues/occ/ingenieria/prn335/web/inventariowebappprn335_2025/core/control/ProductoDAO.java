package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Cliente;
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
    public EntityManager getEntityManager() {return em;}

    @Override
    public void delete(Producto entidad) {
        // Llamamos a la lógica de eliminación que ya está definida en la clase base.
        // El contenedor EJB se asegura de hacer el COMMIT después de que esta línea termine.
        super.delete(entidad);
    }

    @Override
    public void crear(Producto entidad) {
        // Llamamos a la lógica de eliminación que ya está definida en la clase base.
        // El contenedor EJB se asegura de hacer el COMMIT después de que esta línea termine.
        super.crear(entidad);
    }

    @Override
    public  Producto finById(Object id){

        return super.finById(id);
    }

    @Override
    public Producto update(Producto entidad) {
        return super.update(entidad);
    }

    @Override
    public List<Producto> findRange(int first, int pageSize) {
        return super.findRange(first, pageSize);
    }

    @Override
    public Long count() {
        return super.count();
    }

    public List<Producto> findByCompra(Long idCompra) {
        return getEntityManager()
                .createQuery("SELECT cd.idProducto FROM CompraDetalle cd WHERE cd.idCompra.id = :idCompra", Producto.class)
                .setParameter("idCompra", idCompra)
                .getResultList();
    }
}

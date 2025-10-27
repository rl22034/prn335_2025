package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Compra;

import java.io.Serializable;
@Stateless
@LocalBean
public class CompraDAO extends InventarioDefaultDataAccess<Compra> implements Serializable {
    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    public CompraDAO(){super(Compra.class);}

    @Override
    public EntityManager getEntityManager() {return em;}

    @Override
    public void delete(Compra entidad) {
        // Llamamos a la lógica de eliminación que ya está definida en la clase base.
        // El contenedor EJB se asegura de hacer el COMMIT después de que esta línea termine.
        super.delete(entidad);
    }

    @Override
    public void crear(Compra entidad) {
        // Llamamos a la lógica de eliminación que ya está definida en la clase base.
        // El contenedor EJB se asegura de hacer el COMMIT después de que esta línea termine.
        super.crear(entidad);
    }

    @Override
    public  Compra finById(Object id){

        return super.finById(id);

    }

}

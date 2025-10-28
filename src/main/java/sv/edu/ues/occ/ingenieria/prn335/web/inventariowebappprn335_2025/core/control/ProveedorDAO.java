package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Proveedor;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoAlmacen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Stateless
@LocalBean
public class ProveedorDAO extends InventarioDefaultDataAccess<Proveedor> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;
    /**
     * Constructor que especifica la clase de entidad
     */
    public ProveedorDAO() {
        super(Proveedor.class);
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
    public void delete(Proveedor entidad) {
        // Llamamos a la lógica de eliminación que ya está definida en la clase base.
        // El contenedor EJB se asegura de hacer el COMMIT después de que esta línea termine.
        super.delete(entidad);
    }

    @Override
    public void crear(Proveedor entidad) {
        // Llamamos a la lógica de eliminación que ya está definida en la clase base.
        // El contenedor EJB se asegura de hacer el COMMIT después de que esta línea termine.
        super.crear(entidad);
    }

    @Override
    public  Proveedor finById(Object id){

        return super.finById(id);
    }
    public List<Proveedor> findProveedoresActivos() {
        try {
            return em.createQuery(
                    "SELECT p FROM Proveedor p WHERE p.activo = true ORDER BY p.nombre",
                    Proveedor.class
            ).getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

}
package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Caracteristica;

import java.io.Serializable;

@Stateless
@LocalBean
public class CaracteristicaDAO extends InventarioDefaultDataAccess<Caracteristica> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    /**
     * Constructor que especifica la clase de entidad
     */
    public CaracteristicaDAO() {
        super(Caracteristica.class);
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
    public void delete(Caracteristica entidad) {
        // Llamamos a la lógica de eliminación que ya está definida en la clase base.
        // El contenedor EJB se asegura de hacer el COMMIT después de que esta línea termine.
        super.delete(entidad);
    }

    @Override
    public void crear(Caracteristica entidad) {
        // Llamamos a la lógica de creación que ya está definida en la clase base.
        // El contenedor EJB se asegura de hacer el COMMIT después de que esta línea termine.
        super.crear(entidad);
    }

    @Override
    public Caracteristica finById(Object id) {
        return super.finById(id);
    }
}
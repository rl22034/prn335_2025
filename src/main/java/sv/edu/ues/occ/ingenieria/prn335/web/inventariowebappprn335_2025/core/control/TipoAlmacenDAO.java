package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoAlmacen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Stateless
@LocalBean
public class TipoAlmacenDAO extends InventarioDefaultDataAccess<TipoAlmacen> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    public EntityManager em;
    /**
     * Constructor que especifica la clase de entidad
     */
    public TipoAlmacenDAO() {
        super(TipoAlmacen.class);
    }

    /**
     * Implementación requerida del método abstracto
     * Devuelve el EntityManager inyectado
     */
    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<TipoAlmacen> findTiposActivos() {
        try {
            return em.createQuery(
                    "SELECT t FROM TipoAlmacen t WHERE t.activo = true ORDER BY t.nombre",
                    TipoAlmacen.class
            ).getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

}



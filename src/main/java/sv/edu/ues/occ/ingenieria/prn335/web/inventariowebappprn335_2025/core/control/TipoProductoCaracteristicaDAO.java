package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProductoCaracteristica;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Stateless
@LocalBean
public class TipoProductoCaracteristicaDAO extends InventarioDefaultDataAccess<TipoProductoCaracteristica> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    /**
     * Constructor que especifica la clase de entidad
     */
    public TipoProductoCaracteristicaDAO() {
        super(TipoProductoCaracteristica.class);
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
    public void delete(TipoProductoCaracteristica entidad) {
        super.delete(entidad);
    }

    @Override
    public void crear(TipoProductoCaracteristica entidad) {
        super.crear(entidad);
    }

    @Override
    public TipoProductoCaracteristica finById(Object id) {
        return super.finById(id);
    }

    /**
     * Busca todas las características de un tipo de producto específico
     */
    public List<TipoProductoCaracteristica> findByTipoProducto(Long idTipoProducto) {
        try {
            return em.createQuery(
                            "SELECT tpc FROM TipoProductoCaracteristica tpc WHERE tpc.idTipoProducto.id = :idTipoProducto ORDER BY tpc.id",
                            TipoProductoCaracteristica.class)
                    .setParameter("idTipoProducto", idTipoProducto)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
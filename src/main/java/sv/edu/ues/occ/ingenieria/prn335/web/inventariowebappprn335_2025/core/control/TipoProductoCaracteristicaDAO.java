package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProductoCaracteristica; // Ensure this entity exists

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Stateless
@LocalBean
public class TipoProductoCaracteristicaDAO extends InventarioDefaultDataAccess<TipoProductoCaracteristica> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    public TipoProductoCaracteristicaDAO() {
        super(TipoProductoCaracteristica.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    // --- Standard CRUD Overrides ---
    @Override
    public void crear(TipoProductoCaracteristica entidad) {
        super.crear(entidad);
    }

    @Override
    public void delete(TipoProductoCaracteristica entidad) {
        super.delete(entidad);
    }

    @Override
    public TipoProductoCaracteristica finById(Object id) {
        // ID is Bigint according to DDL, usually mapped to Long in Java
        return super.finById(id);
    }

    @Override
    public TipoProductoCaracteristica update(TipoProductoCaracteristica entidad) {
        return super.update(entidad);
    }

    @Override
    public List<TipoProductoCaracteristica> findRange(int first, int pageSize) {
        return super.findRange(first, pageSize);
    }

    @Override
    public Long count() {
        return super.count();
    }

    // --- Custom Query Method ---

    /**
     * Finds all characteristic definitions associated with a specific TipoProducto.
     * This is essential for displaying the characteristics in the TipoProducto screen's detail tab.
     *
     * @param idTipoProducto The ID (Long) of the TipoProducto.
     * @return A list of TipoProductoCaracteristica definitions.
     */
    public List<TipoProductoCaracteristica> findByTipoProducto(Long idTipoProducto) {
        if (idTipoProducto == null) {
            return Collections.emptyList();
        }
        try {
            // JPQL assumes your TipoProductoCaracteristica entity has a field
            // named 'idTipoProducto' which is of type 'TipoProducto'
            // and that TipoProducto's ID field is named 'id'.
            // Example in TipoProductoCaracteristica.java:
            // @ManyToOne @JoinColumn(name="id_tipo_producto")
            // private TipoProducto idTipoProducto;

             return getEntityManager()
                    .createQuery(
                            "SELECT tpc FROM TipoProductoCaracteristica tpc " +
                                    "LEFT JOIN FETCH tpc.idCaracteristica c " +
                                    "LEFT JOIN FETCH c.idTipoUnidadMedida " +
                                    "WHERE tpc.idTipoProducto.id = :idTipo " +
                                    "ORDER BY tpc.id",
                            TipoProductoCaracteristica.class)
                    .setParameter("idTipo", idTipoProducto)
                    .getResultList();

        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            return Collections.emptyList();
        }
    }
}
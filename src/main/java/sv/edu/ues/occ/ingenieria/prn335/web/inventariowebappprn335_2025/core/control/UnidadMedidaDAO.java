package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.UnidadMedida;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Stateless
@LocalBean
public class UnidadMedidaDAO extends InventarioDefaultDataAccess<UnidadMedida> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    /**
     * Constructor que especifica la clase de entidad
     */
    public UnidadMedidaDAO() {
        super(UnidadMedida.class);
    }

    /**
     * Implementación requerida del método abstracto
     * Devuelve el EntityManager inyectado
     */
    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Sobrescribe findRange para hacer JOIN FETCH con TipoUnidadMedida
     * Esto evita LazyInitializationException al mostrar los datos en la tabla
     */
    @Override
    public List<UnidadMedida> findRange(int first, int max) {
        try {
            TypedQuery<UnidadMedida> query = em.createQuery(
                    "SELECT um FROM UnidadMedida um " +
                    "LEFT JOIN FETCH um.idTipoUnidadMedida " +
                    "ORDER BY um.id ASC",
                    UnidadMedida.class
            );
            query.setFirstResult(first);
            query.setMaxResults(max);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Cuenta las UnidadMedida de un tipo específico
     * @param idTipoUnidadMedida ID del tipo de unidad de medida
     * @return Cantidad de registros
     */
    public Long countByTipoUnidadMedida(Integer idTipoUnidadMedida) {
        if (idTipoUnidadMedida == null) {
            return 0L;
        }
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(um) FROM UnidadMedida um " +
                            "WHERE um.idTipoUnidadMedida.id = :idTipo",
                    Long.class
            );
            query.setParameter("idTipo", idTipoUnidadMedida);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * Busca UnidadMedida de un tipo específico con paginación
     * @param idTipoUnidadMedida ID del tipo de unidad de medida
     * @param first Primer registro
     * @param max Máximo de registros
     * @return Lista paginada de UnidadMedida
     */
    public List<UnidadMedida> findByTipoUnidadMedida(Integer idTipoUnidadMedida, int first, int max) {
        if (idTipoUnidadMedida == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<UnidadMedida> query = em.createQuery(
                    "SELECT um FROM UnidadMedida um " +
                            "WHERE um.idTipoUnidadMedida.id = :idTipo " +
                            "ORDER BY um.id",
                    UnidadMedida.class
            );
            query.setParameter("idTipo", idTipoUnidadMedida);
            query.setFirstResult(first);
            query.setMaxResults(max);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

// ... dentro de tu clase UnidadMedidaDAO ...

    public List<UnidadMedida> getUnidadesPorTipoUnidadMedida(Integer id) {
        if (id == null) {
            return Collections.emptyList();
        }
        try {
            // Esta consulta JPQL asume que en tu entidad 'UnidadMedida.java'
            // tienes una propiedad llamada 'idTipoUnidadMedida' que es un objeto de tipo 'TipoUnidadMedida',
            // y que el ID de 'TipoUnidadMedida' se llama 'id'.

            // Ejemplo de cómo debería ser el campo en UnidadMedida.java:
            // @ManyToOne
            // @JoinColumn(name="id_tipo_unidad_medida")
            // private TipoUnidadMedida idTipoUnidadMedida;

            return getEntityManager()
                    .createQuery("SELECT um FROM UnidadMedida um WHERE um.idTipoUnidadMedida.id = :idTipo", UnidadMedida.class)
                    .setParameter("idTipo", id)
                    .getResultList();
        } catch (Exception e) {
            // Es buena práctica loguear el error
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
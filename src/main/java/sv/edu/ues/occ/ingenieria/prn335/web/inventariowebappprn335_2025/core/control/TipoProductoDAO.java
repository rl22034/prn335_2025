package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProducto;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@LocalBean
public class TipoProductoDAO extends InventarioDefaultDataAccess<TipoProducto> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    public TipoProductoDAO() {
        super(TipoProducto.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    // ⭐ SOBRESCRIBIR findRange para cargar la relación padre
    @Override
    public List<TipoProducto> findRange(int first, int max) {
        try {
            TypedQuery<TipoProducto> query = em.createQuery(
                    "SELECT DISTINCT tp FROM TipoProducto tp " +
                            "LEFT JOIN FETCH tp.idTipoProductoPadre " +
                            "ORDER BY tp.id",
                    TipoProducto.class
            );
            query.setFirstResult(first);
            query.setMaxResults(max);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener tipos de producto", e);
        }
    }

    @Override
    public void delete(TipoProducto entidad) {
        super.delete(entidad);
    }

    @Override
    public void crear(TipoProducto entidad) {
        super.crear(entidad);
    }

    @Override
    public TipoProducto finById(Object id) {
        return super.finById(id);
    }

    public List<TipoProducto> findHijos(Long idPadre) {
        if (idPadre == null) {
            return Collections.emptyList();
        }
        try {
            // Asume que la entidad TipoProducto tiene un campo 'idTipoProductoPadre' de tipo TipoProducto
            // y que el ID de TipoProducto se llama 'id'.
            // Ejemplo:
            // @ManyToOne @JoinColumn(name="id_tipo_producto_padre")
            // private TipoProducto idTipoProductoPadre;

            return getEntityManager()
                    .createQuery("SELECT tp FROM TipoProducto tp WHERE tp.idTipoProductoPadre.id = :idPadre", TipoProducto.class)
                    .setParameter("idPadre", idPadre)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace(); // Log del error
            return Collections.emptyList();
        }
    }

    /**
     * Obtiene todos los TipoProducto válidos para ser padre del nodo especificado.
     * Excluye el nodo mismo y todos sus descendientes (hijos, nietos, etc.)
     *
     * @param idActual ID del TipoProducto que se está editando
     * @return Lista de TipoProducto que pueden ser padre del nodo actual
     */
    public List<TipoProducto> findValidosParaPadre(Long idActual) {
        if (idActual == null) {
            // Si es nuevo, todos son válidos
            return findRange(0, 1000);
        }

        try {
            // Obtener todos los tipos de producto
            List<TipoProducto> todos = findRange(0, 1000);

            // Obtener el nodo actual
            TipoProducto nodoActual = finById(idActual);
            if (nodoActual == null) {
                return todos;
            }

            // Obtener todos los descendientes del nodo actual (recursivamente)
            Set<Long> idsExcluir = new HashSet<>();
            idsExcluir.add(idActual); // Excluir el nodo mismo
            obtenerDescendientesRecursivo(idActual, idsExcluir, todos);

            // Filtrar la lista excluyendo el nodo actual y sus descendientes
            return todos.stream()
                    .filter(tp -> !idsExcluir.contains(tp.getId()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Método auxiliar recursivo para obtener todos los descendientes de un nodo
     *
     * @param idNodo ID del nodo padre
     * @param idsDescendientes Set donde se acumulan los IDs de los descendientes
     * @param todos Lista completa de todos los TipoProducto
     */
    private void obtenerDescendientesRecursivo(Long idNodo, Set<Long> idsDescendientes, List<TipoProducto> todos) {
        // Buscar todos los hijos directos del nodo actual
        List<TipoProducto> hijos = todos.stream()
                .filter(tp -> tp.getIdTipoProductoPadre() != null &&
                        tp.getIdTipoProductoPadre().getId().equals(idNodo))
                .collect(Collectors.toList());

        // Para cada hijo, agregarlo a la lista de excluidos y buscar sus descendientes
        for (TipoProducto hijo : hijos) {
            idsDescendientes.add(hijo.getId());
            // Llamada recursiva para obtener los descendientes del hijo
            obtenerDescendientesRecursivo(hijo.getId(), idsDescendientes, todos);
        }
    }
}

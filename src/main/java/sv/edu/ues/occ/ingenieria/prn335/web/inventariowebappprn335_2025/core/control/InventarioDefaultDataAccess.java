package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

import java.util.List;

public abstract class InventarioDefaultDataAccess<T> implements InventarioDAOInterface<T> {

    // Campo final para la clase de entidad
    final Class<T> entityClass;         //Esta línea crea una variable llamada entityClass que guardará el tipo de entidad con la que el DAO trabajará.

    /*
     * constructor
     * */
    public InventarioDefaultDataAccess(Class<T> entityClass) {

        this.entityClass = entityClass;
    }

    public abstract EntityManager getEntityManager();

    // ESTE debe ser concreto y devolver la clase
    public Class<T> getEntityClass() {
        return entityClass;
    }


    //  =================== AQUI VAN TODOS LOS METODOS CRUD O CUALQUIERA REUTLIZABLE ===================

    @Override
    public void crear(T registro) {
        if (registro == null) {
            throw new IllegalArgumentException("El registro no puede ser nulo");
        }

        EntityManager em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("em nulo");
        }
        try {
            em.persist(registro);
        } catch (Exception ex) {
            throw new RuntimeException("No se puede crear la entidad de registro", ex);

        }
    }

    @Override
    public T finById(Object id) {
        if (id == null){
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        EntityManager em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("EntityManager no disponible");
        }
        try {

            return em.find(entityClass, id);
        }catch (Exception e){
            throw new RuntimeException("Error al obtener la entidad");

        }
    }

    @Override
    public T update (T entidad)  {
        if (entidad == null) {
            throw new IllegalArgumentException("EntityManager no disponible");
        }

        EntityManager em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("EntityManager no disponible");
        }
        try {
            T actualizado = em.merge(entidad);
            return actualizado;
        } catch (Exception ex) {
            throw new RuntimeException("Error al actualizar la entidad: " + ex.getMessage(), ex);

        }
    }


   public void delete(T entidad) {

        if (entidad == null) {
            throw new IllegalArgumentException("La entidad no puede ser null");
        }


        EntityManager em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("EntityManager no disponible");
        }

        try {
            // ========== LÓGICA DE ELIMINACIÓN ==========
            if (!em.contains(entidad)) {
                // CASO 1: Entidad NO está gestionada (detached)
                // Primero debemos fusionarla para que esté gestionada
                entidad = em.merge(entidad);
            }

            // CASO 2: Ahora la entidad está gestionada (managed)
            // Podemos eliminarla
            em.remove(entidad);

        } catch (Exception ex) {
            throw new RuntimeException("Error al eliminar entidad: " + ex.getMessage(), ex);
        }
   }

    @Override
    public List<T> findRange(int first, int max) throws IllegalArgumentException {
        if(first< 0 || max<1){
            throw new IllegalArgumentException("Los parametros first y max deben ser mayores que cero");
        }
        try{
            EntityManager em = getEntityManager();
            if(em != null){
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<T> cq = cb.createQuery(entityClass);
                Root<T> root = cq.from(entityClass);
                CriteriaQuery<T> all = cq.select(root);
                TypedQuery<T> allQuery = em.createQuery(all);
                allQuery.setFirstResult(first);
                allQuery.setMaxResults(max);
                return allQuery.getResultList();
            }
        }catch(Exception e){
            throw new IllegalStateException("Error al obtener el rango de registros", e);
        }
        throw new IllegalArgumentException("No se puede obtener los registros");
    }

    @Override
    public Long count () {

        EntityManager em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("EntityManager no disponible");
        }
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            // me devuelve un numero largo no un objeto
            CriteriaQuery cq = cb.createQuery(Long.class);
            Root<T> rootEntity = cq.from(entityClass);

            /*
             * Le dice al traductor: "No me traigas los productos, solo cuenta cuántos hay"
             * ¿Qué es cb.count()? La función que cuenta registros
             * ¿Qué es .select()? Le dice "esto es lo que quiero obtener"
             * En SQL sería como: SELECT COUNT(*) FROM productos
             */
            CriteriaQuery<Long> all = cq.select(cb.count(rootEntity));

            /*
             * Es la consulta "preparada" y lista para enviar a la base de datos
             * ¿Por qué <Long>? Porque sabemos que nos va a devolver un número
             * ¿Qué hace createQuery()? Prepara la consulta para ser ejecutada
             */
            TypedQuery<Long> allQuery = em.createQuery(all);

            /*// Lógica para contar usando la API de Criteria
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<T> rt = cq.from(entityClass);
            cq.select(cb.count(rt));
            TypedQuery<Long> q = em.createQuery(cq);
            return q.getSingleResult();*/
            return ((Long) allQuery.getSingleResult());

        } catch (Exception ex) {
            throw new RuntimeException("Error en los parámetros de la consulta: " + ex.getMessage(), ex);

        }
    }

}
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
    public List<T> findRange(int first, int max) {
        if(first < 0 || max < 1){
            throw new IllegalArgumentException("Tienes un error con los valores first y max");
        }

        EntityManager em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("EntityManager no disponible");
        }

        try {

                // Crea un "asistente" para construir consultas sin escribir SQL, cb es una variable que guarda esta herramienta
                CriteriaBuilder cb = em.getCriteriaBuilder();

                // Crea una "consulta vacía" específica para tu tipo de entidad ejmplo
                // CriteriaQuery cq = cb.createQuery(almacen);
                CriteriaQuery cq = cb.createQuery(entityClass);

                // Define de qué "tabla" vamos a sacar los datos
                //En SQL sería: La parte FROM productos en una consulta
                Root<T> rootEntity = cq.from(entityClass);

                // Dice "quiero todos los campos de esta tabla"
                // En SQL sería: SELECT * FROM productos
                //orderBy(cb.asc(rootEntity.get("id"))) Esto ordena los resultados por ID de forma ascendente (1, 2, 3, 4...).
                CriteriaQuery<T> all = cq.select(rootEntity).orderBy(cb.asc(rootEntity.get("id")));

                /*
                * Esta línea convierte tu consulta "genérica" (all) en una consulta lista para ser ejecutada.
                * El em.createQuery() crea un objeto TypedQuery, que es la consulta final que la base de datos entiende.
                */
                TypedQuery<T> allQuery = em.createQuery(all);
                allQuery.setFirstResult(first);

                /*
                * es el método correcto para decirle a la consulta cuántos resultados máximos debe traer.
                * Esto es esencial para la paginación.
                * */
                allQuery.setMaxResults(max);
                return allQuery.getResultList();


        }catch (Exception ex){
            throw new RuntimeException("No se puede acceder al repositorio", ex);
        }

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
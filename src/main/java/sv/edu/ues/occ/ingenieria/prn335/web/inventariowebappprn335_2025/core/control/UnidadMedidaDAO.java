package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    @Override
    public void delete(UnidadMedida entidad) {
        // Llamamos a la lógica de eliminación que ya está definida en la clase base.
        // El contenedor EJB se asegura de hacer el COMMIT después de que esta línea termine.
        super.delete(entidad);
    }

    @Override
    public void crear(UnidadMedida entidad) {
        // Llamamos a la lógica de creación que ya está definida en la clase base.
        // El contenedor EJB se asegura de hacer el COMMIT después de que esta línea termine.
        super.crear(entidad);
    }

    @Override
    public UnidadMedida finById(Object id) {
        return super.finById(id);
    }

    public List<UnidadMedida> getUnidadesPorTipoUnidadMedida(Integer idTipoUnidadMedida) {
        if (idTipoUnidadMedida == null) {
            return Collections.emptyList();
        }
        try {
            // Esta consulta JPQL asume que en tu entidad UnidadMedida.java
            // tienes una propiedad llamada "idTipoUnidadMedida" que es de tipo "TipoUnidadMedida"
            // y que el ID de esa entidad (TipoUnidadMedida) se llama "id".
            //
            // Ejemplo de la entidad UnidadMedida.java:
            // @ManyToOne
            // @JoinColumn(name="id_tipo_unidad_medida")
            // private TipoUnidadMedida idTipoUnidadMedida;

            return getEntityManager()
                    .createQuery("SELECT um FROM UnidadMedida um WHERE um.idTipoUnidadMedida.id = :idTipo", UnidadMedida.class)
                    .setParameter("idTipo", idTipoUnidadMedida)
                    .getResultList();
        } catch (Exception e) {
            // Opcional: loguear el error
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}

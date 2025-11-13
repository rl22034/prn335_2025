package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.VentaDetalle;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID; // ¡Importante! El ID de Venta es UUID

@Stateless
@LocalBean
public class VentaDetalleDAO extends InventarioDefaultDataAccess<VentaDetalle> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    public VentaDetalleDAO() {
        super(VentaDetalle.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Método ESENCIAL para la lógica Maestro-Detalle.
     * Busca todos los VentaDetalle asociados a un ID de Venta (UUID).
     * * @param idVenta El UUID de la Venta (Maestro).
     * @return Una lista de detalles de venta.
     */
    public List<VentaDetalle> getDetallesPorVenta(UUID idVenta) {
        if (idVenta == null) {
            return Collections.emptyList();
        }
        try {
            // Esta consulta JPQL asume que en tu entidad VentaDetalle.java
            // tienes una propiedad llamada "idVenta" que es de tipo "Venta"
            // y que el ID de esa entidad Venta se llama "id".
            // Ejemplo: private Venta idVenta;

            return getEntityManager()
                    .createQuery("SELECT vd FROM VentaDetalle vd WHERE vd.idVenta.id = :idVenta", VentaDetalle.class)
                    .setParameter("idVenta", idVenta)
                    .getResultList();
        } catch (Exception e) {
            // Opcional: loguear el error
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
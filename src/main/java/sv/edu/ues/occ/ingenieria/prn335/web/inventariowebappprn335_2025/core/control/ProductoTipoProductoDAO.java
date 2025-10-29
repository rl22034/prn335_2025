package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.ProductoTipoProducto;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Stateless
@LocalBean
public class ProductoTipoProductoDAO extends InventarioDefaultDataAccess<ProductoTipoProducto> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    public ProductoTipoProductoDAO() {
        super(ProductoTipoProducto.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void delete(ProductoTipoProducto entidad) {
        if (entidad == null) {
            throw new IllegalArgumentException("La entidad no puede ser null");
        }

        EntityManager em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("EntityManager no disponible");
        }

        try {
            // Usar SQL nativo con CAST para UUID
            em.createNativeQuery(
                            "DELETE FROM public.producto_tipo_producto WHERE id_producto_tipo_producto = CAST(:id AS uuid)"
                    )
                    .setParameter("id", entidad.getId().toString())
                    .executeUpdate();
        } catch (Exception ex) {
            throw new RuntimeException("Error al eliminar ProductoTipoProducto: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void crear(ProductoTipoProducto entidad) {
        if (entidad == null) {
            throw new IllegalArgumentException("La entidad no puede ser null");
        }

        EntityManager em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("EntityManager no disponible");
        }

        try {
            // Usar SQL nativo con CAST para UUID
            em.createNativeQuery(
                            "INSERT INTO public.producto_tipo_producto " +
                                    "(id_producto_tipo_producto, activo, fecha_creacion, observaciones, id_producto, id_tipo_producto) " +
                                    "VALUES (CAST(:id AS uuid), :activo, :fechaCreacion, :observaciones, CAST(:idProducto AS uuid), :idTipoProducto)"
                    )
                    .setParameter("id", entidad.getId().toString())
                    .setParameter("activo", entidad.getActivo())
                    .setParameter("fechaCreacion", entidad.getFechaCreacion())
                    .setParameter("observaciones", entidad.getObservaciones())
                    .setParameter("idProducto", entidad.getIdProducto().getId().toString())
                    .setParameter("idTipoProducto", entidad.getIdTipoProducto().getId())
                    .executeUpdate();
        } catch (Exception ex) {
            throw new RuntimeException("Error al crear ProductoTipoProducto: " + ex.getMessage(), ex);
        }
    }

    @Override
    public ProductoTipoProducto finById(Object id) {
        return super.finById(id);
    }

    /**
     * Busca todos los TipoProducto asignados a un Producto específico
     * @param idProducto ID del producto (UUID)
     * @return Lista de ProductoTipoProducto
     */
    public List<ProductoTipoProducto> findByProducto(UUID idProducto) {
        try {
            // Usar SQL nativo con CAST para manejar UUID correctamente
            List<Object[]> resultados = em.createNativeQuery(
                            "SELECT ptp.id_producto_tipo_producto, ptp.activo, ptp.fecha_creacion, ptp.observaciones, " +
                                    "ptp.id_producto, ptp.id_tipo_producto " +
                                    "FROM public.producto_tipo_producto ptp " +
                                    "WHERE ptp.id_producto = CAST(:idProducto AS uuid) " +
                                    "ORDER BY ptp.fecha_creacion DESC"
                    )
                    .setParameter("idProducto", idProducto.toString())
                    .getResultList();

            // Convertir resultados a entidades
            List<ProductoTipoProducto> lista = new ArrayList<>();
            for (Object[] row : resultados) {
                ProductoTipoProducto ptp = new ProductoTipoProducto();
                ptp.setId(UUID.fromString(row[0].toString()));
                ptp.setActivo((Boolean) row[1]);
                ptp.setFechaCreacion((OffsetDateTime) row[2]);
                ptp.setObservaciones((String) row[3]);
                // Cargar relaciones si es necesario
                lista.add(ptp);
            }
            return lista;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Busca un ProductoTipoProducto específico por producto y tipo
     * @param idProducto ID del producto (UUID)
     * @param idTipoProducto ID del tipo de producto (Long)
     * @return ProductoTipoProducto o null si no existe
     */
    public ProductoTipoProducto findByProductoAndTipo(UUID idProducto, Long idTipoProducto) {
        try {
            List<Object[]> resultados = em.createNativeQuery(
                            "SELECT ptp.id_producto_tipo_producto, ptp.activo, ptp.fecha_creacion, ptp.observaciones, " +
                                    "ptp.id_producto, ptp.id_tipo_producto " +
                                    "FROM public.producto_tipo_producto ptp " +
                                    "WHERE ptp.id_producto = CAST(:idProducto AS uuid) " +
                                    "AND ptp.id_tipo_producto = :idTipoProducto"
                    )
                    .setParameter("idProducto", idProducto.toString())
                    .setParameter("idTipoProducto", idTipoProducto)
                    .getResultList();

            if (resultados.isEmpty()) {
                return null;
            }

            Object[] row = resultados.get(0);
            ProductoTipoProducto ptp = new ProductoTipoProducto();
            ptp.setId(UUID.fromString(row[0].toString()));
            ptp.setActivo((Boolean) row[1]);
            ptp.setFechaCreacion((OffsetDateTime) row[2]);
            ptp.setObservaciones((String) row[3]);

            return ptp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Verifica si ya existe una relación entre producto y tipo
     * @param idProducto ID del producto (UUID)
     * @param idTipoProducto ID del tipo de producto (Long)
     * @return true si existe, false si no
     */
    public boolean existeRelacion(UUID idProducto, Long idTipoProducto) {
        try {
            Long count = ((Number) em.createNativeQuery(
                            "SELECT COUNT(*) FROM public.producto_tipo_producto " +
                                    "WHERE id_producto = CAST(:idProducto AS uuid) " +
                                    "AND id_tipo_producto = :idTipoProducto"
                    )
                    .setParameter("idProducto", idProducto.toString())
                    .setParameter("idTipoProducto", idTipoProducto)
                    .getSingleResult()).longValue();

            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Kardex;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Stateless
@LocalBean
public class KardexDAO extends InventarioDefaultDataAccess<Kardex> implements Serializable {

    @PersistenceContext(unitName = "inventarioPU")
    private EntityManager em;

    public KardexDAO() {
        super(Kardex.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Obtiene la cantidad actual del último movimiento de un producto.
     * Si no hay movimientos previos, retorna ZERO.
     */
    public BigDecimal obtenerCantidadActual(UUID idProducto) {
        try {
            List<BigDecimal> resultados = em.createQuery(
                    "SELECT k.cantidadActual FROM Kardex k WHERE k.idProducto.id = :idProducto ORDER BY k.fecha DESC",
                    BigDecimal.class)
                    .setParameter("idProducto", idProducto)
                    .setMaxResults(1)
                    .getResultList();

            if (resultados.isEmpty()) {
                return BigDecimal.ZERO;
            }
            return resultados.get(0);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Crea un registro Kardex de ENTRADA (compra) usando SQL nativo.
     */
    public void crearKardexEntrada(UUID idKardex, UUID idProducto,
                                    BigDecimal cantidad, BigDecimal precio,
                                    BigDecimal cantidadActual, BigDecimal precioActual,
                                    UUID idCompraDetalle, String observaciones, Integer idAlmacen) {
        String sql = "INSERT INTO kardex (id_kardex, id_producto, fecha, tipo_movimiento, " +
                     "cantidad, precio, cantidad_actual, precio_actual, " +
                     "id_compra_detalle, id_venta_detalle, referencia_externa, observaciones, id_almacen) " +
                     "VALUES (CAST(?1 AS uuid), CAST(?2 AS uuid), NOW(), 'ENTRADA', " +
                     "CAST(?3 AS numeric), CAST(?4 AS numeric), CAST(?5 AS numeric), CAST(?6 AS numeric), " +
                     "CAST(?7 AS uuid), NULL, NULL, ?8, ?9)";

        em.createNativeQuery(sql)
                .setParameter(1, idKardex.toString())
                .setParameter(2, idProducto.toString())
                .setParameter(3, cantidad)
                .setParameter(4, precio)
                .setParameter(5, cantidadActual)
                .setParameter(6, precioActual)
                .setParameter(7, idCompraDetalle.toString())
                .setParameter(8, observaciones)
                .setParameter(9, idAlmacen)
                .executeUpdate();
    }

    /**
     * Crea un registro Kardex de SALIDA (venta) usando SQL nativo.
     */
    public void crearKardexSalida(UUID idKardex, UUID idProducto,
                                   BigDecimal cantidad, BigDecimal precio,
                                   BigDecimal cantidadActual, BigDecimal precioActual,
                                   UUID idVentaDetalle, String observaciones, Integer idAlmacen) {
        String sql = "INSERT INTO kardex (id_kardex, id_producto, fecha, tipo_movimiento, " +
                     "cantidad, precio, cantidad_actual, precio_actual, " +
                     "id_compra_detalle, id_venta_detalle, referencia_externa, observaciones, id_almacen) " +
                     "VALUES (CAST(?1 AS uuid), CAST(?2 AS uuid), NOW(), 'SALIDA', " +
                     "CAST(?3 AS numeric), CAST(?4 AS numeric), CAST(?5 AS numeric), CAST(?6 AS numeric), " +
                     "NULL, CAST(?7 AS uuid), NULL, ?8, ?9)";

        em.createNativeQuery(sql)
                .setParameter(1, idKardex.toString())
                .setParameter(2, idProducto.toString())
                .setParameter(3, cantidad)
                .setParameter(4, precio)
                .setParameter(5, cantidadActual)
                .setParameter(6, precioActual)
                .setParameter(7, idVentaDetalle.toString())
                .setParameter(8, observaciones)
                .setParameter(9, idAlmacen)
                .executeUpdate();
    }
}

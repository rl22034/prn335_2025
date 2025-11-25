package core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.KardexDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Kardex;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KardexDAOTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<BigDecimal> typedQuery;

    @Mock
    private Query nativeQuery;

    private KardexDAO kardexDAO;

    @BeforeEach
    void setUp() throws Exception {
        kardexDAO = new KardexDAO();
        Field emField = KardexDAO.class.getDeclaredField("em");
        emField.setAccessible(true);
        emField.set(kardexDAO, entityManager);
    }

    // ========== Tests para getEntityManager ==========

    @Test
    void getEntityManager_debeRetornarEntityManager() {
        assertEquals(entityManager, kardexDAO.getEntityManager());
    }

    // ========== Tests para obtenerCantidadActual ==========

    @Test
    void obtenerCantidadActual_conMovimientosPrevios_debeRetornarUltimaCantidad() {
        UUID idProducto = UUID.randomUUID();
        BigDecimal cantidadEsperada = new BigDecimal("50.00");

        when(entityManager.createQuery(anyString(), eq(BigDecimal.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("idProducto"), eq(idProducto))).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(cantidadEsperada));

        BigDecimal resultado = kardexDAO.obtenerCantidadActual(idProducto);

        assertEquals(cantidadEsperada, resultado);
    }

    @Test
    void obtenerCantidadActual_sinMovimientosPrevios_debeRetornarCero() {
        UUID idProducto = UUID.randomUUID();

        when(entityManager.createQuery(anyString(), eq(BigDecimal.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("idProducto"), eq(idProducto))).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(1)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>());

        BigDecimal resultado = kardexDAO.obtenerCantidadActual(idProducto);

        assertEquals(BigDecimal.ZERO, resultado);
    }

    @Test
    void obtenerCantidadActual_conExcepcion_debeRetornarCero() {
        UUID idProducto = UUID.randomUUID();

        when(entityManager.createQuery(anyString(), eq(BigDecimal.class))).thenThrow(new RuntimeException("Error"));

        BigDecimal resultado = kardexDAO.obtenerCantidadActual(idProducto);

        assertEquals(BigDecimal.ZERO, resultado);
    }

    // ========== Tests para crearKardexEntrada ==========

    @Test
    void crearKardexEntrada_debeEjecutarInsertConParametrosCorrectos() {
        UUID idKardex = UUID.randomUUID();
        UUID idProducto = UUID.randomUUID();
        BigDecimal cantidad = new BigDecimal("10");
        BigDecimal precio = new BigDecimal("5.00");
        BigDecimal cantidadActual = new BigDecimal("100");
        BigDecimal precioActual = new BigDecimal("5.00");
        UUID idCompraDetalle = UUID.randomUUID();
        String observaciones = "Test compra";
        Integer idAlmacen = 1;

        when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.setParameter(anyInt(), any())).thenReturn(nativeQuery);
        when(nativeQuery.executeUpdate()).thenReturn(1);

        kardexDAO.crearKardexEntrada(idKardex, idProducto, cantidad, precio,
                cantidadActual, precioActual, idCompraDetalle, observaciones, idAlmacen);

        verify(entityManager).createNativeQuery(contains("INSERT INTO kardex"));
        verify(nativeQuery).setParameter(1, idKardex.toString());
        verify(nativeQuery).setParameter(2, idProducto.toString());
        verify(nativeQuery).setParameter(3, cantidad);
        verify(nativeQuery).setParameter(4, precio);
        verify(nativeQuery).setParameter(5, cantidadActual);
        verify(nativeQuery).setParameter(6, precioActual);
        verify(nativeQuery).setParameter(7, idCompraDetalle.toString());
        verify(nativeQuery).setParameter(8, observaciones);
        verify(nativeQuery).setParameter(9, idAlmacen);
        verify(nativeQuery).executeUpdate();
    }

    @Test
    void crearKardexEntrada_conAlmacenNull_debePermitirNull() {
        UUID idKardex = UUID.randomUUID();
        UUID idProducto = UUID.randomUUID();
        UUID idCompraDetalle = UUID.randomUUID();

        when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.setParameter(anyInt(), any())).thenReturn(nativeQuery);
        when(nativeQuery.executeUpdate()).thenReturn(1);

        kardexDAO.crearKardexEntrada(idKardex, idProducto, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE, idCompraDetalle, "Test", null);

        verify(nativeQuery).setParameter(9, null);
        verify(nativeQuery).executeUpdate();
    }

    // ========== Tests para crearKardexSalida ==========

    @Test
    void crearKardexSalida_debeEjecutarInsertConParametrosCorrectos() {
        UUID idKardex = UUID.randomUUID();
        UUID idProducto = UUID.randomUUID();
        BigDecimal cantidad = new BigDecimal("5");
        BigDecimal precio = new BigDecimal("10.00");
        BigDecimal cantidadActual = new BigDecimal("95");
        BigDecimal precioActual = new BigDecimal("10.00");
        UUID idVentaDetalle = UUID.randomUUID();
        String observaciones = "Test venta";
        Integer idAlmacen = 2;

        when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.setParameter(anyInt(), any())).thenReturn(nativeQuery);
        when(nativeQuery.executeUpdate()).thenReturn(1);

        kardexDAO.crearKardexSalida(idKardex, idProducto, cantidad, precio,
                cantidadActual, precioActual, idVentaDetalle, observaciones, idAlmacen);

        verify(entityManager).createNativeQuery(contains("INSERT INTO kardex"));
        verify(nativeQuery).setParameter(1, idKardex.toString());
        verify(nativeQuery).setParameter(2, idProducto.toString());
        verify(nativeQuery).setParameter(3, cantidad);
        verify(nativeQuery).setParameter(4, precio);
        verify(nativeQuery).setParameter(5, cantidadActual);
        verify(nativeQuery).setParameter(6, precioActual);
        verify(nativeQuery).setParameter(7, idVentaDetalle.toString());
        verify(nativeQuery).setParameter(8, observaciones);
        verify(nativeQuery).setParameter(9, idAlmacen);
        verify(nativeQuery).executeUpdate();
    }

    @Test
    void crearKardexSalida_conAlmacenNull_debePermitirNull() {
        UUID idKardex = UUID.randomUUID();
        UUID idProducto = UUID.randomUUID();
        UUID idVentaDetalle = UUID.randomUUID();

        when(entityManager.createNativeQuery(anyString())).thenReturn(nativeQuery);
        when(nativeQuery.setParameter(anyInt(), any())).thenReturn(nativeQuery);
        when(nativeQuery.executeUpdate()).thenReturn(1);

        kardexDAO.crearKardexSalida(idKardex, idProducto, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE, idVentaDetalle, "Test", null);

        verify(nativeQuery).setParameter(9, null);
        verify(nativeQuery).executeUpdate();
    }

    // ========== Tests para métodos heredados de InventarioDefaultDataAccess ==========

    @Test
    void crear_debeUsarEntityManagerPersist() {
        Kardex kardex = new Kardex();
        kardex.setTipoMovimiento("ENTRADA");

        kardexDAO.crear(kardex);

        verify(entityManager).persist(kardex);
    }

    @Test
    void findById_debeUsarEntityManagerFind() {
        UUID id = UUID.randomUUID();
        Kardex kardex = new Kardex();
        kardex.setId(id);

        when(entityManager.find(Kardex.class, id)).thenReturn(kardex);

        Kardex resultado = kardexDAO.finById(id);

        assertEquals(kardex, resultado);
        verify(entityManager).find(Kardex.class, id);
    }

    @Test
    void update_debeUsarEntityManagerMerge() {
        Kardex kardex = new Kardex();
        kardex.setId(UUID.randomUUID());

        kardexDAO.update(kardex);

        verify(entityManager).merge(kardex);
    }

    @Test
    void delete_debeUsarEntityManagerRemove() {
        UUID id = UUID.randomUUID();
        Kardex kardex = new Kardex();
        kardex.setId(id);

        when(entityManager.merge(kardex)).thenReturn(kardex);

        kardexDAO.delete(kardex);

        verify(entityManager).merge(kardex);
        verify(entityManager).remove(kardex);
    }
}

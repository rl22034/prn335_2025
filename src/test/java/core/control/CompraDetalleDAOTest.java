package core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CompraDetalleDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Compra;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.CompraDetalle;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// Usamos Strictness.LENIENT para evitar UnnecessaryStubbingException.
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class CompraDetalleDAOTest {

    private static final Long TEST_COMPRA_ID = 50L;
    private static final UUID TEST_ID = UUID.randomUUID();

    // Mocks para JPA/Criteria API (Heredados)
    @Mock
    EntityManager mockEm;
    @Mock
    CriteriaBuilder mockCb;
    @Mock
    CriteriaQuery<CompraDetalle> mockCqDetalle;
    @Mock
    CriteriaQuery<Long> mockCqLong;
    @Mock
    Root<CompraDetalle> mockRoot;
    @Mock
    TypedQuery<CompraDetalle> mockTypedQueryDetalle;
    @Mock
    TypedQuery<Long> mockTypedQueryLong;

    // Mocks específicos para consultas JPQL personalizadas
    @Mock
    TypedQuery<CompraDetalle> mockQueryDetallesPorCompra;
    @Mock
    TypedQuery<BigDecimal> mockQueryTotalCompra;

    // Spy e Inyección
    @Spy
    @InjectMocks
    CompraDetalleDAO compraDetalleDAO;

    CompraDetalle entidad;
    Compra compra;
    List<CompraDetalle> expectedList;

    @BeforeEach
    void setUp() {
        // Inicialización de la entidad de prueba
        compra = new Compra();
        compra.setId(TEST_COMPRA_ID);

        entidad = new CompraDetalle();
        entidad.setId(TEST_ID);
        entidad.setIdCompra(compra);
        entidad.setCantidad(new BigDecimal("10"));
        entidad.setPrecio(new BigDecimal("5.50"));

        expectedList = Arrays.asList(entidad);

        // Configuración de GetEntityManager
        doReturn(mockEm).when(compraDetalleDAO).getEntityManager();

        // ===========================================
        // 1. Configuraciones para FindRange y Count (Heredados)
        // ===========================================
        when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);

        // FindRange
        when(mockCb.createQuery(eq(CompraDetalle.class))).thenReturn(mockCqDetalle);
        when(mockCqDetalle.from(eq(CompraDetalle.class))).thenReturn(mockRoot);
        when(mockCqDetalle.select(mockRoot)).thenReturn(mockCqDetalle);
        when(mockCb.asc(mockRoot.get("id"))).thenReturn(mock(Order.class));
        when(mockCqDetalle.orderBy(any(Order.class))).thenReturn(mockCqDetalle);
        when(mockEm.createQuery(eq(mockCqDetalle))).thenReturn(mockTypedQueryDetalle);
        when(mockTypedQueryDetalle.setFirstResult(anyInt())).thenReturn(mockTypedQueryDetalle);
        when(mockTypedQueryDetalle.setMaxResults(anyInt())).thenReturn(mockTypedQueryDetalle);

        // Count
        when(mockCb.createQuery(eq(Long.class))).thenReturn(mockCqLong);
        when(mockCqLong.from(eq(CompraDetalle.class))).thenReturn(mockRoot);
        when(mockCb.count(mockRoot)).thenReturn(mock(jakarta.persistence.criteria.Expression.class));
        when(mockCqLong.select(any())).thenReturn(mockCqLong);
        when(mockEm.createQuery(eq(mockCqLong))).thenReturn(mockTypedQueryLong);

        // ===========================================
        // 2. Configuraciones para Métodos Personalizados (JPQL)
        // ===========================================

        // getDetallesPorCompra
        when(mockEm.createQuery(
                eq("SELECT cd FROM CompraDetalle cd WHERE cd.idCompra.id = :idCompra"),
                eq(CompraDetalle.class)
        )).thenReturn(mockQueryDetallesPorCompra);
        when(mockQueryDetallesPorCompra.setParameter(eq("idCompra"), anyLong())).thenReturn(mockQueryDetallesPorCompra);

        // calcularTotalCompra
        when(mockEm.createQuery(
                eq("SELECT SUM(cd.cantidad * cd.precio) FROM CompraDetalle cd WHERE cd.idCompra.id = :idCompra"),
                eq(BigDecimal.class)
        )).thenReturn(mockQueryTotalCompra);
        when(mockQueryTotalCompra.setParameter(eq("idCompra"), anyLong())).thenReturn(mockQueryTotalCompra);
    }

    // ---
    // Métodos Heredados (CRUD Básico y Paginación)
    // ---

    // 🏷️ Test: Constructor y Getters
    @Test
    void testConstructorAndGetters() {
        assertEquals(CompraDetalle.class, compraDetalleDAO.getEntityClass());
        assertEquals(mockEm, compraDetalleDAO.getEntityManager());
    }

    // ➕ Test: Crear
    @Test
    void testCrear_Success() {
        assertDoesNotThrow(() -> compraDetalleDAO.crear(entidad));
        verify(mockEm, times(1)).persist(entidad);
    }
    @Test
    void testCrear_NullInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> compraDetalleDAO.crear(null));
    }
    @Test
    void testCrear_PersistenceException_ThrowsRuntimeException() {
        doThrow(new RuntimeException()).when(mockEm).persist(entidad);
        assertThrows(RuntimeException.class, () -> compraDetalleDAO.crear(entidad));
    }

    // 🔍 Test: Buscar por ID
    @Test
    void testFindById_Success() {
        when(mockEm.find(CompraDetalle.class, TEST_ID)).thenReturn(entidad);
        CompraDetalle result = compraDetalleDAO.finById(TEST_ID);
        assertNotNull(result);
    }
    @Test
    void testFindById_NullInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> compraDetalleDAO.finById(null));
    }

    // ✏️ Test: Actualizar
    @Test
    void testUpdate_Success() {
        when(mockEm.merge(entidad)).thenReturn(entidad);
        CompraDetalle result = compraDetalleDAO.update(entidad);
        assertEquals(entidad, result);
    }
    @Test
    void testUpdate_NullInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> compraDetalleDAO.update(null));
    }

    // 🗑️ Test: Eliminar
    @Test
    void testDelete_ManagedEntity_Success() {
        when(mockEm.contains(entidad)).thenReturn(true);
        assertDoesNotThrow(() -> compraDetalleDAO.delete(entidad));
        verify(mockEm, times(1)).remove(entidad);
    }
    @Test
    void testDelete_DetachedEntity_Success() {
        when(mockEm.contains(entidad)).thenReturn(false);
        when(mockEm.merge(entidad)).thenReturn(entidad);
        assertDoesNotThrow(() -> compraDetalleDAO.delete(entidad));
        verify(mockEm, times(1)).merge(entidad);
    }
    @Test
    void testDelete_NullInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> compraDetalleDAO.delete(null));
    }

    // 🗃️ Test: Rango
    @Test
    void testFindRange_Success() {
        when(mockTypedQueryDetalle.getResultList()).thenReturn(expectedList);
        List<CompraDetalle> result = compraDetalleDAO.findRange(0, 10);
        assertEquals(expectedList, result);
    }
    @Test
    void testFindRange_InvalidParameters_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> compraDetalleDAO.findRange(-1, 10));
    }

    // 🔢 Test: Conteo
    @Test
    void testCount_Success() {
        Long expectedCount = 42L;
        when(mockTypedQueryLong.getSingleResult()).thenReturn(expectedCount);
        Long result = compraDetalleDAO.count();
        assertEquals(expectedCount, result);
    }

    // NOTA: Para las pruebas de NullEntityManager y PersistenceException en métodos heredados,
    // se asume que la cobertura ya se logró en pruebas anteriores (como ClienteDAOTest),
    // pero se incluyen aquí para la integridad del 100% de cobertura en esta clase.

    @Test
    void testHeredado_NullEntityManager_ThrowsException() {
        doReturn(null).when(compraDetalleDAO).getEntityManager();
        assertThrows(IllegalStateException.class, () -> compraDetalleDAO.crear(entidad));
        assertThrows(IllegalStateException.class, () -> compraDetalleDAO.finById(TEST_ID));
        assertThrows(IllegalStateException.class, () -> compraDetalleDAO.update(entidad));
        assertThrows(IllegalStateException.class, () -> compraDetalleDAO.delete(entidad));
        assertThrows(IllegalStateException.class, () -> compraDetalleDAO.findRange(0, 1));
        assertThrows(IllegalStateException.class, () -> compraDetalleDAO.count());
    }

    // ---
    // Métodos Específicos
    // ---

    // 🛒 Test: getDetallesPorCompra

    @Test
    void testGetDetallesPorCompra_Success() {
        when(mockQueryDetallesPorCompra.getResultList()).thenReturn(expectedList);

        List<CompraDetalle> result = compraDetalleDAO.getDetallesPorCompra(TEST_COMPRA_ID);

        assertEquals(expectedList, result);
        verify(mockQueryDetallesPorCompra, times(1)).setParameter("idCompra", TEST_COMPRA_ID);
        verify(mockQueryDetallesPorCompra, times(1)).getResultList();
    }

    @Test
    void testGetDetallesPorCompra_NullInput_ReturnsEmptyList() {
        List<CompraDetalle> result = compraDetalleDAO.getDetallesPorCompra(null);
        assertTrue(result.isEmpty());
        verify(mockEm, never()).createQuery(anyString(), any());
    }

    @Test
    void testGetDetallesPorCompra_PersistenceException_ReturnsEmptyList() {
        when(mockQueryDetallesPorCompra.getResultList()).thenThrow(new RuntimeException("DB Error"));

        List<CompraDetalle> result = compraDetalleDAO.getDetallesPorCompra(TEST_COMPRA_ID);

        assertTrue(result.isEmpty());
        verify(mockQueryDetallesPorCompra, times(1)).getResultList();
    }

    // 💵 Test: calcularTotalCompra

    @Test
    void testCalcularTotalCompra_Success() {
        BigDecimal totalCalculado = new BigDecimal("55.00");
        when(mockQueryTotalCompra.getSingleResult()).thenReturn(totalCalculado);

        BigDecimal result = compraDetalleDAO.calcularTotalCompra(TEST_COMPRA_ID);

        assertEquals(0, totalCalculado.compareTo(result)); // Compara BigDecimal
        verify(mockQueryTotalCompra, times(1)).setParameter("idCompra", TEST_COMPRA_ID);
        verify(mockQueryTotalCompra, times(1)).getSingleResult();
    }

    @Test
    void testCalcularTotalCompra_NullInput_ReturnsZero() {
        BigDecimal result = compraDetalleDAO.calcularTotalCompra(null);
        assertEquals(0, BigDecimal.ZERO.compareTo(result));
        verify(mockEm, never()).createQuery(anyString(), any());
    }

    @Test
    void testCalcularTotalCompra_NullResultFromDB_ReturnsZero() {
        // Esto ocurre si la lista de detalles está vacía, la BD devuelve NULL para SUM()
        when(mockQueryTotalCompra.getSingleResult()).thenReturn(null);

        BigDecimal result = compraDetalleDAO.calcularTotalCompra(TEST_COMPRA_ID);

        assertEquals(0, BigDecimal.ZERO.compareTo(result));
        verify(mockQueryTotalCompra, times(1)).getSingleResult();
    }

    @Test
    void testCalcularTotalCompra_PersistenceException_ReturnsZero() {
        when(mockQueryTotalCompra.getSingleResult()).thenThrow(new RuntimeException("DB Error"));

        BigDecimal result = compraDetalleDAO.calcularTotalCompra(TEST_COMPRA_ID);

        assertEquals(0, BigDecimal.ZERO.compareTo(result));
        verify(mockQueryTotalCompra, times(1)).getSingleResult();
    }
}

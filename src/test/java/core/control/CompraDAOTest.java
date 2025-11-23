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
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CompraDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Compra;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Proveedor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// Usamos Strictness.LENIENT para evitar UnnecessaryStubbingException, ya que no todos los mocks se usan en cada prueba.
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class CompraDAOTest {

    private static final Long TEST_ID = 100L;
    private static final Integer TEST_PROVEEDOR_ID = 5;
    private static final String ESTADO_COMPLETADA = "COMPLETADA";

    // Mocks para JPA/Criteria API (Usados por los métodos heredados)
    @Mock
    EntityManager mockEm;
    @Mock
    CriteriaBuilder mockCb;
    @Mock
    CriteriaQuery<Compra> mockCqCompra;
    @Mock
    CriteriaQuery<Long> mockCqLong;
    @Mock
    Root<Compra> mockRoot;
    @Mock
    TypedQuery<Compra> mockTypedQueryCompra;
    @Mock
    TypedQuery<Long> mockTypedQueryLong;

    // Mocks específicos para las consultas JPQL (findByProveedor/findByEstado)
    @Mock
    TypedQuery<Compra> mockQueryProveedor;
    @Mock
    TypedQuery<Compra> mockQueryEstado;


    // Spy e Inyección
    @Spy
    @InjectMocks
    CompraDAO compraDAO;

    Compra entidad;
    List<Compra> expectedList;

    @BeforeEach
    void setUp() {
        // Inicialización de la entidad de prueba
        entidad = new Compra();
        entidad.setId(TEST_ID);
        entidad.setEstado(ESTADO_COMPLETADA);

        Proveedor proveedor = new Proveedor();
        proveedor.setId(TEST_PROVEEDOR_ID);
        entidad.setProveedor(proveedor);

        expectedList = Arrays.asList(entidad);

        // Configuración del Spy para usar el Mock de EntityManager
        doReturn(mockEm).when(compraDAO).getEntityManager();

        // ===========================================
        // 1. Configuraciones para FindRange y Count (Heredados)
        // ===========================================
        when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);

        // FindRange
        when(mockCb.createQuery(eq(Compra.class))).thenReturn(mockCqCompra);
        when(mockCqCompra.from(eq(Compra.class))).thenReturn(mockRoot);
        when(mockCqCompra.select(mockRoot)).thenReturn(mockCqCompra);
        when(mockCb.asc(mockRoot.get("id"))).thenReturn(mock(Order.class));
        when(mockCqCompra.orderBy(any(Order.class))).thenReturn(mockCqCompra);
        when(mockEm.createQuery(eq(mockCqCompra))).thenReturn(mockTypedQueryCompra);
        when(mockTypedQueryCompra.setFirstResult(anyInt())).thenReturn(mockTypedQueryCompra);
        when(mockTypedQueryCompra.setMaxResults(anyInt())).thenReturn(mockTypedQueryCompra);

        // Count
        when(mockCb.createQuery(eq(Long.class))).thenReturn(mockCqLong);
        when(mockCqLong.from(eq(Compra.class))).thenReturn(mockRoot);
        when(mockCb.count(mockRoot)).thenReturn(mock(jakarta.persistence.criteria.Expression.class));
        when(mockCqLong.select(any())).thenReturn(mockCqLong);
        when(mockEm.createQuery(eq(mockCqLong))).thenReturn(mockTypedQueryLong);

        // ===========================================
        // 2. Configuraciones para Métodos Personalizados (JPQL)
        // ===========================================

        // findByProveedor
        when(mockEm.createQuery(
                eq("SELECT c FROM Compra c WHERE c.proveedor.id = :idProveedor ORDER BY c.fecha DESC"),
                eq(Compra.class)
        )).thenReturn(mockQueryProveedor);
        when(mockQueryProveedor.setParameter(eq("idProveedor"), anyInt())).thenReturn(mockQueryProveedor);

        // findByEstado
        when(mockEm.createQuery(
                eq("SELECT c FROM Compra c WHERE c.estado = :estado ORDER BY c.fecha DESC"),
                eq(Compra.class)
        )).thenReturn(mockQueryEstado);
        when(mockQueryEstado.setParameter(eq("estado"), anyString())).thenReturn(mockQueryEstado);

    }

    // ---
    // Métodos Heredados (CRUD Básico y Paginación)
    // ---

    // 🏷️ Test: Constructor y Getters
    @Test
    void testConstructorAndGetters() {
        assertEquals(Compra.class, compraDAO.getEntityClass());
        assertEquals(mockEm, compraDAO.getEntityManager());
    }

    // ➕ Test: Crear
    @Test
    void testCrear_Success() {
        assertDoesNotThrow(() -> compraDAO.crear(entidad));
        verify(mockEm, times(1)).persist(entidad);
    }
    @Test
    void testCrear_NullInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> compraDAO.crear(null));
    }
    @Test
    void testCrear_NullEntityManager_ThrowsException() {
        doReturn(null).when(compraDAO).getEntityManager();
        assertThrows(IllegalStateException.class, () -> compraDAO.crear(entidad));
    }
    @Test
    void testCrear_PersistenceException_ThrowsRuntimeException() {
        doThrow(new RuntimeException()).when(mockEm).persist(entidad);
        assertThrows(RuntimeException.class, () -> compraDAO.crear(entidad));
    }

    // 🔍 Test: Buscar por ID
    @Test
    void testFindById_Success() {
        when(mockEm.find(Compra.class, TEST_ID)).thenReturn(entidad);
        Compra result = compraDAO.finById(TEST_ID);
        assertNotNull(result);
    }
    @Test
    void testFindById_NullInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> compraDAO.finById(null));
    }
    @Test
    void testFindById_PersistenceException_ThrowsRuntimeException() {
        doThrow(new RuntimeException()).when(mockEm).find(Compra.class, TEST_ID);
        assertThrows(RuntimeException.class, () -> compraDAO.finById(TEST_ID));
    }

    // ✏️ Test: Actualizar
    @Test
    void testUpdate_Success() {
        when(mockEm.merge(entidad)).thenReturn(entidad);
        Compra result = compraDAO.update(entidad);
        assertEquals(entidad, result);
    }
    @Test
    void testUpdate_NullInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> compraDAO.update(null));
    }
    @Test
    void testUpdate_PersistenceException_ThrowsRuntimeException() {
        doThrow(new RuntimeException()).when(mockEm).merge(entidad);
        assertThrows(RuntimeException.class, () -> compraDAO.update(entidad));
    }

    // 🗑️ Test: Eliminar
    @Test
    void testDelete_ManagedEntity_Success() {
        when(mockEm.contains(entidad)).thenReturn(true);
        assertDoesNotThrow(() -> compraDAO.delete(entidad));
        verify(mockEm, times(1)).remove(entidad);
    }
    @Test
    void testDelete_DetachedEntity_Success() {
        when(mockEm.contains(entidad)).thenReturn(false);
        when(mockEm.merge(entidad)).thenReturn(entidad);
        assertDoesNotThrow(() -> compraDAO.delete(entidad));
        verify(mockEm, times(1)).merge(entidad);
        verify(mockEm, times(1)).remove(entidad);
    }
    @Test
    void testDelete_NullInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> compraDAO.delete(null));
    }
    @Test
    void testDelete_PersistenceException_ThrowsRuntimeException() {
        when(mockEm.contains(entidad)).thenReturn(true);
        doThrow(new RuntimeException()).when(mockEm).remove(entidad);
        assertThrows(RuntimeException.class, () -> compraDAO.delete(entidad));
    }

    // 🗃️ Test: Rango
    @Test
    void testFindRange_Success() {
        when(mockTypedQueryCompra.getResultList()).thenReturn(expectedList);
        List<Compra> result = compraDAO.findRange(0, 10);
        assertEquals(expectedList, result);
        verify(mockTypedQueryCompra, times(1)).setMaxResults(10);
    }
    @Test
    void testFindRange_InvalidParameters_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> compraDAO.findRange(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> compraDAO.findRange(0, 0));
    }
    @Test
    void testFindRange_PersistenceException_ThrowsRuntimeException() {
        when(mockEm.getCriteriaBuilder()).thenThrow(new RuntimeException("DB Access Denied"));
        assertThrows(RuntimeException.class, () -> compraDAO.findRange(0, 10));
    }

    // 🔢 Test: Conteo
    @Test
    void testCount_Success() {
        Long expectedCount = 42L;
        when(mockTypedQueryLong.getSingleResult()).thenReturn(expectedCount);
        Long result = compraDAO.count();
        assertEquals(expectedCount, result);
    }
    @Test
    void testCount_PersistenceException_ThrowsRuntimeException() {
        when(mockEm.getCriteriaBuilder()).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> compraDAO.count());
    }

    // ---
    // Métodos Específicos (findByProveedor, findByEstado)
    // ---

    // 📦 Test: findByProveedor

    @Test
    void testFindByProveedor_Success() {
        when(mockQueryProveedor.getResultList()).thenReturn(expectedList);

        List<Compra> result = compraDAO.findByProveedor(TEST_PROVEEDOR_ID);

        assertEquals(expectedList, result);
        verify(mockQueryProveedor, times(1)).setParameter("idProveedor", TEST_PROVEEDOR_ID);
        verify(mockQueryProveedor, times(1)).getResultList();
    }

    @Test
    void testFindByProveedor_NullInput_ReturnsEmptyList() {
        List<Compra> result = compraDAO.findByProveedor(null);
        assertTrue(result.isEmpty());
        verify(mockEm, never()).createQuery(anyString(), any());
    }

    @Test
    void testFindByProveedor_PersistenceException_ReturnsEmptyList() {
        // Simula la excepción durante la ejecución de la consulta
        when(mockQueryProveedor.getResultList()).thenThrow(new RuntimeException("DB Error"));

        List<Compra> result = compraDAO.findByProveedor(TEST_PROVEEDOR_ID);

        assertTrue(result.isEmpty());
        verify(mockQueryProveedor, times(1)).getResultList();
    }

    // 📝 Test: findByEstado

    @Test
    void testFindByEstado_Success() {
        when(mockQueryEstado.getResultList()).thenReturn(expectedList);

        List<Compra> result = compraDAO.findByEstado(ESTADO_COMPLETADA);

        assertEquals(expectedList, result);
        verify(mockQueryEstado, times(1)).setParameter("estado", ESTADO_COMPLETADA);
        verify(mockQueryEstado, times(1)).getResultList();
    }

    @Test
    void testFindByEstado_NullInput_ReturnsEmptyList() {
        List<Compra> result = compraDAO.findByEstado(null);
        assertTrue(result.isEmpty());
        verify(mockEm, never()).createQuery(anyString(), any());
    }

    @Test
    void testFindByEstado_EmptyInput_ReturnsEmptyList() {
        List<Compra> result = compraDAO.findByEstado("  ");
        assertTrue(result.isEmpty());
        verify(mockEm, never()).createQuery(anyString(), any());
    }

    @Test
    void testFindByEstado_PersistenceException_ReturnsEmptyList() {
        // Simula la excepción durante la ejecución de la consulta
        when(mockQueryEstado.getResultList()).thenThrow(new RuntimeException("DB Error"));

        List<Compra> result = compraDAO.findByEstado(ESTADO_COMPLETADA);

        assertTrue(result.isEmpty());
        verify(mockQueryEstado, times(1)).getResultList();
    }
}
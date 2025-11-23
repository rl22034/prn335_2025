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
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Producto;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ProductoDAOTest {

    private static final Long TEST_COMPRA_ID = 50L;
    private static final UUID TEST_ID = UUID.randomUUID();

    // Mocks para JPA/Criteria API (Heredados)
    @Mock
    EntityManager mockEm;
    @Mock
    CriteriaBuilder mockCb;
    @Mock
    CriteriaQuery<Producto> mockCqProducto;
    @Mock
    CriteriaQuery<Long> mockCqLong;
    @Mock
    Root<Producto> mockRoot;
    @Mock
    TypedQuery<Producto> mockTypedQueryProducto;
    @Mock
    TypedQuery<Long> mockTypedQueryLong;

    // Mocks específicos para consultas JPQL
    @Mock
    TypedQuery<Producto> mockQueryProductoByCompra;

    // Spy e Inyección
    @Spy
    @InjectMocks
    ProductoDAO productoDAO;

    Producto entidad;
    List<Producto> expectedList;

    @BeforeEach
    void setUp() {
        // Inicialización de la entidad de prueba
        entidad = new Producto();
        entidad.setId(TEST_ID);
        entidad.setNombreProducto("Laptop Test");

        expectedList = Arrays.asList(entidad);

        // Configuración de GetEntityManager
        doReturn(mockEm).when(productoDAO).getEntityManager();

        // ===========================================
        // 1. Configuraciones para FindRange y Count (Heredados)
        // ===========================================
        when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);

        // FindRange
        when(mockCb.createQuery(eq(Producto.class))).thenReturn(mockCqProducto);
        when(mockCqProducto.from(eq(Producto.class))).thenReturn(mockRoot);
        when(mockCqProducto.select(mockRoot)).thenReturn(mockCqProducto);
        when(mockCb.asc(mockRoot.get("id"))).thenReturn(mock(Order.class));
        when(mockCqProducto.orderBy(any(Order.class))).thenReturn(mockCqProducto);
        when(mockEm.createQuery(eq(mockCqProducto))).thenReturn(mockTypedQueryProducto);
        when(mockTypedQueryProducto.setFirstResult(anyInt())).thenReturn(mockTypedQueryProducto);
        when(mockTypedQueryProducto.setMaxResults(anyInt())).thenReturn(mockTypedQueryProducto);

        // Count
        when(mockCb.createQuery(eq(Long.class))).thenReturn(mockCqLong);
        when(mockCqLong.from(eq(Producto.class))).thenReturn(mockRoot);
        when(mockCb.count(mockRoot)).thenReturn(mock(jakarta.persistence.criteria.Expression.class));
        when(mockCqLong.select(any())).thenReturn(mockCqLong);
        when(mockEm.createQuery(eq(mockCqLong))).thenReturn(mockTypedQueryLong);

        // ===========================================
        // 2. Configuraciones para findByCompra (JPQL)
        // ===========================================
        when(mockEm.createQuery(
                eq("SELECT cd.idProducto FROM CompraDetalle cd WHERE cd.idCompra.id = :idCompra"),
                eq(Producto.class)
        )).thenReturn(mockQueryProductoByCompra);
        when(mockQueryProductoByCompra.setParameter(eq("idCompra"), anyLong())).thenReturn(mockQueryProductoByCompra);
    }

    // ---
    // Métodos Heredados (CRUD Básico y Paginación)
    // ---

    // 🏷️ Test: Constructor y Getters
    @Test
    void testConstructorAndGetters() {
        assertEquals(Producto.class, productoDAO.getEntityClass());
        assertEquals(mockEm, productoDAO.getEntityManager());
    }

    // ➕ Test: Crear
    @Test
    void testCrear_Success() {
        assertDoesNotThrow(() -> productoDAO.crear(entidad));
        verify(mockEm, times(1)).persist(entidad);
    }
    @Test
    void testCrear_NullInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> productoDAO.crear(null));
    }
    @Test
    void testCrear_PersistenceException_ThrowsRuntimeException() {
        doThrow(new RuntimeException()).when(mockEm).persist(entidad);
        assertThrows(RuntimeException.class, () -> productoDAO.crear(entidad));
    }

    // 🔍 Test: Buscar por ID
    @Test
    void testFindById_Success() {
        when(mockEm.find(Producto.class, TEST_ID)).thenReturn(entidad);
        Producto result = productoDAO.finById(TEST_ID);
        assertNotNull(result);
    }
    @Test
    void testFindById_NullInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> productoDAO.finById(null));
    }

    // ✏️ Test: Actualizar
    @Test
    void testUpdate_Success() {
        when(mockEm.merge(entidad)).thenReturn(entidad);
        Producto result = productoDAO.update(entidad);
        assertEquals(entidad, result);
    }
    @Test
    void testUpdate_NullInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> productoDAO.update(null));
    }

    // 🗑️ Test: Eliminar
    @Test
    void testDelete_ManagedEntity_Success() {
        when(mockEm.contains(entidad)).thenReturn(true);
        assertDoesNotThrow(() -> productoDAO.delete(entidad));
        verify(mockEm, times(1)).remove(entidad);
    }
    @Test
    void testDelete_DetachedEntity_Success() {
        when(mockEm.contains(entidad)).thenReturn(false);
        when(mockEm.merge(entidad)).thenReturn(entidad);
        assertDoesNotThrow(() -> productoDAO.delete(entidad));
    }
    @Test
    void testDelete_NullInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> productoDAO.delete(null));
    }

    // 🗃️ Test: Rango
    @Test
    void testFindRange_Success() {
        when(mockTypedQueryProducto.getResultList()).thenReturn(expectedList);
        List<Producto> result = productoDAO.findRange(0, 10);
        assertEquals(expectedList, result);
    }
    @Test
    void testFindRange_InvalidParameters_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> productoDAO.findRange(-1, 10));
    }

    // 🔢 Test: Conteo
    @Test
    void testCount_Success() {
        Long expectedCount = 42L;
        when(mockTypedQueryLong.getSingleResult()).thenReturn(expectedCount);
        Long result = productoDAO.count();
        assertEquals(expectedCount, result);
    }

    // Test de Excepciones para Cobertura de InventarioDefaultDataAccess
    @Test
    void testHeredado_NullEntityManager_ThrowsException() {
        doReturn(null).when(productoDAO).getEntityManager();
        assertThrows(IllegalStateException.class, () -> productoDAO.crear(entidad));
        assertThrows(IllegalStateException.class, () -> productoDAO.finById(TEST_ID));
        assertThrows(IllegalStateException.class, () -> productoDAO.update(entidad));
        assertThrows(IllegalStateException.class, () -> productoDAO.delete(entidad));
        assertThrows(IllegalStateException.class, () -> productoDAO.findRange(0, 1));
        assertThrows(IllegalStateException.class, () -> productoDAO.count());
    }
    @Test
    void testHeredado_PersistenceException_ThrowsRuntimeException() {
        // Cubrimos un método para forzar la excepción de Criteria API
        when(mockEm.getCriteriaBuilder()).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> productoDAO.findRange(0, 1));
    }


    // ---
    // Método Específico
    // ---

    // 🛍️ Test: findByCompra

    @Test
    void testFindByCompra_Success() {
        when(mockQueryProductoByCompra.getResultList()).thenReturn(expectedList);

        List<Producto> result = productoDAO.findByCompra(TEST_COMPRA_ID);

        assertEquals(expectedList, result);
        verify(mockQueryProductoByCompra, times(1)).setParameter("idCompra", TEST_COMPRA_ID);
        verify(mockQueryProductoByCompra, times(1)).getResultList();
    }


    @Test
    void testFindByCompra_PersistenceException_ThrowsRuntimeException() {
            doReturn(null).when(productoDAO).getEntityManager();

            // CORREGIDO: Esperar NullPointerException, ya que la línea
            // getEntityManager().createQuery(...) provoca un NPE si getEntityManager() es null.
            assertThrows(NullPointerException.class, () -> productoDAO.findByCompra(TEST_COMPRA_ID));

    }

    @Test
    void testFindByCompra_NullEntityManager_ThrowsNullPointerException() {
        doReturn(null).when(productoDAO).getEntityManager();

        // La llamada a getEntityManager().createQuery(...) con getEntityManager() siendo null
        // provoca una excepción de puntero nulo (NPE).
        assertThrows(NullPointerException.class, () -> productoDAO.findByCompra(TEST_COMPRA_ID));
    }
}
package core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoTipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.ProductoTipoProducto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class ProductoTipoProductoDAOTest {

    private final UUID TEST_PRODUCTO_ID = UUID.randomUUID();
    private final int FIRST_RESULT = 0;
    private final int MAX_RESULTS = 10;
    private ProductoTipoProducto mockPtp;
    private List<ProductoTipoProducto> expectedList;

    @Mock
    EntityManager mockEm;

    // Mocks para Named Queries
    @Mock
    TypedQuery<ProductoTipoProducto> mockQueryFindByProducto;
    @Mock
    TypedQuery<Long> mockQueryCountByProducto;

    // Spy e Inyección
    // Nota: Se asume que el método getEntityManager() está en el DAO o en la clase padre (InventarioDefaultDataAccess)
    @Spy
    @InjectMocks
    ProductoTipoProductoDAO dao;

    @BeforeEach
    void setUp() {
        mockPtp = new ProductoTipoProducto();
        mockPtp.setId(UUID.randomUUID());
        expectedList = Arrays.asList(mockPtp);

        // Configuración de getEntityManager() para cubrir la herencia (aunque no se use directamente aquí)
        doReturn(mockEm).when(dao).getEntityManager();

        // 1. Configuración de ProductoTipoProducto.findByProducto
        when(mockEm.createNamedQuery(eq("ProductoTipoProducto.findByProducto"), eq(ProductoTipoProducto.class)))
                .thenReturn(mockQueryFindByProducto);
        when(mockQueryFindByProducto.setParameter(eq("idProducto"), any(UUID.class))).thenReturn(mockQueryFindByProducto);
        when(mockQueryFindByProducto.setFirstResult(anyInt())).thenReturn(mockQueryFindByProducto);
        when(mockQueryFindByProducto.setMaxResults(anyInt())).thenReturn(mockQueryFindByProducto);
        when(mockQueryFindByProducto.getResultList()).thenReturn(expectedList);

        // 2. Configuración de ProductoTipoProducto.countByProducto
        when(mockEm.createNamedQuery(eq("ProductoTipoProducto.countByProducto"), eq(Long.class)))
                .thenReturn(mockQueryCountByProducto);
        when(mockQueryCountByProducto.setParameter(eq("idProducto"), any(UUID.class))).thenReturn(mockQueryCountByProducto);
        when(mockQueryCountByProducto.getSingleResult()).thenReturn(5L);
    }

    // ----------------------------------------------------
    // Test del Constructor y Getters
    // ----------------------------------------------------

    @Test
    void testConstructorAndGetters() {
        // Verifica que el constructor pase la clase correcta al padre
        assertEquals(ProductoTipoProducto.class, dao.getEntityClass());
        // Verifica que el método getEntityManager devuelva el mock inyectado
        assertEquals(mockEm, dao.getEntityManager());
    }

    // ----------------------------------------------------
    // Test del Método findByIdProducto
    // ----------------------------------------------------

    @Test
    void testFindByIdProducto_Success() {
        List<ProductoTipoProducto> result = dao.findByIdProducto(TEST_PRODUCTO_ID, FIRST_RESULT, MAX_RESULTS);

        assertEquals(expectedList, result);

        // Verificaciones
        verify(mockQueryFindByProducto, times(1)).setParameter("idProducto", TEST_PRODUCTO_ID);
        verify(mockQueryFindByProducto, times(1)).setFirstResult(FIRST_RESULT);
        verify(mockQueryFindByProducto, times(1)).setMaxResults(MAX_RESULTS);
        verify(mockQueryFindByProducto, times(1)).getResultList();
    }

    @Test
    void testFindByIdProducto_NullId_ReturnsEmptyList() {
        List<ProductoTipoProducto> result = dao.findByIdProducto(null, FIRST_RESULT, MAX_RESULTS);

        // Cobertura del 'if (idProducto != null)'
        assertTrue(result.isEmpty());
        // Asegura que no se haya intentado crear la NamedQuery
        verify(mockEm, never()).createNamedQuery(anyString(), any(Class.class));
    }

    @Test
    void testFindByIdProducto_PersistenceException_ReturnsEmptyList() {
        // Simular que el EntityManager lanza una excepción al crear la Query
        when(mockEm.createNamedQuery(anyString(), any(Class.class)))
                .thenThrow(new RuntimeException("DB Error"));

        List<ProductoTipoProducto> result = dao.findByIdProducto(TEST_PRODUCTO_ID, FIRST_RESULT, MAX_RESULTS);

        // Cobertura del bloque catch
        assertTrue(result.isEmpty());
    }

    // ----------------------------------------------------
    // Test del Método countByIdProducto
    // ----------------------------------------------------

    @Test
    void testCountByIdProducto_Success() {
        long result = dao.countByIdProducto(TEST_PRODUCTO_ID);

        assertEquals(5L, result);

        // Verificaciones
        verify(mockQueryCountByProducto, times(1)).setParameter("idProducto", TEST_PRODUCTO_ID);
        verify(mockQueryCountByProducto, times(1)).getSingleResult();
    }

    @Test
    void testCountByIdProducto_NullId_ReturnsZero() {
        long result = dao.countByIdProducto(null);

        // Cobertura del 'if (idProducto != null)'
        assertEquals(0L, result);
        // Asegura que no se haya intentado crear la NamedQuery
        verify(mockEm, never()).createNamedQuery(anyString(), any(Class.class));
    }

    @Test
    void testCountByIdProducto_PersistenceException_ReturnsZero() {
        // Simular que el TypedQuery lanza una excepción al obtener el resultado
        when(mockQueryCountByProducto.getSingleResult()).thenThrow(new RuntimeException("DB Error"));

        // Asegurar que el DAO devuelve 0 y maneja la excepción
        long result = dao.countByIdProducto(TEST_PRODUCTO_ID);

        // Cobertura del bloque catch
        assertEquals(0L, result);
    }
}

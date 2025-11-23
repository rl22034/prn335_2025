package core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Caracteristica;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CaracteristicaDAOTest {

    private static final Integer TEST_ID = 5;

    // Mocks para simular el entorno JPA
    @Mock
    EntityManager mockEm;
    @Mock
    CriteriaBuilder mockCb;

    // Mocks separados para CriteriaQuery de Caracteristica y de Long
    @Mock
    CriteriaQuery<Caracteristica> mockCqCaracteristica;
    @Mock
    CriteriaQuery<Long> mockCqLong;

    @Mock
    Root<Caracteristica> mockRoot;

    // Mocks para las TypedQuery
    @Mock
    TypedQuery<Caracteristica> mockTypedQueryCaracteristica;
    @Mock
    TypedQuery<Long> mockTypedQueryLong;

    // Spy en la clase concreta
    @Spy
    @InjectMocks
    CaracteristicaDAO caracteristicaDAO;

    // Entidad de prueba
    Caracteristica entidad;

    @BeforeEach
    void setUp() {
        entidad = new Caracteristica();
        entidad.setId(TEST_ID);
        entidad.setNombre("Color");

        // Configuraciones Lenient para Criteria API
        lenient().when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);

        // 1. Configuración de FindRange (Caracteristica)
        // La línea 62, que Mockito marcaba como innecesaria, se mantiene como lenient
        lenient().when(mockCb.createQuery(eq(Caracteristica.class))).thenReturn(mockCqCaracteristica);
        lenient().when(mockCqCaracteristica.from(eq(Caracteristica.class))).thenReturn(mockRoot);
        lenient().when(mockCqCaracteristica.select(mockRoot)).thenReturn(mockCqCaracteristica);

        // Simulación para orderBy
        lenient().when(mockCb.asc(mockRoot.get("id"))).thenReturn(mock(Order.class));
        lenient().when(mockCqCaracteristica.orderBy(any(Order.class))).thenReturn(mockCqCaracteristica);

        // ⭐ CORRECCIÓN CLAVE para NullPointerException:
        // Mockito fallaba porque 'em.createQuery(all)' recibía un objeto CriteriaQuery
        // que no coincidía *exactamente* con un mock predefinido.
        // Simulamos que cualquier llamada a createQuery con un CriteriaQuery devuelve
        // el TypedQuery que necesitamos para FindRange.
        lenient().when(mockEm.createQuery(any(CriteriaQuery.class))).thenReturn((TypedQuery) mockTypedQueryCaracteristica);

        lenient().when(mockTypedQueryCaracteristica.setFirstResult(anyInt())).thenReturn(mockTypedQueryCaracteristica);
        lenient().when(mockTypedQueryCaracteristica.setMaxResults(anyInt())).thenReturn(mockTypedQueryCaracteristica);


        // 2. Configuraciones para Count (Long)
        lenient().when(mockCb.createQuery(eq(Long.class))).thenReturn(mockCqLong);
        lenient().when(mockCqLong.from(eq(Caracteristica.class))).thenReturn(mockRoot);
        lenient().when(mockCqLong.select(any())).thenReturn(mockCqLong);

        // 3. Configuración del TypedQuery Long para Count
        lenient().when(mockEm.createQuery(eq(mockCqLong))).thenReturn(mockTypedQueryLong);
    }

    // =================== Test Constructor y Getters ===================

    @Test
    void testConstructorAndGetters() {
        assertEquals(Caracteristica.class, caracteristicaDAO.getEntityClass());
        assertEquals(mockEm, caracteristicaDAO.getEntityManager());
    }

    // =================== Test CREAR ===================

    @Test
    void testCrear_Success() {
        assertDoesNotThrow(() -> caracteristicaDAO.crear(entidad));
        verify(mockEm, times(1)).persist(entidad);
    }

    @Test
    void testCrear_NullInput() {
        assertThrows(IllegalArgumentException.class, () -> caracteristicaDAO.crear(null));
        verify(mockEm, never()).persist(any());
    }

    @Test
    void testCrear_NullEntityManager() {
        doReturn(null).when(caracteristicaDAO).getEntityManager();
        assertThrows(IllegalStateException.class, () -> caracteristicaDAO.crear(entidad));
    }

    @Test
    void testCrear_PersistenceException() {
        doThrow(new RuntimeException("DB Error")).when(mockEm).persist(entidad);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> caracteristicaDAO.crear(entidad));
        assertTrue(ex.getMessage().contains("No se puede crear la entidad de registro"));
    }

    // =================== Test FINDBYID ===================

    @Test
    void testFindById_Success() {
        when(mockEm.find(Caracteristica.class, TEST_ID)).thenReturn(entidad);
        Caracteristica result = caracteristicaDAO.finById(TEST_ID);
        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        verify(mockEm, times(1)).find(Caracteristica.class, TEST_ID);
    }

    @Test
    void testFindById_NullInput() {
        assertThrows(IllegalArgumentException.class, () -> caracteristicaDAO.finById(null));
        verify(mockEm, never()).find(any(), any());
    }

    @Test
    void testFindById_NullEntityManager() {
        doReturn(null).when(caracteristicaDAO).getEntityManager();
        assertThrows(IllegalStateException.class, () -> caracteristicaDAO.finById(TEST_ID));
    }

    @Test
    void testFindById_PersistenceException() {
        doThrow(new RuntimeException("Error de conexión")).when(mockEm).find(Caracteristica.class, TEST_ID);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> caracteristicaDAO.finById(TEST_ID));
        assertTrue(ex.getMessage().contains("Error al obtener la entidad"));
    }

    // =================== Test UPDATE ===================

    @Test
    void testUpdate_Success() {
        Caracteristica updatedEntity = new Caracteristica();
        when(mockEm.merge(entidad)).thenReturn(updatedEntity);

        Caracteristica result = caracteristicaDAO.update(entidad);

        assertEquals(updatedEntity, result);
        verify(mockEm, times(1)).merge(entidad);
    }

    @Test
    void testUpdate_NullInput() {
        assertThrows(IllegalArgumentException.class, () -> caracteristicaDAO.update(null));
        verify(mockEm, never()).merge(any());
    }

    @Test
    void testUpdate_NullEntityManager() {
        doReturn(null).when(caracteristicaDAO).getEntityManager();
        assertThrows(IllegalStateException.class, () -> caracteristicaDAO.update(entidad));
    }

    @Test
    void testUpdate_PersistenceException() {
        doThrow(new RuntimeException("DB Update Error")).when(mockEm).merge(entidad);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> caracteristicaDAO.update(entidad));
        assertTrue(ex.getMessage().contains("Error al actualizar la entidad"));
    }

    // =================== Test DELETE ===================

    @Test
    void testDelete_ManagedEntity_Success() {
        when(mockEm.contains(entidad)).thenReturn(true);

        assertDoesNotThrow(() -> caracteristicaDAO.delete(entidad));
        verify(mockEm, times(1)).contains(entidad);
        verify(mockEm, times(1)).remove(entidad);
    }

    @Test
    void testDelete_DetachedEntity_Success() {
        Caracteristica mergedEntity = new Caracteristica();

        when(mockEm.contains(entidad)).thenReturn(false);
        when(mockEm.merge(entidad)).thenReturn(mergedEntity);

        assertDoesNotThrow(() -> caracteristicaDAO.delete(entidad));

        verify(mockEm, times(1)).contains(entidad);
        verify(mockEm, times(1)).merge(entidad);
        verify(mockEm, times(1)).remove(mergedEntity);
    }

    @Test
    void testDelete_NullInput() {
        assertThrows(IllegalArgumentException.class, () -> caracteristicaDAO.delete(null));
        verify(mockEm, never()).remove(any());
    }

    @Test
    void testDelete_NullEntityManager() {
        doReturn(null).when(caracteristicaDAO).getEntityManager();
        assertThrows(IllegalStateException.class, () -> caracteristicaDAO.delete(entidad));
    }

    @Test
    void testDelete_PersistenceException() {
        when(mockEm.contains(entidad)).thenReturn(true);
        doThrow(new RuntimeException("DB Delete Error")).when(mockEm).remove(entidad);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> caracteristicaDAO.delete(entidad));
        assertTrue(ex.getMessage().contains("Error al eliminar entidad"));
    }

    // =================== Test FINDRANGE ===================

    @Test
    void testFindRange_Success() {
        List<Caracteristica> expectedList = Arrays.asList(entidad);

        when(mockTypedQueryCaracteristica.getResultList()).thenReturn(expectedList);

        List<Caracteristica> result = caracteristicaDAO.findRange(0, 10);

        // Verificaciones de lógica de Criteria y Paginación
        assertEquals(expectedList, result);
        verify(mockEm, times(1)).getCriteriaBuilder();
        verify(mockCb, times(1)).createQuery(Caracteristica.class);
        verify(mockCqCaracteristica, times(1)).from(Caracteristica.class);
        verify(mockCqCaracteristica, times(1)).select(mockRoot);

        // Verifica el orderBy usando la clase Order (soluciona ambigüedad)
        verify(mockCqCaracteristica, times(1)).orderBy(any(Order.class));

        verify(mockTypedQueryCaracteristica, times(1)).setFirstResult(0);
        verify(mockTypedQueryCaracteristica, times(1)).setMaxResults(10);
        verify(mockTypedQueryCaracteristica, times(1)).getResultList();
    }

    @Test
    void testFindRange_InvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> caracteristicaDAO.findRange(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> caracteristicaDAO.findRange(0, 0));
        verify(mockEm, never()).getCriteriaBuilder();
    }

    @Test
    void testFindRange_NullEntityManager() {
        doReturn(null).when(caracteristicaDAO).getEntityManager();
        assertThrows(IllegalStateException.class, () -> caracteristicaDAO.findRange(0, 10));
    }

    @Test
    void testFindRange_PersistenceException() {
        when(mockEm.getCriteriaBuilder()).thenThrow(new RuntimeException("DB Access Denied"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> caracteristicaDAO.findRange(0, 10));
        assertTrue(ex.getMessage().contains("No se puede acceder al repositorio"));
    }

    // =================== Test COUNT ===================

    @Test
    void testCount_Success() {
        Long expectedCount = 42L;
        // El TypedQuery Long debe ser mockeado para devolver el resultado
        when(mockTypedQueryLong.getSingleResult()).thenReturn(expectedCount);

        Long result = caracteristicaDAO.count();

        // Verificaciones de lógica de Criteria para el conteo
        assertEquals(expectedCount, result);
        verify(mockEm, times(1)).getCriteriaBuilder();
        verify(mockCb, times(1)).createQuery(Long.class);
        verify(mockCqLong, times(1)).from(Caracteristica.class);
        verify(mockCqLong, times(1)).select(any());
        verify(mockEm, times(1)).createQuery(mockCqLong); // Verifica que se llama con el mockCqLong
        verify(mockTypedQueryLong, times(1)).getSingleResult();
    }

    @Test
    void testCount_NullEntityManager() {
        doReturn(null).when(caracteristicaDAO).getEntityManager();
        assertThrows(IllegalStateException.class, () -> caracteristicaDAO.count());
    }

    @Test
    void testCount_PersistenceException() {
        when(mockEm.getCriteriaBuilder()).thenThrow(new RuntimeException("Error en la consulta COUNT"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> caracteristicaDAO.count());
        assertTrue(ex.getMessage().contains("Error en los parámetros de la consulta"));
    }
}
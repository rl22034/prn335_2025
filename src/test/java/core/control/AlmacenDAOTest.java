package core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.AlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Almacen;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoAlmacen;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlmacenDAOTest {

    private static final Integer TEST_ID = 1;
    private static final Integer TEST_ID_TIPO = 10;

    @Mock
    EntityManager mockEm;
    @Mock
    TypedQuery<Almacen> mockAlmacenTypedQuery;
    @Mock
    TypedQuery<Long> mockLongTypedQuery;

    @InjectMocks
    AlmacenDAO almacenDAO;

    Almacen mockAlmacen;
    TipoAlmacen mockTipoAlmacen;

    @BeforeEach
    void setUp() {
        // Inicializar entidades de prueba
        mockTipoAlmacen = new TipoAlmacen();
        mockTipoAlmacen.setId(TEST_ID_TIPO);

        mockAlmacen = new Almacen();
        mockAlmacen.setId(TEST_ID);
        mockAlmacen.setActivo(true);
        mockAlmacen.setIdTipoAlmacen(mockTipoAlmacen);

        // Configuración básica del TypedQuery para evitar NPEs en los métodos del DAO.
        // Usamos lenient().when() para las configuraciones globales que no todos los tests usan.

        // Simulación para consultas que devuelven Almacen
        lenient().when(mockEm.createQuery(anyString(), eq(Almacen.class))).thenReturn(mockAlmacenTypedQuery);
        // Simulación para consultas que devuelven Long (COUNT)
        lenient().when(mockEm.createQuery(anyString(), eq(Long.class))).thenReturn(mockLongTypedQuery);

        // Simulación de encadenamiento de métodos para Almacen TypedQuery
        lenient().when(mockAlmacenTypedQuery.setParameter(anyString(), any())).thenReturn(mockAlmacenTypedQuery);
        lenient().when(mockAlmacenTypedQuery.setFirstResult(anyInt())).thenReturn(mockAlmacenTypedQuery);
        lenient().when(mockAlmacenTypedQuery.setMaxResults(anyInt())).thenReturn(mockAlmacenTypedQuery);

        // Simulación de encadenamiento de métodos para Long TypedQuery
        lenient().when(mockLongTypedQuery.setParameter(anyString(), any())).thenReturn(mockLongTypedQuery);
    }

    // --- MÉTODOS BASE DEL DAO ---

    @Test
    void testConstructorAndGetEntityManager() {
        // La clase es abstracta, se comprueba la inyección y el constructor de la superclase
        assertNotNull(almacenDAO.getEntityManager());
        assertEquals(mockEm, almacenDAO.getEntityManager());
    }

    // --- FINDBYID PERSONALIZADO (FETCH JOIN) ---

    @Test
    void testFinById_Success() {
        when(mockAlmacenTypedQuery.getSingleResult()).thenReturn(mockAlmacen);

        Almacen result = almacenDAO.finById(TEST_ID);

        // Verificaciones
        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        verify(mockEm, times(1)).createQuery(
                eq("SELECT a FROM Almacen a LEFT JOIN FETCH a.idTipoAlmacen WHERE a.id = :id"),
                eq(Almacen.class));
        verify(mockAlmacenTypedQuery, times(1)).setParameter("id", TEST_ID);
        verify(mockAlmacenTypedQuery, times(1)).getSingleResult();
    }

    @Test
    void testFinById_NotFound() {
        // Simular que no se encuentra ningún resultado
        when(mockAlmacenTypedQuery.getSingleResult()).thenThrow(new NoResultException());

        Almacen result = almacenDAO.finById(TEST_ID);

        // Verificaciones
        assertNull(result, "Debe retornar null si ocurre NoResultException.");
    }

    @Test
    void testFinById_OtherException() {
        // Simular una excepción de cualquier otro tipo (e.g., QueryTimeoutException)
        when(mockAlmacenTypedQuery.getSingleResult()).thenThrow(new RuntimeException("Simulated DB timeout"));

        Almacen result = almacenDAO.finById(TEST_ID);

        // Verificaciones
        assertNull(result, "Debe retornar null si ocurre cualquier otra excepción.");
    }

    // --- FINDRANGE PERSONALIZADO (FETCH JOIN) ---

    @Test
    void testFindRange_Success() {
        List<Almacen> mockList = List.of(mockAlmacen);
        when(mockAlmacenTypedQuery.getResultList()).thenReturn(mockList);

        List<Almacen> result = almacenDAO.findRange(0, 10);

        // Verificaciones
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(mockEm, times(1)).createQuery(
                eq("SELECT a FROM Almacen a LEFT JOIN FETCH a.idTipoAlmacen ORDER BY a.id ASC"),
                eq(Almacen.class));
        verify(mockAlmacenTypedQuery, times(1)).setFirstResult(0);
        verify(mockAlmacenTypedQuery, times(1)).setMaxResults(10);
        verify(mockAlmacenTypedQuery, times(1)).getResultList();
    }

    @Test
    void testFindRange_Exception() {
        // Simular una excepción
        when(mockAlmacenTypedQuery.getResultList()).thenThrow(new RuntimeException("DB connection error"));

        List<Almacen> result = almacenDAO.findRange(0, 10);

        // Verificaciones
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Debe retornar una lista vacía en caso de excepción.");
    }

    // --- FINDBYTIPOALMACEN ---

    @Test
    void testFindByTipoAlmacen_Success() {
        List<Almacen> mockList = List.of(mockAlmacen);
        when(mockAlmacenTypedQuery.getResultList()).thenReturn(mockList);

        List<Almacen> result = almacenDAO.findByTipoAlmacen(TEST_ID_TIPO, 0, 5);

        // Verificaciones
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(mockEm, times(1)).createQuery(
                eq("SELECT a FROM Almacen a WHERE a.idTipoAlmacen.id = :idTipo ORDER BY a.id"),
                eq(Almacen.class));
        verify(mockAlmacenTypedQuery, times(1)).setParameter("idTipo", TEST_ID_TIPO);
        verify(mockAlmacenTypedQuery, times(1)).setFirstResult(0);
        verify(mockAlmacenTypedQuery, times(1)).setMaxResults(5);
        verify(mockAlmacenTypedQuery, times(1)).getResultList();
    }

    @Test
    void testFindByTipoAlmacen_Exception() {
        // Simular una excepción
        when(mockAlmacenTypedQuery.getResultList()).thenThrow(new RuntimeException("Error en consulta filtrada"));

        List<Almacen> result = almacenDAO.findByTipoAlmacen(TEST_ID_TIPO, 0, 5);

        // Verificaciones
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Debe retornar una lista vacía en caso de excepción.");
    }

    // --- COUNTBYTIPOALMACEN ---

    @Test
    void testCountByTipoAlmacen_Success() {
        Long expectedCount = 5L;
        when(mockLongTypedQuery.getSingleResult()).thenReturn(expectedCount);

        Long result = almacenDAO.countByTipoAlmacen(TEST_ID_TIPO);

        // Verificaciones
        assertEquals(expectedCount, result);
        verify(mockEm, times(1)).createQuery(
                eq("SELECT COUNT(a) FROM Almacen a WHERE a.idTipoAlmacen.id = :idTipo"),
                eq(Long.class));
        verify(mockLongTypedQuery, times(1)).setParameter("idTipo", TEST_ID_TIPO);
        verify(mockLongTypedQuery, times(1)).getSingleResult();
    }

    @Test
    void testCountByTipoAlmacen_Exception() {
        // Simular una excepción
        when(mockLongTypedQuery.getSingleResult()).thenThrow(new RuntimeException("Error contando"));

        Long result = almacenDAO.countByTipoAlmacen(TEST_ID_TIPO);

        // Verificaciones
        assertEquals(0L, result, "Debe retornar 0L en caso de excepción.");
    }
}
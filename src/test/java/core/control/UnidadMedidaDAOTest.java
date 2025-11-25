package core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.UnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.UnidadMedida;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UnidadMedidaDAOTest {

    @Mock
    private EntityManager mockEm;

    @Mock
    private TypedQuery<UnidadMedida> mockQueryUnidadMedida;

    @Mock
    private TypedQuery<Long> mockQueryLong;

    @InjectMocks
    private UnidadMedidaDAO unidadMedidaDAO;

    private static final String JPQL_FIND_RANGE = "SELECT um FROM UnidadMedida um LEFT JOIN FETCH um.idTipoUnidadMedida ORDER BY um.id ASC";
    private static final String JPQL_COUNT_BY_TIPO = "SELECT COUNT(um) FROM UnidadMedida um WHERE um.idTipoUnidadMedida.id = :idTipo";
    private static final String JPQL_FIND_BY_TIPO = "SELECT um FROM UnidadMedida um WHERE um.idTipoUnidadMedida.id = :idTipo ORDER BY um.id";
    private static final String JPQL_GET_UNIDADES_BY_TIPO = "SELECT um FROM UnidadMedida um WHERE um.idTipoUnidadMedida.id = :idTipo";
    private static final Integer TIPO_ID = 1;
    private UnidadMedida mockUnidad;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUnidad = new UnidadMedida();
        mockUnidad.setId(1);

        // ❌ LÍNEA ORIGINAL Y ERRÓNEA:
        // mockUnidad.setNombre("Kilogramo");

        // ✅ CORRECCIÓN: Usar un setter válido de la clase UnidadMedida
        mockUnidad.setComentarios("Kilogramo");

        // Alternativamente, puedes simplemente eliminar la línea si no necesitas inicializar ese campo.
    }

    // -------------------------------------------------------------------------
    // 1. Pruebas de Métodos Base (Constructor/getEntityManager)
    // -------------------------------------------------------------------------

    @Test
    void testConstructorAndGetEntityManager() {
        // Assert: Verifica que el EntityManager se devuelve correctamente
        assertEquals(mockEm, unidadMedidaDAO.getEntityManager());

        // Assert: Verifica que la clase de entidad se inicializó correctamente
        // Nota: Esto asume que InventarioDefaultDataAccess tiene un campo para la clase de entidad
        // Aquí solo verificamos el comportamiento observable: el DAO está instanciado.
        assertNotNull(unidadMedidaDAO);
    }

    // -------------------------------------------------------------------------
    // 2. Pruebas de findRange
    // -------------------------------------------------------------------------

    @Test
    void testFindRange_Success() {
        // Arrange
        List<UnidadMedida> expectedList = List.of(mockUnidad);

        when(mockEm.createQuery(eq(JPQL_FIND_RANGE), eq(UnidadMedida.class))).thenReturn(mockQueryUnidadMedida);
        when(mockQueryUnidadMedida.setFirstResult(0)).thenReturn(mockQueryUnidadMedida);
        when(mockQueryUnidadMedida.setMaxResults(10)).thenReturn(mockQueryUnidadMedida);
        when(mockQueryUnidadMedida.getResultList()).thenReturn(expectedList);

        // Act
        List<UnidadMedida> actualList = unidadMedidaDAO.findRange(0, 10);

        // Assert
        assertEquals(expectedList, actualList);
        verify(mockEm, times(1)).createQuery(eq(JPQL_FIND_RANGE), eq(UnidadMedida.class));
        verify(mockQueryUnidadMedida, times(1)).setFirstResult(0);
        verify(mockQueryUnidadMedida, times(1)).setMaxResults(10);
    }

    @Test
    void testFindRange_Exception() {
        // Arrange
        when(mockEm.createQuery(anyString(), eq(UnidadMedida.class))).thenThrow(new RuntimeException("DB Error"));

        // Act
        List<UnidadMedida> actualList = unidadMedidaDAO.findRange(0, 10);

        // Assert
        assertTrue(actualList.isEmpty());
        assertEquals(Collections.emptyList(), actualList);
    }

    // -------------------------------------------------------------------------
    // 3. Pruebas de countByTipoUnidadMedida
    // -------------------------------------------------------------------------

    @Test
    void testCountByTipoUnidadMedida_IdIsNull() {
        // Act
        Long count = unidadMedidaDAO.countByTipoUnidadMedida(null);

        // Assert
        assertEquals(0L, count);
        verifyNoInteractions(mockEm); // Aseguramos que no se intentó consultar la BD
    }

    @Test
    void testCountByTipoUnidadMedida_Success() {
        // Arrange
        Long expectedCount = 5L;

        when(mockEm.createQuery(eq(JPQL_COUNT_BY_TIPO), eq(Long.class))).thenReturn(mockQueryLong);
        when(mockQueryLong.setParameter("idTipo", TIPO_ID)).thenReturn(mockQueryLong);
        when(mockQueryLong.getSingleResult()).thenReturn(expectedCount);

        // Act
        Long actualCount = unidadMedidaDAO.countByTipoUnidadMedida(TIPO_ID);

        // Assert
        assertEquals(expectedCount, actualCount);
        verify(mockQueryLong, times(1)).setParameter("idTipo", TIPO_ID);
        verify(mockQueryLong, times(1)).getSingleResult();
    }

    @Test
    void testCountByTipoUnidadMedida_Exception() {
        // Arrange
        when(mockEm.createQuery(anyString(), eq(Long.class))).thenReturn(mockQueryLong);
        when(mockQueryLong.setParameter(anyString(), any())).thenReturn(mockQueryLong);
        when(mockQueryLong.getSingleResult()).thenThrow(new RuntimeException("DB Error"));

        // Act
        Long actualCount = unidadMedidaDAO.countByTipoUnidadMedida(TIPO_ID);

        // Assert
        assertEquals(0L, actualCount);
    }

    // -------------------------------------------------------------------------
    // 4. Pruebas de findByTipoUnidadMedida
    // -------------------------------------------------------------------------

    @Test
    void testFindByTipoUnidadMedida_IdIsNull() {
        // Act
        List<UnidadMedida> actualList = unidadMedidaDAO.findByTipoUnidadMedida(null, 0, 10);

        // Assert
        assertTrue(actualList.isEmpty());
        verifyNoInteractions(mockEm); // Aseguramos que no se intentó consultar la BD
    }

    @Test
    void testFindByTipoUnidadMedida_Success() {
        // Arrange
        List<UnidadMedida> expectedList = List.of(mockUnidad);

        when(mockEm.createQuery(eq(JPQL_FIND_BY_TIPO), eq(UnidadMedida.class))).thenReturn(mockQueryUnidadMedida);
        when(mockQueryUnidadMedida.setParameter("idTipo", TIPO_ID)).thenReturn(mockQueryUnidadMedida);
        when(mockQueryUnidadMedida.setFirstResult(5)).thenReturn(mockQueryUnidadMedida);
        when(mockQueryUnidadMedida.setMaxResults(10)).thenReturn(mockQueryUnidadMedida);
        when(mockQueryUnidadMedida.getResultList()).thenReturn(expectedList);

        // Act
        List<UnidadMedida> actualList = unidadMedidaDAO.findByTipoUnidadMedida(TIPO_ID, 5, 10);

        // Assert
        assertEquals(expectedList, actualList);
        verify(mockQueryUnidadMedida, times(1)).setParameter("idTipo", TIPO_ID);
        verify(mockQueryUnidadMedida, times(1)).setFirstResult(5);
        verify(mockQueryUnidadMedida, times(1)).setMaxResults(10);
        verify(mockQueryUnidadMedida, times(1)).getResultList();
    }

    @Test
    void testFindByTipoUnidadMedida_Exception() {
        // Arrange
        when(mockEm.createQuery(anyString(), eq(UnidadMedida.class))).thenThrow(new RuntimeException("DB Error"));

        // Act
        List<UnidadMedida> actualList = unidadMedidaDAO.findByTipoUnidadMedida(TIPO_ID, 0, 10);

        // Assert
        assertTrue(actualList.isEmpty());
    }

    // -------------------------------------------------------------------------
    // 5. Pruebas de getUnidadesPorTipoUnidadMedida
    // -------------------------------------------------------------------------

    @Test
    void testGetUnidadesPorTipoUnidadMedida_IdIsNull() {
        // Act
        List<UnidadMedida> actualList = unidadMedidaDAO.getUnidadesPorTipoUnidadMedida(null);

        // Assert
        assertTrue(actualList.isEmpty());
        verify(mockEm, never()).createQuery(anyString(), eq(UnidadMedida.class));
    }

    @Test
    void testGetUnidadesPorTipoUnidadMedida_Success() {
        // Arrange
        List<UnidadMedida> expectedList = List.of(mockUnidad);

        // Uso de getEntityManager() ya que el método llama a getEntityManager().createQuery(...)
        when(mockEm.createQuery(eq(JPQL_GET_UNIDADES_BY_TIPO), eq(UnidadMedida.class))).thenReturn(mockQueryUnidadMedida);
        when(mockQueryUnidadMedida.setParameter("idTipo", TIPO_ID)).thenReturn(mockQueryUnidadMedida);
        when(mockQueryUnidadMedida.getResultList()).thenReturn(expectedList);

        // Act
        List<UnidadMedida> actualList = unidadMedidaDAO.getUnidadesPorTipoUnidadMedida(TIPO_ID);

        // Assert
        assertEquals(expectedList, actualList);
        verify(mockQueryUnidadMedida, times(1)).setParameter("idTipo", TIPO_ID);
        verify(mockQueryUnidadMedida, times(1)).getResultList();
    }

    @Test
    void testGetUnidadesPorTipoUnidadMedida_Exception() {
        // Arrange
        when(mockEm.createQuery(anyString(), eq(UnidadMedida.class))).thenThrow(new RuntimeException("DB Error"));

        // Act
        List<UnidadMedida> actualList = unidadMedidaDAO.getUnidadesPorTipoUnidadMedida(TIPO_ID);

        // Assert
        assertTrue(actualList.isEmpty());
    }
}

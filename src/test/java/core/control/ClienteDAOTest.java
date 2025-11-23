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
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ClienteDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Cliente;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ClienteDAOTest {

    private static final UUID TEST_ID = UUID.randomUUID();

    // Mocks
    @Mock
    EntityManager mockEm;
    @Mock
    CriteriaBuilder mockCb;

    @Mock
    CriteriaQuery<Cliente> mockCqCliente;
    @Mock
    CriteriaQuery<Long> mockCqLong;

    @Mock
    Root<Cliente> mockRoot;

    @Mock
    TypedQuery<Cliente> mockTypedQueryCliente;
    @Mock
    TypedQuery<Long> mockTypedQueryLong;

    // Spy e Inyección
    @Spy
    @InjectMocks
    ClienteDAO clienteDAO;

    Cliente entidad;

    @BeforeEach
    void setUp() {
        entidad = new Cliente();
        entidad.setId(TEST_ID);
        entidad.setNombre("Luis Milla");

        // Configuración de GetEntityManager
        doReturn(mockEm).when(clienteDAO).getEntityManager();

        // Configuración de CriteriaBuilder
        when(mockEm.getCriteriaBuilder()).thenReturn(mockCb);

        // 1. Configuración de FindRange (Cliente)
        when(mockCb.createQuery(eq(Cliente.class))).thenReturn(mockCqCliente);
        when(mockCqCliente.from(eq(Cliente.class))).thenReturn(mockRoot);
        when(mockCqCliente.select(mockRoot)).thenReturn(mockCqCliente);

        when(mockCb.asc(mockRoot.get("id"))).thenReturn(mock(Order.class));
        when(mockCqCliente.orderBy(any(Order.class))).thenReturn(mockCqCliente);

        when(mockEm.createQuery(eq(mockCqCliente))).thenReturn(mockTypedQueryCliente);

        when(mockTypedQueryCliente.setFirstResult(anyInt())).thenReturn(mockTypedQueryCliente);
        when(mockTypedQueryCliente.setMaxResults(anyInt())).thenReturn(mockTypedQueryCliente);


        // 2. Configuraciones para Count (Long)
        when(mockCb.createQuery(eq(Long.class))).thenReturn(mockCqLong);
        when(mockCqLong.from(eq(Cliente.class))).thenReturn(mockRoot);
        when(mockCb.count(mockRoot)).thenReturn(mock(jakarta.persistence.criteria.Expression.class));
        when(mockCqLong.select(any())).thenReturn(mockCqLong);

        when(mockEm.createQuery(eq(mockCqLong))).thenReturn(mockTypedQueryLong);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(Cliente.class, clienteDAO.getEntityClass());
        assertEquals(mockEm, clienteDAO.getEntityManager());
    }

    @Test
    void testCrear_Success() {
        assertDoesNotThrow(() -> clienteDAO.crear(entidad));
        verify(mockEm, times(1)).persist(entidad);
    }

    @Test
    void testCrear_NullInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> clienteDAO.crear(null));
        verify(mockEm, never()).persist(any());
    }

    @Test
    void testCrear_NullEntityManager_ThrowsException() {
        doReturn(null).when(clienteDAO).getEntityManager();
        assertThrows(IllegalStateException.class, () -> clienteDAO.crear(entidad));
    }

    @Test
    void testCrear_PersistenceException_ThrowsRuntimeException() {
        doThrow(new RuntimeException("DB Error")).when(mockEm).persist(entidad);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> clienteDAO.crear(entidad));
        assertTrue(ex.getMessage().contains("No se puede crear la entidad de registro"));
    }

    @Test
    void testFindById_Success() {
        when(mockEm.find(Cliente.class, TEST_ID)).thenReturn(entidad);
        Cliente result = clienteDAO.finById(TEST_ID);
        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        verify(mockEm, times(1)).find(Cliente.class, TEST_ID);
    }

    @Test
    void testFindById_NullInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> clienteDAO.finById(null));
        verify(mockEm, never()).find(any(), any());
    }

    @Test
    void testFindById_NullEntityManager_ThrowsException() {
        doReturn(null).when(clienteDAO).getEntityManager();
        assertThrows(IllegalStateException.class, () -> clienteDAO.finById(TEST_ID));
    }

    @Test
    void testFindById_PersistenceException_ThrowsRuntimeException() {
        doThrow(new RuntimeException("Error de conexión")).when(mockEm).find(Cliente.class, TEST_ID);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> clienteDAO.finById(TEST_ID));
        assertTrue(ex.getMessage().contains("Error al obtener la entidad"));
    }

    @Test
    void testUpdate_Success() {
        Cliente updatedEntity = new Cliente();
        when(mockEm.merge(entidad)).thenReturn(updatedEntity);

        Cliente result = clienteDAO.update(entidad);

        assertEquals(updatedEntity, result);
        verify(mockEm, times(1)).merge(entidad);
    }

    @Test
    void testUpdate_NullInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> clienteDAO.update(null));
        verify(mockEm, never()).merge(any());
    }

    @Test
    void testUpdate_NullEntityManager_ThrowsException() {
        doReturn(null).when(clienteDAO).getEntityManager();
        assertThrows(IllegalStateException.class, () -> clienteDAO.update(entidad));
    }

    @Test
    void testUpdate_PersistenceException_ThrowsRuntimeException() {
        doThrow(new RuntimeException("DB Update Error")).when(mockEm).merge(entidad);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> clienteDAO.update(entidad));
        assertTrue(ex.getMessage().contains("Error al actualizar la entidad"));
    }

    @Test
    void testDelete_ManagedEntity_Success() {
        // Simula que la entidad está gestionada (managed)
        when(mockEm.contains(entidad)).thenReturn(true);

        assertDoesNotThrow(() -> clienteDAO.delete(entidad));
        verify(mockEm, never()).merge(any()); // No debe llamar a merge
        verify(mockEm, times(1)).remove(entidad);
    }

    @Test
    void testDelete_DetachedEntity_Success() {
        Cliente mergedEntity = new Cliente();
        // Simula que la entidad NO está gestionada (detached)
        when(mockEm.contains(entidad)).thenReturn(false);
        when(mockEm.merge(entidad)).thenReturn(mergedEntity);

        assertDoesNotThrow(() -> clienteDAO.delete(entidad));

        verify(mockEm, times(1)).merge(entidad); // Debe llamar a merge primero
        verify(mockEm, times(1)).remove(mergedEntity);
    }

    @Test
    void testDelete_NullInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> clienteDAO.delete(null));
        verify(mockEm, never()).remove(any());
    }

    @Test
    void testDelete_NullEntityManager_ThrowsException() {
        doReturn(null).when(clienteDAO).getEntityManager();
        assertThrows(IllegalStateException.class, () -> clienteDAO.delete(entidad));
    }

    @Test
    void testDelete_PersistenceException_ThrowsRuntimeException() {
        when(mockEm.contains(entidad)).thenReturn(true);
        doThrow(new RuntimeException("DB Delete Error")).when(mockEm).remove(entidad);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clienteDAO.delete(entidad));
        assertTrue(ex.getMessage().contains("Error al eliminar entidad"));
    }

    @Test
    void testFindRange_Success() {
        List<Cliente> expectedList = Arrays.asList(entidad);

        when(mockTypedQueryCliente.getResultList()).thenReturn(expectedList);

        List<Cliente> result = clienteDAO.findRange(0, 10);

        assertEquals(expectedList, result);
        verify(mockTypedQueryCliente, times(1)).setFirstResult(0);
        verify(mockTypedQueryCliente, times(1)).setMaxResults(10);
        // Verificación de llamadas a Criteria API
        verify(mockEm, times(1)).getCriteriaBuilder();
    }

    @Test
    void testFindRange_InvalidFirstParameter_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> clienteDAO.findRange(-1, 10));
        verify(mockEm, never()).getCriteriaBuilder();
    }

    @Test
    void testFindRange_InvalidMaxParameter_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> clienteDAO.findRange(0, 0));
        verify(mockEm, never()).getCriteriaBuilder();
    }

    @Test
    void testFindRange_NullEntityManager_ThrowsException() {
        doReturn(null).when(clienteDAO).getEntityManager();
        assertThrows(IllegalStateException.class, () -> clienteDAO.findRange(0, 10));
    }

    @Test
    void testFindRange_PersistenceException_ThrowsRuntimeException() {
        // Simula un error al obtener el CriteriaBuilder (o cualquier paso posterior)
        when(mockEm.getCriteriaBuilder()).thenThrow(new RuntimeException("DB Access Denied"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clienteDAO.findRange(0, 10));
        assertTrue(ex.getMessage().contains("No se puede acceder al repositorio"));
    }

    @Test
    void testCount_Success() {
        Long expectedCount = 42L;
        when(mockTypedQueryLong.getSingleResult()).thenReturn(expectedCount);

        Long result = clienteDAO.count();

        assertEquals(expectedCount, result);
        verify(mockTypedQueryLong, times(1)).getSingleResult();
        // Verificación de llamadas a Criteria API
        verify(mockEm, times(1)).getCriteriaBuilder();
    }

    @Test
    void testCount_NullEntityManager_ThrowsException() {
        doReturn(null).when(clienteDAO).getEntityManager();
        assertThrows(IllegalStateException.class, () -> clienteDAO.count());
    }

    @Test
    void testCount_PersistenceException_ThrowsRuntimeException() {
        // Simula un error al obtener el CriteriaBuilder (o cualquier paso posterior)
        when(mockEm.getCriteriaBuilder()).thenThrow(new RuntimeException("Error en la consulta COUNT"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> clienteDAO.count());
        assertTrue(ex.getMessage().contains("Error en los parámetros de la consulta"));
    }
}

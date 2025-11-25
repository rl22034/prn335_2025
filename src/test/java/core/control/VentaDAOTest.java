package core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.VentaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Venta;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VentaDAOTest {

    @Mock
    private EntityManager em;

    // Usamos @Spy en el DAO para simular métodos heredados como findRange y count si es necesario
    @InjectMocks
    @Spy
    private VentaDAO ventaDAO;

    // Constantes para los estados
    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_EXCLUIDO = "CANCELADA";

    // Helper para simular la TypedQuery y su ejecución
    @SuppressWarnings("unchecked")
    private TypedQuery<Venta> simularQueryVenta(String jpql) {
        TypedQuery<Venta> query = mock(TypedQuery.class);
        when(em.createQuery(eq(jpql), eq(Venta.class))).thenReturn(query);
        return query;
    }

    // Helper para simular la TypedQuery de COUNT y su ejecución
    @SuppressWarnings("unchecked")
    private TypedQuery<Long> simularQueryCount(String jpql) {
        TypedQuery<Long> query = mock(TypedQuery.class);
        when(em.createQuery(eq(jpql), eq(Long.class))).thenReturn(query);
        return query;
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- 1. Tests de Inicialización y Getters ---

    @Test
    void testConstructorAndGetEntityManager() {
        // Cubre el constructor y el método getEntityManager()
        assertEquals(em, ventaDAO.getEntityManager(),
                "getEntityManager debe retornar el EntityManager inyectado.");
    }

    // --- 2. Tests de findByEstado ---

    private static final String FIND_BY_ESTADO_JPQL =
            "SELECT v FROM Venta v WHERE v.estado = :estado ORDER BY v.fecha DESC";

    @Test
    void testFindByEstado_Success() {
        // Arrange
        TypedQuery<Venta> mockQuery = simularQueryVenta(FIND_BY_ESTADO_JPQL);
        List<Venta> listaEsperada = Collections.singletonList(new Venta());

        when(mockQuery.setParameter(eq("estado"), eq(ESTADO_PENDIENTE))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(listaEsperada);

        // Act (Cubre el bloque try{} de findByEstado)
        List<Venta> resultado = ventaDAO.findByEstado(ESTADO_PENDIENTE);

        // Assert
        assertFalse(resultado.isEmpty(), "Debe retornar una lista con resultados.");
        verify(mockQuery, times(1)).setParameter("estado", ESTADO_PENDIENTE);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void testFindByEstado_NullOrEmptyState() {
        // Act y Assert (Cubre la condición 'if (estado == null || estado.trim().isEmpty())')
        assertTrue(ventaDAO.findByEstado(null).isEmpty(), "ID nulo debe retornar lista vacía.");
        assertTrue(ventaDAO.findByEstado("").isEmpty(), "ID vacío debe retornar lista vacía.");
        assertTrue(ventaDAO.findByEstado("   ").isEmpty(), "ID en blanco debe retornar lista vacía.");

        // Verificamos que el EntityManager no fue usado en estos casos
        verify(em, never()).createQuery(anyString(), any());
    }

    @Test
    void testFindByEstado_Exception() {
        // Arrange (Cubre el bloque catch{} de findByEstado)
        when(em.createQuery(eq(FIND_BY_ESTADO_JPQL), eq(Venta.class)))
                .thenThrow(new RuntimeException("Simulated DB error"));

        // Act
        List<Venta> resultado = ventaDAO.findByEstado(ESTADO_PENDIENTE);

        // Assert
        assertTrue(resultado.isEmpty(), "Debe retornar lista vacía tras una excepción.");
    }

    // --- 3. Tests de findExcluyendoEstado ---

    private static final String FIND_EXCLUYENDO_ESTADO_JPQL =
            "SELECT v FROM Venta v WHERE v.estado <> :estado ORDER BY v.fecha DESC";

    @Test
    void testFindExcluyendoEstado_Success() {
        // Arrange
        int first = 10;
        int pageSize = 20;
        TypedQuery<Venta> mockQuery = simularQueryVenta(FIND_EXCLUYENDO_ESTADO_JPQL);
        List<Venta> listaEsperada = Collections.singletonList(new Venta());

        when(mockQuery.setParameter(eq("estado"), eq(ESTADO_EXCLUIDO))).thenReturn(mockQuery);
        when(mockQuery.setFirstResult(first)).thenReturn(mockQuery);
        when(mockQuery.setMaxResults(pageSize)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(listaEsperada);

        // Act (Cubre el bloque try{} de findExcluyendoEstado)
        List<Venta> resultado = ventaDAO.findExcluyendoEstado(ESTADO_EXCLUIDO, first, pageSize);

        // Assert
        assertFalse(resultado.isEmpty(), "Debe retornar una lista con resultados.");
        verify(mockQuery, times(1)).setParameter("estado", ESTADO_EXCLUIDO);
        verify(mockQuery, times(1)).setFirstResult(first);
        verify(mockQuery, times(1)).setMaxResults(pageSize);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void testFindExcluyendoEstado_NullOrEmptyExclusion() throws Exception {
        // Arrange (Cubre la condición 'if (estadoExcluido == null || estadoExcluido.trim().isEmpty())')
        int first = 0;
        int pageSize = 5;
        List<Venta> listaEsperada = Collections.singletonList(new Venta());

        // Simular que findRange (el método heredado) es llamado
        doReturn(listaEsperada).when(ventaDAO).findRange(eq(first), eq(pageSize));

        // Act & Assert
        assertEquals(1, ventaDAO.findExcluyendoEstado(null, first, pageSize).size(),
                "Debe llamar a findRange con estado nulo.");
        assertEquals(1, ventaDAO.findExcluyendoEstado(" ", first, pageSize).size(),
                "Debe llamar a findRange con estado en blanco.");

        // Verificamos que findRange fue llamado dos veces (para las dos llamadas)
        verify(ventaDAO, times(2)).findRange(eq(first), eq(pageSize));
        verify(em, never()).createQuery(anyString(), any());
    }

    @Test
    void testFindExcluyendoEstado_Exception() {
        // Arrange (Cubre el bloque catch{} de findExcluyendoEstado)
        when(em.createQuery(eq(FIND_EXCLUYENDO_ESTADO_JPQL), eq(Venta.class)))
                .thenThrow(new RuntimeException("Simulated DB error"));

        // Act
        List<Venta> resultado = ventaDAO.findExcluyendoEstado(ESTADO_EXCLUIDO, 0, 10);

        // Assert
        assertTrue(resultado.isEmpty(), "Debe retornar lista vacía tras una excepción.");
    }

    // --- 4. Tests de countExcluyendoEstado ---

    private static final String COUNT_EXCLUYENDO_ESTADO_JPQL =
            "SELECT COUNT(v) FROM Venta v WHERE v.estado <> :estado";

    @Test
    void testCountExcluyendoEstado_Success() {
        // Arrange
        Long expectedCount = 5L;
        TypedQuery<Long> mockQuery = simularQueryCount(COUNT_EXCLUYENDO_ESTADO_JPQL);

        when(mockQuery.setParameter(eq("estado"), eq(ESTADO_EXCLUIDO))).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(expectedCount);

        // Act (Cubre el bloque try{} de countExcluyendoEstado)
        Long resultado = ventaDAO.countExcluyendoEstado(ESTADO_EXCLUIDO);

        // Assert
        assertEquals(expectedCount, resultado, "Debe retornar el conteo esperado.");
        verify(mockQuery, times(1)).setParameter("estado", ESTADO_EXCLUIDO);
        verify(mockQuery, times(1)).getSingleResult();
    }

    @Test
    void testCountExcluyendoEstado_NullOrEmptyExclusion() throws Exception {
        // Arrange (Cubre la condición 'if (estadoExcluido == null || estadoExcluido.trim().isEmpty())')
        Long totalCount = 100L;

        // Simular que count() (el método heredado) es llamado
        doReturn(totalCount).when(ventaDAO).count();

        // Act & Assert
        assertEquals(totalCount, ventaDAO.countExcluyendoEstado(null),
                "Debe llamar a count() con estado nulo.");
        assertEquals(totalCount, ventaDAO.countExcluyendoEstado(" "),
                "Debe llamar a count() con estado en blanco.");

        // Verificamos que count() fue llamado dos veces (para las dos llamadas)
        verify(ventaDAO, times(2)).count();
        verify(em, never()).createQuery(anyString(), eq(Long.class));
    }

    @Test
    void testCountExcluyendoEstado_Exception() {
        // Arrange (Cubre el bloque catch{} de countExcluyendoEstado)
        when(em.createQuery(eq(COUNT_EXCLUYENDO_ESTADO_JPQL), eq(Long.class)))
                .thenThrow(new RuntimeException("Simulated DB error"));

        // Act
        Long resultado = ventaDAO.countExcluyendoEstado(ESTADO_EXCLUIDO);

        // Assert
        assertEquals(0L, resultado, "Debe retornar 0L tras una excepción.");
    }
}

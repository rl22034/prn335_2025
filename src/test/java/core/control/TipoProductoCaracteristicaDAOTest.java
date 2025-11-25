package core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoProductoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProductoCaracteristica;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TipoProductoCaracteristicaDAOTest {

    @Mock
    // Simula el EntityManager inyectado
    private EntityManager em;

    @InjectMocks
    // Inyecta el mock de EntityManager en la instancia de la clase DAO
    private TipoProductoCaracteristicaDAO tipoProductoCaracteristicaDAO;

    // Constante con el JPQL esperado
    private static final String JPQL_QUERY =
            "SELECT tpc FROM TipoProductoCaracteristica tpc " +
                    "LEFT JOIN FETCH tpc.idCaracteristica c " +
                    "LEFT JOIN FETCH c.idTipoUnidadMedida " +
                    "WHERE tpc.idTipoProducto.id = :idTipo " +
                    "ORDER BY tpc.id";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- 1. Tests de Inicialización ---

    @Test
    void testConstructorAndGetEntityManager() {
        // Verifica que getEntityManager devuelve el mock inyectado.
        // Esto cubre la lógica del constructor y el método getEntityManager().
        assertEquals(em, tipoProductoCaracteristicaDAO.getEntityManager(),
                "getEntityManager debe retornar el EntityManager inyectado.");
    }

    // --- 2. Tests del Método findByTipoProducto ---

    @Test
    void testFindByTipoProducto_NullId() {
        // Ejecución con ID nulo (Cubre la condición 'if (idTipoProducto == null)')
        List<TipoProductoCaracteristica> resultado = tipoProductoCaracteristicaDAO.findByTipoProducto(null);

        // Aserción: Debe retornar una lista vacía
        assertNotNull(resultado, "El resultado no debe ser nulo.");
        assertTrue(resultado.isEmpty(), "Debe retornar una lista vacía para ID nulo.");

        // Verificación: Asegura que NINGÚN método de EntityManager fue llamado
        verifyNoInteractions(em);
    }

    @Test
    void testFindByTipoProducto_Success() {
        // Arrange
        Long idTipoProducto = 5L;
        @SuppressWarnings("unchecked")
        TypedQuery<TipoProductoCaracteristica> mockQuery = mock(TypedQuery.class);
        TipoProductoCaracteristica tpc = new TipoProductoCaracteristica();
        List<TipoProductoCaracteristica> listaEsperada = Collections.singletonList(tpc);

        // Simular las llamadas encadenadas
        when(em.createQuery(eq(JPQL_QUERY), eq(TipoProductoCaracteristica.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter("idTipo", idTipoProducto)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(listaEsperada);

        // Act (Cubre la lógica dentro del bloque 'try')
        List<TipoProductoCaracteristica> resultado = tipoProductoCaracteristicaDAO.findByTipoProducto(idTipoProducto);

        // Assert
        assertNotNull(resultado, "El resultado no debe ser nulo.");
        assertFalse(resultado.isEmpty(), "Debe retornar la lista de resultados.");
        assertEquals(1, resultado.size());

        // Verificación: Asegura que los métodos correctos fueron llamados
        verify(em, times(1)).createQuery(eq(JPQL_QUERY), eq(TipoProductoCaracteristica.class));
        verify(mockQuery, times(1)).setParameter("idTipo", idTipoProducto);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void testFindByTipoProducto_ExceptionHandling() {
        // Arrange
        Long idTipoProducto = 10L;
        // Simular que la creación de la consulta o la ejecución lanza una excepción
        // (Cubre la lógica dentro del bloque 'catch')
        when(em.createQuery(anyString(), eq(TipoProductoCaracteristica.class))).thenThrow(new RuntimeException("Error simulado de base de datos"));

        // Act
        List<TipoProductoCaracteristica> resultado = tipoProductoCaracteristicaDAO.findByTipoProducto(idTipoProducto);

        // Assert: Debe retornar una lista vacía tras la excepción
        assertNotNull(resultado, "El resultado no debe ser nulo.");
        assertTrue(resultado.isEmpty(), "Debe retornar una lista vacía tras una excepción.");

        // Verificación: Asegura que se intentó crear la consulta
        verify(em, times(1)).createQuery(anyString(), eq(TipoProductoCaracteristica.class));
    }
}
package core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.VentaDetalleDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.VentaDetalle;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VentaDetalleDAOTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private VentaDetalleDAO ventaDetalleDAO;

    // Constante para la JPQL esperada
    private static final String FIND_BY_VENTA_JPQL =
            "SELECT vd FROM VentaDetalle vd WHERE vd.idVenta.id = :idVenta";

    // UUID de prueba
    private final UUID TEST_VENTA_ID = UUID.randomUUID();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- 1. Tests de Inicialización y Getters ---

    @Test
    void testConstructorAndGetEntityManager() {
        // Cubre el constructor y el método getEntityManager()
        assertEquals(em, ventaDetalleDAO.getEntityManager(),
                "getEntityManager debe retornar el EntityManager inyectado.");
    }

    // --- 2. Tests de getDetallesPorVenta ---

    @Test
    void testGetDetallesPorVenta_NullId() {
        // Act (Cubre la condición 'if (idVenta == null)')
        List<VentaDetalle> resultado = ventaDetalleDAO.getDetallesPorVenta(null);

        // Assert
        assertNotNull(resultado, "El resultado no debe ser nulo.");
        assertTrue(resultado.isEmpty(), "Debe retornar una lista vacía para ID nulo.");

        // Verificación: Asegura que NINGÚN método de EntityManager fue llamado
        verifyNoInteractions(em);
    }

    @Test
    void testGetDetallesPorVenta_Success() {
        // Arrange
        @SuppressWarnings("unchecked")
        TypedQuery<VentaDetalle> mockQuery = mock(TypedQuery.class);
        VentaDetalle detalle = new VentaDetalle();
        List<VentaDetalle> listaEsperada = Collections.singletonList(detalle);

        // Simular las llamadas encadenadas
        when(em.createQuery(eq(FIND_BY_VENTA_JPQL), eq(VentaDetalle.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("idVenta"), eq(TEST_VENTA_ID))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(listaEsperada);

        // Act (Cubre el bloque try{} de getDetallesPorVenta)
        List<VentaDetalle> resultado = ventaDetalleDAO.getDetallesPorVenta(TEST_VENTA_ID);

        // Assert
        assertNotNull(resultado, "El resultado no debe ser nulo.");
        assertFalse(resultado.isEmpty(), "Debe retornar la lista de resultados.");
        assertEquals(1, resultado.size());

        // Verificación: Asegura que los métodos correctos fueron llamados
        verify(em, times(1)).createQuery(eq(FIND_BY_VENTA_JPQL), eq(VentaDetalle.class));
        verify(mockQuery, times(1)).setParameter("idVenta", TEST_VENTA_ID);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void testGetDetallesPorVenta_ExceptionHandling() {
        // Arrange
        // Simular que la creación de la consulta o la ejecución lanza una excepción
        // (Cubre el bloque catch{} de getDetallesPorVenta)
        when(em.createQuery(anyString(), eq(VentaDetalle.class))).thenThrow(new RuntimeException("Error simulado de base de datos"));

        // Act
        List<VentaDetalle> resultado = ventaDetalleDAO.getDetallesPorVenta(TEST_VENTA_ID);

        // Assert: Debe retornar una lista vacía tras la excepción
        assertTrue(resultado.isEmpty(), "Debe retornar una lista vacía tras una excepción.");

        // Verificación: Asegura que se intentó crear la consulta
        verify(em, times(1)).createQuery(anyString(), eq(VentaDetalle.class));
    }
}

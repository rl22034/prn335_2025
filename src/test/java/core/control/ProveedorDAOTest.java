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
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProveedorDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Proveedor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class ProveedorDAOTest {

    private Proveedor mockProveedor;
    private List<Proveedor> expectedList;

    @Mock
    EntityManager mockEm;

    @Mock
    TypedQuery<Proveedor> mockTypedQuery;

    // Spy e Inyección
    @Spy
    @InjectMocks
    ProveedorDAO dao;

    @BeforeEach
    void setUp() {
        mockProveedor = new Proveedor();
        mockProveedor.setId(1);
        mockProveedor.setNombre("Proveedor Activo S.A.");
        expectedList = Arrays.asList(mockProveedor);

        // Configuración para el método findProveedoresActivos()
        // 1. Aseguramos que createQuery devuelva nuestro TypedQuery mock
        when(mockEm.createQuery(
                eq("SELECT p FROM Proveedor p WHERE p.activo = true ORDER BY p.nombre"),
                eq(Proveedor.class)
        )).thenReturn(mockTypedQuery);

        // 2. Aseguramos que el TypedQuery devuelva la lista de resultados
        when(mockTypedQuery.getResultList()).thenReturn(expectedList);

        // Configuración de getEntityManager() para cubrir la implementación
        doReturn(mockEm).when(dao).getEntityManager();
    }

    // ----------------------------------------------------
    // Test del Constructor y Getters
    // ----------------------------------------------------

    @Test
    void testConstructorAndGetters() {
        // Verifica que el constructor pase la clase correcta al padre
        assertEquals(Proveedor.class, dao.getEntityClass());
        // Verifica que el método getEntityManager devuelva el mock inyectado
        assertEquals(mockEm, dao.getEntityManager());
    }

    // ----------------------------------------------------
    // Test del Método findProveedoresActivos()
    // ----------------------------------------------------

    @Test
    void testFindProveedoresActivos_Success() {
        List<Proveedor> result = dao.findProveedoresActivos();

        // 1. Verifica el resultado
        assertEquals(expectedList, result);
        assertFalse(result.isEmpty());

        // 2. Verifica la interacción con JPA
        verify(mockEm, times(1)).createQuery(
                eq("SELECT p FROM Proveedor p WHERE p.activo = true ORDER BY p.nombre"),
                eq(Proveedor.class)
        );
        verify(mockTypedQuery, times(1)).getResultList();
    }

    @Test
    void testFindProveedoresActivos_ReturnsEmptyList() {
        // Configurar el mock para devolver una lista vacía
        when(mockTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        List<Proveedor> result = dao.findProveedoresActivos();

        // 1. Verifica el resultado
        assertTrue(result.isEmpty());

        // 2. Verifica la interacción
        verify(mockTypedQuery, times(1)).getResultList();
    }

    @Test
    void testFindProveedoresActivos_PersistenceException_ReturnsEmptyList() {
        // Simular que createQuery lanza una excepción (cubre el bloque catch)
        when(mockEm.createQuery(anyString(), eq(Proveedor.class)))
                .thenThrow(new RuntimeException("Error de base de datos simulado"));

        List<Proveedor> result = dao.findProveedoresActivos();

        // 1. Verifica el resultado (debe ser lista vacía según el catch)
        assertTrue(result.isEmpty());

        // 2. Verifica la interacción
        verify(mockEm, times(1)).createQuery(anyString(), eq(Proveedor.class));
        verify(mockTypedQuery, never()).getResultList(); // No se llega a llamar
    }
}
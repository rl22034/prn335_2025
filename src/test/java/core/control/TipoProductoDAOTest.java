package core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProducto;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TipoProductoDAOTest {

    @Mock
    private EntityManager em;

    // Cambiado de @InjectMocks a @Spy para poder simular métodos que llaman a 'super'
    // sin tener que simular toda la cadena JPA (EntityManager -> Query, etc.)
    @InjectMocks
    @Spy // Usamos @Spy para simular métodos propios como finById y findRange
    private TipoProductoDAO tipoProductoDAO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Helper para crear un TipoProducto con ID y Padre
     */
    private TipoProducto crearTipoProducto(Long id, Long idPadre) {
        TipoProducto tp = new TipoProducto();
        tp.setId(id);
        if (idPadre != null) {
            // Se crea una entidad TipoProducto parcial solo con el ID para la relación
            TipoProducto padre = new TipoProducto();
            padre.setId(idPadre);
            tp.setIdTipoProductoPadre(padre);
        }
        tp.setNombre("TP-" + id);
        return tp;
    }

    // --- 1. Tests de Inicialización y Getters ---
    // (Permanecen sin cambios, pero ahora usan el @Spy)

    @Test
    void testConstructorAndGetEntityManager() {
        // Cubre el constructor y el método getEntityManager()
        assertEquals(em, tipoProductoDAO.getEntityManager(),
                "getEntityManager debe retornar el EntityManager inyectado.");
    }

    // --- 2. Tests de findRange (Sobrescrito) ---

    private static final String FIND_RANGE_JPQL =
            "SELECT DISTINCT tp FROM TipoProducto tp LEFT JOIN FETCH tp.idTipoProductoPadre ORDER BY tp.id";

    @Test
    void testFindRange_Success() {
        // Arrange
        @SuppressWarnings("unchecked")
        TypedQuery<TipoProducto> mockQuery = mock(TypedQuery.class);
        List<TipoProducto> listaEsperada = Arrays.asList(
                crearTipoProducto(1L, null),
                crearTipoProducto(2L, 1L)
        );

        when(em.createQuery(eq(FIND_RANGE_JPQL), eq(TipoProducto.class))).thenReturn(mockQuery);
        when(mockQuery.setFirstResult(0)).thenReturn(mockQuery);
        when(mockQuery.setMaxResults(10)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(listaEsperada);

        // Act
        List<TipoProducto> resultado = tipoProductoDAO.findRange(0, 10);

        // Assert (Cubre el bloque try{} de findRange)
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(mockQuery, times(1)).setFirstResult(0);
        verify(mockQuery, times(1)).setMaxResults(10);
    }

    @Test
    void testFindRange_Exception() {
        // Arrange
        // Usar doThrow para simular la excepción al crear la consulta
        doThrow(new RuntimeException("DB Error"))
                .when(em).createQuery(eq(FIND_RANGE_JPQL), eq(TipoProducto.class));

        // Act & Assert (Cubre el bloque catch{} de findRange)
        assertThrows(RuntimeException.class, () -> {
            tipoProductoDAO.findRange(0, 10);
        }, "Debe lanzar RuntimeException al fallar la DB.");
    }

    // --- 3. Tests de findHijos ---

    @Test
    void testFindHijos_NullId() {
        // Act (Cubre la condición 'if (idPadre == null)')
        List<TipoProducto> resultado = tipoProductoDAO.findHijos(null);

        // Assert
        assertTrue(resultado.isEmpty());
        // Verificación de que no se tocó el EntityManager
        verifyNoInteractions(em);
    }

    @Test
    void testFindHijos_Success() {
        // Arrange
        Long idPadre = 10L;
        @SuppressWarnings("unchecked")
        TypedQuery<TipoProducto> mockQuery = mock(TypedQuery.class);
        List<TipoProducto> listaEsperada = Collections.singletonList(crearTipoProducto(11L, idPadre));

        // Simular la consulta JPQL de findHijos
        when(em.createQuery(anyString(), eq(TipoProducto.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), eq(idPadre))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(listaEsperada);

        // Act (Cubre el bloque try{} de findHijos)
        List<TipoProducto> resultado = tipoProductoDAO.findHijos(idPadre);

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(mockQuery, times(1)).setParameter("idPadre", idPadre);
    }

    @Test
    void testFindHijos_Exception() {
        // Arrange
        // Simular que la creación de la consulta lanza una excepción (cubre el catch)
        doThrow(new RuntimeException("DB Error"))
                .when(em).createQuery(anyString(), eq(TipoProducto.class));

        // Act (Cubre el bloque catch{} de findHijos)
        List<TipoProducto> resultado = tipoProductoDAO.findHijos(5L);

        // Assert
        assertTrue(resultado.isEmpty());
    }

    // --- 4. Tests de findValidosParaPadre (Lógica Compleja y Recursiva) ---

    /**
     * Helper para simular findRange y finById en el DAO @Spy.
     * Esto resuelve los errores de RuntimeException en finById.
     */
    private void simularDependenciesForPadre(Long idActual, List<TipoProducto> todos) throws Exception {
        // 1. Simular findRange para que devuelva la lista completa (usado en el caso null y el caso principal)
        doReturn(todos)
                .when(tipoProductoDAO).findRange(0, 1000);

        // 2. Simular finById si el idActual no es nulo
        if (idActual != null) {
            TipoProducto nodoActual = todos.stream()
                    .filter(tp -> tp.getId().equals(idActual))
                    .findFirst().orElse(null);

            // Usar doReturn para simular el método finById del DAO (que es la dependencia)
            doReturn(nodoActual)
                    .when(tipoProductoDAO).finById(eq(idActual));
        }
    }

    @Test
    void testFindValidosParaPadre_NullId() throws Exception {
        // Arrange (Cubre la condición 'if (idActual == null)')
        List<TipoProducto> todos = Arrays.asList(
                crearTipoProducto(1L, null), crearTipoProducto(2L, 1L)
        );
        // Simular el findRange para este caso
        simularDependenciesForPadre(null, todos);

        // Act
        List<TipoProducto> resultado = tipoProductoDAO.findValidosParaPadre(null);

        // Assert: Si es null, todos son válidos
        assertEquals(2, resultado.size());
        assertTrue(resultado.containsAll(todos));
        // Verificamos que se llamó a findRange
        verify(tipoProductoDAO, times(1)).findRange(0, 1000);
    }

    @Test
    void testFindValidosParaPadre_NodoActualNotFound() throws Exception {
        // Arrange
        Long idActual = 99L; // Un ID que no existe en la lista 'todos'
        List<TipoProducto> todos = Arrays.asList(
                crearTipoProducto(1L, null), crearTipoProducto(2L, 1L)
        );

        // Simular finById devolviendo null (Cubre la condición 'if (nodoActual == null)')
        simularDependenciesForPadre(idActual, todos);

        // Act
        List<TipoProducto> resultado = tipoProductoDAO.findValidosParaPadre(idActual);

        // Assert: Si el nodo actual no se encuentra, todos son válidos
        assertEquals(2, resultado.size());
        assertTrue(resultado.containsAll(todos));

        verify(tipoProductoDAO, times(1)).finById(idActual);
    }

    @Test
    void testFindValidosParaPadre_ExcluyeActualYDescendientes() throws Exception {
        // Arrange
        Long idActual = 1L;
        // Jerarquía: A(1) -> B(2) -> C(3) | D(4)
        // Válido: D(4)
        // Excluidos: A(1), B(2), C(3)
        List<TipoProducto> todos = Arrays.asList(
                crearTipoProducto(1L, null), // Padre (A) - ID Actual
                crearTipoProducto(2L, 1L),  // Hijo (B)
                crearTipoProducto(3L, 2L),  // Nieto (C)
                crearTipoProducto(4L, null)  // Otro Padre (D) - Válido
        );

        simularDependenciesForPadre(idActual, todos);

        // Act (Cubre toda la lógica de obtención recursiva)
        List<TipoProducto> resultado = tipoProductoDAO.findValidosParaPadre(idActual);

        // Assert: Solo el nodo D(4) debería ser válido.
        Set<Long> idsResultado = resultado.stream().map(TipoProducto::getId).collect(Collectors.toSet());

        assertEquals(1, resultado.size(), "Solo D(4) debe ser válido como padre.");
        assertTrue(idsResultado.contains(4L));
        assertFalse(idsResultado.contains(1L)); // Excluye A (actual)
        assertFalse(idsResultado.contains(2L)); // Excluye B (descendiente)
        assertFalse(idsResultado.contains(3L)); // Excluye C (descendiente)
    }

    @Test
    void testFindValidosParaPadre_Exception() throws Exception {
        // Arrange (Cubre el bloque catch{} de findValidosParaPadre)
        Long idActual = 1L;

        // Simular que findRange (la primera dependencia) lanza una excepción.
        doThrow(new RuntimeException("Simulated FindRange Error"))
                .when(tipoProductoDAO).findRange(anyInt(), anyInt());

        // Act
        List<TipoProducto> resultado = tipoProductoDAO.findValidosParaPadre(idActual);

        // Assert: Debe retornar una lista vacía
        assertTrue(resultado.isEmpty());
        // Verificamos que findRange fue invocado
        verify(tipoProductoDAO, times(1)).findRange(anyInt(), anyInt());
    }
}
package core.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoAlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoAlmacen;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TipoAlmacenDAOTest {

    @Mock
    // Inyectamos el mock del EntityManager
    public EntityManager em;

    @InjectMocks
    // La instancia real de la clase que vamos a probar
    private TipoAlmacenDAO tipoAlmacenDAO;

    @BeforeEach
    public void setUp() {
        // Inicializa los mocks y la inyección en tipoAlmacenDAO
        MockitoAnnotations.openMocks(this);
    }

    // --- 1. Tests de Inicialización y Métodos Abstractos ---

    @Test
    void testConstructorAndGetEntityManager() {
        // El constructor ya se ejecuta en @BeforeEach a través de @InjectMocks.
        // Verificamos que getEntityManager devuelve el mock inyectado.
        // Esto cubre el 100% de la lógica de inicialización y el método getEntityManager().
        assertEquals(em, tipoAlmacenDAO.getEntityManager(),
                "El método getEntityManager debe retornar el EntityManager inyectado.");
    }

    // --- 2. Tests de Métodos Heredados (Asegurando el llamado a super) ---

    @Test
    void testCrear() {
        // Arrange
        TipoAlmacen mockEntidad = mock(TipoAlmacen.class);

        // Act
        // Como 'crear' llama a super.crear(entidad), usamos doNothing para evitar errores
        // y verificamos que el EntityManager se haya usado para la persistencia.
        tipoAlmacenDAO.crear(mockEntidad);

        // Assert: Verificamos que la llamada al método EntityManager.persist() fue invocada.
        // Esto prueba indirectamente el comportamiento del super.crear(entidad).
        verify(em, times(1)).persist(mockEntidad);
    }

    @Test
    void testDelete() {
        // Arrange
        TipoAlmacen mockEntidad = mock(TipoAlmacen.class);
        when(em.contains(mockEntidad)).thenReturn(true);

        // Act
        // Como 'delete' llama a super.delete(entidad), verificamos que el EntityManager
        // haya sido usado para la operación de eliminación.
        tipoAlmacenDAO.delete(mockEntidad);

        // Assert: Verificamos que la llamada al método EntityManager.remove() fue invocada.
        // Esto prueba indirectamente el comportamiento del super.delete(entidad).
        verify(em, times(1)).remove(mockEntidad);
    }

    @Test
    void testFinById() {
        // Arrange
        Integer id = 1;
        TipoAlmacen mockEntidad = mock(TipoAlmacen.class);
        // Configuramos el EntityManager para que devuelva el mock al llamar a find
        when(em.find(TipoAlmacen.class, id)).thenReturn(mockEntidad);

        // Act
        TipoAlmacen resultado = tipoAlmacenDAO.finById(id);

        // Assert: Verificamos que find fue llamado y que el resultado es el esperado
        // Esto prueba indirectamente el comportamiento del super.finById(id).
        verify(em, times(1)).find(TipoAlmacen.class, id);
        assertEquals(mockEntidad, resultado);
    }

    // --- 3. Tests de Métodos Específicos de la Clase DAO ---

    @Test
    void testFindTiposActivos_Success() {
        // Arrange
        @SuppressWarnings("unchecked")
        TypedQuery<TipoAlmacen> mockQuery = mock(TypedQuery.class);
        List<TipoAlmacen> listaEsperada = Arrays.asList(
                new TipoAlmacen(), new TipoAlmacen()
        );

        // Simulamos la cadena de llamadas: em.createQuery(...) -> getResultList()
        when(em.createQuery(anyString(), eq(TipoAlmacen.class))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(listaEsperada);

        // Act
        List<TipoAlmacen> resultado = tipoAlmacenDAO.findTiposActivos();

        // Assert: Verificamos la ejecución correcta
        // Esto cubre la lógica dentro del try{}
        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        // Verificamos que la consulta se haya creado con el String correcto (buena práctica)
        verify(em, times(1)).createQuery(
                eq("SELECT t FROM TipoAlmacen t WHERE t.activo = true ORDER BY t.nombre"),
                eq(TipoAlmacen.class)
        );
    }

    @Test
    void testFindTiposActivos_ExceptionHandling() {
        // Arrange
        // Simulamos que em.createQuery lanza una RuntimeException, cubriendo el catch{}
        when(em.createQuery(anyString(), eq(TipoAlmacen.class))).thenThrow(new RuntimeException("Simulated DB error"));

        // Act
        List<TipoAlmacen> resultado = tipoAlmacenDAO.findTiposActivos();

        // Assert: Verificamos que el manejo de errores devuelve una lista vacía
        // Esto cubre la lógica dentro del catch{}
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        // Verificamos que se intentó crear la query
        verify(em, times(1)).createQuery(anyString(), eq(TipoAlmacen.class));
    }
}

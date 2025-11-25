package core.control;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoUnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoUnidadMedida;

import static org.junit.jupiter.api.Assertions.*;

public class TipoUnidadMedidaDAOTest {

    @Mock
    // Simula el EntityManager inyectado por el contenedor EJB
    private EntityManager em;

    @InjectMocks
    // Inyecta el mock de EntityManager en la instancia de la clase DAO
    private TipoUnidadMedidaDAO tipoUnidadMedidaDAO;

    @BeforeEach
    public void setUp() {
        // Inicializa los mocks antes de cada prueba
        MockitoAnnotations.openMocks(this);
    }

    // --- Test de Cobertura 100% de DAO ---

    @Test
    void testConstructorAndGetEntityManager() {
        // El constructor (línea 16: super(TipoUnidadMedida.class);) ya se ejecutó en @InjectMocks/@BeforeEach.

        // Act: Llamar al método getEntityManager() (líneas 19-21)
        EntityManager resultado = tipoUnidadMedidaDAO.getEntityManager();

        // Assert:
        // 1. Verificar que el EntityManager devuelto es el mock inyectado.
        // Esto confirma que el constructor se ejecutó correctamente y que el método getEntityManager() funciona.
        assertEquals(em, resultado,
                "getEntityManager debe retornar el EntityManager inyectado.");
    }

    // --- Test de Entidad TipoUnidadMedida (Para asegurar la funcionalidad de getters/setters/equals) ---

    @Test
    void testTipoUnidadMedidaEntity_Accessors() {
        // Arrange
        TipoUnidadMedida tum = new TipoUnidadMedida();

        // Data de prueba
        Integer id = 1;
        String nombre = "Peso";
        Boolean activo = true;
        String unidadBase = "Kilogramo";
        String comentarios = "Unidad para medir masa";

        // Act (Setters)
        tum.setId(id);
        tum.setNombre(nombre);
        tum.setActivo(activo);
        tum.setUnidadBase(unidadBase);
        tum.setComentarios(comentarios);

        // Assert (Getters)
        assertEquals(id, tum.getId());
        assertEquals(nombre, tum.getNombre());
        assertEquals(activo, tum.getActivo());
        assertEquals(unidadBase, tum.getUnidadBase());
        assertEquals(comentarios, tum.getComentarios());

        // Verificar que la lista de unidadMedidas se inicializa (aunque no se use en el DAO)
        assertNotNull(tum.getUnidadMedidas());
        assertTrue(tum.getUnidadMedidas().isEmpty());
    }

    @Test
    void testTipoUnidadMedidaEntity_EqualsAndHashCode() {
        // Arrange
        TipoUnidadMedida tum1 = new TipoUnidadMedida();
        tum1.setId(10);
        TipoUnidadMedida tum2 = new TipoUnidadMedida();
        tum2.setId(10);
        TipoUnidadMedida tum3 = new TipoUnidadMedida();
        tum3.setId(20);
        TipoUnidadMedida tumNullId = new TipoUnidadMedida();

        // Assert Equals
        assertTrue(tum1.equals(tum2), "Objetos con el mismo ID deben ser iguales.");
        assertEquals(tum1.hashCode(), tum2.hashCode(), "HashCodes deben ser iguales para objetos iguales.");
        assertFalse(tum1.equals(tum3), "Objetos con diferente ID no deben ser iguales.");

        // Assert Null ID
        // Aunque la lógica del equals es ligeramente inusual (si el ID es nulo, siempre es false),
        // probamos el contrato estándar.
        assertFalse(tum1.equals(tumNullId), "Comparar con objeto con ID nulo debe ser falso.");
        assertTrue(tum1.equals(tum1), "Debe ser igual a sí mismo.");
    }
}

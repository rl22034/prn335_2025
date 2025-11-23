package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoAlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoAlmacen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoAlmacenFrmTest {

    @Mock
    private TipoAlmacenDAO tipoAlmacenDAO;

    @InjectMocks
    private TipoAlmacenFrm tipoAlmacenFrm;

    private TipoAlmacen tipoAlmacen;

    @BeforeEach
    void setUp() {
        tipoAlmacen = new TipoAlmacen();
        tipoAlmacen.setNombre("Almacén Principal");
        tipoAlmacen.setActivo(true);
    }

    @Test
    void testObtenerDAO() {
        // When
        TipoAlmacenDAO resultado = tipoAlmacenFrm.obtenerDAO();

        // Then
        assertNotNull(resultado);
        assertEquals(tipoAlmacenDAO, resultado);
    }

    @Test
    void testValidarAntesDeCrear_ConNombreValido() {
        // Given
        tipoAlmacen.setNombre("Almacén Válido");

        // When & Then
        assertDoesNotThrow(() -> tipoAlmacenFrm.validarAntesDeCrear(tipoAlmacen));
    }

    @Test
    void testValidarAntesDeCrear_ConNombreNulo() {
        // Given
        tipoAlmacen.setNombre(null);

        // When & Then
        Exception exception = assertThrows(Exception.class,
                () -> tipoAlmacenFrm.validarAntesDeCrear(tipoAlmacen));
        assertEquals("validacion.nombre.requerido", exception.getMessage());
    }

    @Test
    void testValidarAntesDeCrear_ConNombreVacio() {
        // Given
        tipoAlmacen.setNombre("");

        // When & Then
        Exception exception = assertThrows(Exception.class,
                () -> tipoAlmacenFrm.validarAntesDeCrear(tipoAlmacen));
        assertEquals("validacion.nombre.requerido", exception.getMessage());
    }

    @Test
    void testValidarAntesDeCrear_ConNombreSoloEspacios() {
        // Given
        tipoAlmacen.setNombre("   ");

        // When & Then
        Exception exception = assertThrows(Exception.class,
                () -> tipoAlmacenFrm.validarAntesDeCrear(tipoAlmacen));
        assertEquals("validacion.nombre.requerido", exception.getMessage());
    }

    @Test
    void testValidarAntesDeActualizar_ConNombreValido() {
        // Given
        tipoAlmacen.setNombre("Almacén Actualizado");

        // When & Then
        assertDoesNotThrow(() -> tipoAlmacenFrm.validarAntesDeActualizar(tipoAlmacen));
    }

    @Test
    void testValidarAntesDeActualizar_ConNombreNulo() {
        // Given
        tipoAlmacen.setNombre(null);

        // When & Then
        Exception exception = assertThrows(Exception.class,
                () -> tipoAlmacenFrm.validarAntesDeActualizar(tipoAlmacen));
        assertEquals("validacion.nombre.requerido", exception.getMessage());
    }

    @Test
    void testValidarAntesDeActualizar_ConNombreVacio() {
        // Given
        tipoAlmacen.setNombre("");

        // When & Then
        Exception exception = assertThrows(Exception.class,
                () -> tipoAlmacenFrm.validarAntesDeActualizar(tipoAlmacen));
        assertEquals("validacion.nombre.requerido", exception.getMessage());
    }

    @Test
    void testValidarAntesDeEliminar_ConNombreIgual() {
        // Given
        TipoAlmacen original = new TipoAlmacen();
        original.setNombre("Almacén Principal");
        tipoAlmacen.setNombre("Almacén Principal");

        // When & Then
        assertDoesNotThrow(() -> tipoAlmacenFrm.validarAntesDeEliminar(tipoAlmacen, original));
    }

    @Test
    void testValidarAntesDeEliminar_ConNombreDiferente() {
        // Given
        TipoAlmacen original = new TipoAlmacen();
        original.setNombre("Almacén Original");
        tipoAlmacen.setNombre("Almacén Modificado");

        // When & Then
        Exception exception = assertThrows(Exception.class,
                () -> tipoAlmacenFrm.validarAntesDeEliminar(tipoAlmacen, original));
        assertEquals("validacion.nombre.cambiado", exception.getMessage());
    }

    @Test
    void testInstanciarEntidad() {
        // When
        TipoAlmacen nuevo = tipoAlmacenFrm.instanciarEntidad();

        // Then
        assertNotNull(nuevo);
        assertTrue(nuevo.getActivo());
        assertNull(nuevo.getNombre());
    }

    @Test
    void testGetTipoAlmacenDAO() {
        // When
        TipoAlmacenDAO resultado = tipoAlmacenFrm.getTipoAlmacenDAO();

        // Then
        assertNotNull(resultado);
        assertEquals(tipoAlmacenDAO, resultado);
    }

    @Test
    void testSetTipoAlmacenDAO() {
        // Given
        TipoAlmacenDAO nuevoDAO = mock(TipoAlmacenDAO.class);

        // When
        tipoAlmacenFrm.setTipoAlmacenDAO(nuevoDAO);

        // Then
        assertEquals(nuevoDAO, tipoAlmacenFrm.getTipoAlmacenDAO());
    }
}


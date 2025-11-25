package core.boundory.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProducto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TipoProductoConverterTest {

    // Dependencia inyectada (TipoProductoDAO) que será mockeada
    @Mock
    private TipoProductoDAO mockDAO;

    // Mocks de entorno JSF (no necesarios para la lógica, pero requeridos por la firma)
    @Mock
    private FacesContext mockFacesContext;
    @Mock
    private UIComponent mockComponent;

    // Clase a probar, inyectando el mockDAO
    @InjectMocks
    private sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.converter.TipoProductoConverter converter;

    private final Long TEST_ID = 50L;
    private final String TEST_ID_STRING = "50";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------------------------------------------------------------
    //                              Pruebas getAsString
    // -------------------------------------------------------------------------

    @Test
    void testGetAsString_Success() {
        // Arrange
        TipoProducto entidad = new TipoProducto();
        entidad.setId(TEST_ID);

        // Act & Assert (Cubre la ruta de éxito)
        assertEquals(TEST_ID_STRING, converter.getAsString(mockFacesContext, mockComponent, entidad));
    }

    @Test
    void testGetAsString_NullObject_ReturnsEmptyString() {
        // Act & Assert (Cubre la condición 'if (value == null)')
        assertEquals("", converter.getAsString(mockFacesContext, mockComponent, null));
    }

    @Test
    void testGetAsString_ObjectWithNullId_ReturnsEmptyString() {
        // Arrange
        TipoProducto entidad = new TipoProducto();
        entidad.setId(null);

        // Act & Assert (Cubre la condición 'if (value.getId() == null)')
        assertEquals("", converter.getAsString(mockFacesContext, mockComponent, entidad));
    }

    // -------------------------------------------------------------------------
    //                              Pruebas getAsObject
    // -------------------------------------------------------------------------

    @Test
    void testGetAsObject_Success() {
        // Arrange
        TipoProducto entidadEsperada = new TipoProducto();
        entidadEsperada.setId(TEST_ID);
        // Simular que el DAO encuentra la entidad
        when(mockDAO.finById(eq(TEST_ID))).thenReturn(entidadEsperada);

        // Act (Cubre el bloque try{} exitoso)
        TipoProducto result = converter.getAsObject(mockFacesContext, mockComponent, TEST_ID_STRING);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        verify(mockDAO, times(1)).finById(TEST_ID);
    }

    @Test
    void testGetAsObject_EdgeCasesForNullOrEmptyValue() {
        // Cubre la condición 'if (value == null || value.trim().isEmpty())'

        // Assert para String nulo
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, null), "Debe devolver null para String nulo.");

        // Assert para String vacío
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, ""), "Debe devolver null para String vacío.");

        // Assert para String en blanco (cubre el .trim().isEmpty())
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, "  "), "Debe devolver null para String en blanco.");

        // Verificación: El DAO nunca debe ser llamado en estos casos
        verifyNoInteractions(mockDAO);
    }

    @Test
    void testGetAsObject_InvalidNumberFormat_ReturnsNull() {
        // Arrange (Cubre el catch NumberFormatException)
        String invalidString = "not_a_long";

        // Act & Assert
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, invalidString),
                "Debe devolver null si el String no es un Long válido.");

        // Verificación: El DAO nunca debe ser llamado
        verifyNoInteractions(mockDAO);
    }

    @Test
    void testGetAsObject_EntityNotFound_ReturnsNull() {
        // Arrange
        // Simular que el DAO no encuentra la entidad (retorna null)
        when(mockDAO.finById(eq(TEST_ID))).thenReturn(null);

        // Act
        TipoProducto result = converter.getAsObject(mockFacesContext, mockComponent, TEST_ID_STRING);

        // Assert
        assertNull(result, "Debe devolver null si el DAO no encuentra la entidad.");
        verify(mockDAO, times(1)).finById(TEST_ID);
    }

    @Test
    void testGetAsObject_DaoThrowsRuntimeException_PropagatesException() {
        // Arrange (Cubre el flujo de código donde se lanza una excepción NO capturada)
        // Simular un fallo de la base de datos (RuntimeException no está en el catch)
        when(mockDAO.finById(anyLong())).thenThrow(new RuntimeException("Simulated DB connection error"));

        // Act & Assert: Usamos assertThrows para validar que la RuntimeException se propague fuera del Converter
        assertThrows(RuntimeException.class, () -> {
            converter.getAsObject(mockFacesContext, mockComponent, TEST_ID_STRING);
        }, "La RuntimeException debe propagarse ya que el Converter solo captura NumberFormatException.");

        // Verificación: El DAO fue llamado
        verify(mockDAO, times(1)).finById(TEST_ID);
    }
}

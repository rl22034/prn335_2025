package core.boundory.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.converter.UUIDConverter;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UUIDConverterTest {

    private UUIDConverter converter;

    // Mocks de entorno JSF (no necesarios para la lógica, pero requeridos por la firma)
    @Mock
    private FacesContext mockFacesContext;
    @Mock
    private UIComponent mockComponent;

    private final UUID TEST_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426655440000");
    private final String TEST_UUID_STRING = "123e4567-e89b-12d3-a456-426655440000";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        converter = new UUIDConverter();
    }

    // -------------------------------------------------------------------------
    //                              Pruebas getAsString
    // -------------------------------------------------------------------------

    @Test
    void testGetAsString_Success() {
        // Act & Assert (Cubre la ruta de éxito: value.toString())
        assertEquals(TEST_UUID_STRING, converter.getAsString(mockFacesContext, mockComponent, TEST_UUID));
    }

    @Test
    void testGetAsString_NullValue_ReturnsEmptyString() {
        // Act & Assert (Cubre la condición 'if (value == null)')
        assertEquals("", converter.getAsString(mockFacesContext, mockComponent, null));
    }

    // -------------------------------------------------------------------------
    //                              Pruebas getAsObject
    // -------------------------------------------------------------------------

    @Test
    void testGetAsObject_Success() {
        // Act & Assert (Cubre el bloque try{} exitoso: UUID.fromString())
        UUID result = converter.getAsObject(mockFacesContext, mockComponent, TEST_UUID_STRING);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_UUID, result);
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
    }

    @Test
    void testGetAsObject_InvalidUUIDFormat_ReturnsNull() {
        // Arrange
        // Provoca un IllegalArgumentException, que es atrapado por el catch
        String invalidString = "not-a-valid-uuid-format";

        // Act & Assert (Cubre el catch (IllegalArgumentException e))
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, invalidString),
                "Debe devolver null si el String no es un UUID válido.");
    }
}
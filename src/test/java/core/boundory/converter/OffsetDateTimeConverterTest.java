package core.boundory.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.converter.OffsetDateTimeConverter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;

public class OffsetDateTimeConverterTest {

    // Zona horaria usada en el Converter (El Salvador)
    private static final ZoneId DEFAULT_ZONE = ZoneId.of("America/El_Salvador");

    @Mock
    private FacesContext mockFacesContext;

    @Mock
    private UIComponent mockComponent;

    @InjectMocks
    private OffsetDateTimeConverter converter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- Pruebas de getAsString (OffsetDateTime a String) ---

    @Test
    void testGetAsString_SuccessConversionToDefaultZone() {
        // Arrange
        // Hora UTC: 2025-10-14T19:45:00Z
        // Hora en El Salvador (-06:00): 2025-10-14T13:45:00-06:00
        OffsetDateTime utcValue = OffsetDateTime.of(2025, 10, 14, 19, 45, 0, 0, ZoneOffset.UTC);
        String expectedString = "2025-10-14T13:45"; // Formato esperado por el datePicker

        // Act (Cubre el bloque de retorno)
        String result = converter.getAsString(mockFacesContext, mockComponent, utcValue);

        // Assert
        assertEquals(expectedString, result, "Debe convertir a la hora de El Salvador y formatear sin offset/segundos.");
    }

    @Test
    void testGetAsString_NullValue() {
        // Act (Cubre la condición 'if (value == null)')
        String result = converter.getAsString(mockFacesContext, mockComponent, null);

        // Assert
        assertEquals("", result, "Debe retornar una cadena vacía para valor nulo.");
    }

    // --- Pruebas de getAsObject (String a OffsetDateTime) ---

    @Test
    void testGetAsObject_NullOrBlankString() {
        // Act & Assert (Cubre la condición 'if (value == null || value.isBlank())')
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, null), "Debe retornar null para entrada nula.");
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, ""), "Debe retornar null para cadena vacía.");
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, "   "), "Debe retornar null para cadena en blanco.");

        verifyNoInteractions(mockFacesContext);
    }

    @Test
    void testGetAsObject_WithOffset_Success() {
        // Arrange
        String input = "2025-10-14T13:45-06:00"; // Formato con offset

        // Act (Cubre el bloque 'if (v.matches(".*[+-]\\d{2}:\\d{2}$"))')
        OffsetDateTime result = converter.getAsObject(mockFacesContext, mockComponent, input);

        // Assert
        assertNotNull(result);
        assertEquals(13, result.getHour());
        assertEquals(ZoneOffset.ofHours(-6), result.getOffset(), "El offset debe ser -06:00.");
    }

    @Test
    void testGetAsObject_WithOffset_Success_DifferentOffset() {
        // Arrange
        String input = "2025-10-14T20:45+02:00"; // Formato con offset positivo

        // Act (Asegura que el Regex y el parseo manejan offsets positivos)
        OffsetDateTime result = converter.getAsObject(mockFacesContext, mockComponent, input);

        // Assert
        assertNotNull(result);
        assertEquals(20, result.getHour());
        assertEquals(ZoneOffset.ofHours(2), result.getOffset(), "El offset debe ser +02:00.");
    }

    @Test
    void testGetAsObject_WithOffset_Success_Normalization() {
        // Arrange
        String input = "2025-10-14T 13:45-06:00"; // Espacio después de 'T' (Cubre replaceFirst)

        // Act (Cubre la normalización 'replaceFirst("T\\s+", "T")')
        OffsetDateTime result = converter.getAsObject(mockFacesContext, mockComponent, input);

        // Assert
        assertNotNull(result);
        assertEquals(13, result.getHour());
    }


    @Test
    void testGetAsObject_NoOffset_Success_AssignDefaultZone() {
        // Arrange
        String input = "2025-10-14T13:45"; // Formato sin offset (lo que envía el datePicker)

        // Act (Cubre el bloque de asignación de zona)
        OffsetDateTime result = converter.getAsObject(mockFacesContext, mockComponent, input);

        // Assert
        assertNotNull(result);
        assertEquals(13, result.getHour());
        assertEquals(DEFAULT_ZONE.getRules().getOffset(result.toInstant()),
                result.getOffset(),
                "El offset debe ser el de la zona de El Salvador.");
    }

    @Test
    void testGetAsObject_InvalidFormat_ReturnsNull() {
        // Arrange
        String input = "14/10/2025 13:45"; // Formato incorrecto

        // Act (Cubre el bloque catch (Exception e))
        OffsetDateTime result = converter.getAsObject(mockFacesContext, mockComponent, input);

        // Assert
        assertNull(result, "Debe retornar null si la cadena tiene un formato inválido.");
    }
}

package core.boundory.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.converter.CaracteristicaConverter;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Caracteristica;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CaracteristicaConverterTest {

    // Mocks de las dependencias de JSF (aunque no se usan en la lógica, son necesarias para la firma del método)
    @Mock
    private FacesContext mockFacesContext;

    @Mock
    private UIComponent mockComponent;

    // Mock del DAO que es la dependencia inyectada (Línea 21)
    @Mock
    private CaracteristicaDAO mockDAO;

    // La clase a probar, inyectando los mocks
    @InjectMocks
    private CaracteristicaConverter converter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- 1. Tests de getAsString (Objeto a String) ---

    @Test
    void testGetAsString_Success() {
        // Arrange
        Integer testId = 42;
        Caracteristica c = new Caracteristica();
        c.setId(testId);

        // Act
        String result = converter.getAsString(mockFacesContext, mockComponent, c);

        // Assert (Cubre el bloque de retorno del ID como String)
        assertEquals("42", result, "Debe devolver el ID del objeto como String.");
    }

    @Test
    void testGetAsString_NullObject() {
        // Act
        String result = converter.getAsString(mockFacesContext, mockComponent, null);

        // Assert (Cubre la condición 'if (value == null)')
        assertEquals("", result, "Debe devolver cadena vacía si el objeto es nulo.");
    }

    @Test
    void testGetAsString_ObjectWithNullId() {
        // Arrange
        Caracteristica c = new Caracteristica();
        c.setId(null); // Objeto existe, pero ID es nulo

        // Act
        String result = converter.getAsString(mockFacesContext, mockComponent, c);

        // Assert (Cubre la condición 'if (value.getId() == null)')
        assertEquals("", result, "Debe devolver cadena vacía si el ID del objeto es nulo.");
    }

    // --- 2. Tests de getAsObject (String a Objeto) ---

    @Test
    void testGetAsObject_Success() throws Exception {
        // Arrange
        Integer id = 100;
        String idString = "100";
        Caracteristica cEsperada = new Caracteristica();
        cEsperada.setId(id);

        // Simular que el DAO encuentra la entidad (Cubre el bloque try{} exitoso)
        when(mockDAO.finById(eq(id))).thenReturn(cEsperada);

        // Act
        Caracteristica result = converter.getAsObject(mockFacesContext, mockComponent, idString);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        // Verificar que el DAO fue llamado con el ID correcto
        verify(mockDAO, times(1)).finById(id);
    }

    @Test
    void testGetAsObject_NullOrEmptyString() {
        // Act & Assert (Cubre la condición 'if (value == null || value.trim().isEmpty())')
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, null), "Debe devolver null para String nulo.");
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, ""), "Debe devolver null para String vacío.");
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, "  "), "Debe devolver null para String en blanco.");

        // Verificación: El DAO nunca debe ser llamado en estos casos
        verifyNoInteractions(mockDAO);
    }

    @Test
    void testGetAsObject_InvalidNumberFormat() {
        // Arrange
        String invalidString = "abc"; // No es un número

        // Act & Assert (Cubre el catch NumberFormatException y retorna null)
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, invalidString),
                "Debe devolver null si el formato del String es inválido.");

        // Verificación: El DAO nunca debe ser llamado
        verifyNoInteractions(mockDAO);
    }
}
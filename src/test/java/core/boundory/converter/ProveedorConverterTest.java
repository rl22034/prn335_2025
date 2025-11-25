package core.boundory.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.converter.ProveedorConverter;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProveedorDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Proveedor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProveedorConverterTest {

    // Dependencia inyectada que debe ser mockeada
    @Mock
    private ProveedorDAO mockDAO;

    // Mocks del entorno de JSF (necesarios para la firma del método)
    @Mock
    private FacesContext mockFacesContext;

    @Mock
    private UIComponent mockComponent;

    // Clase a probar, inyectando el mockDAO
    @InjectMocks
    private ProveedorConverter converter;

    // ID de prueba
    private final Integer TEST_ID = 50;
    private final String TEST_ID_STRING = "50";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- 1. Tests de getAsString (Objeto a String) ---

    @Test
    void testGetAsString_Success() {
        // Arrange
        Proveedor proveedor = new Proveedor();
        proveedor.setId(TEST_ID);

        // Act
        String result = converter.getAsString(mockFacesContext, mockComponent, proveedor);

        // Assert (Cubre el retorno del ID como String)
        assertEquals(TEST_ID_STRING, result, "Debe devolver el ID del objeto como String.");
    }

    @Test
    void testGetAsString_NullObject() {
        // Act
        String result = converter.getAsString(mockFacesContext, mockComponent, null);

        // Assert (Cubre la condición 'if (proveedor == null)')
        assertEquals("", result, "Debe devolver cadena vacía si el objeto es nulo.");
    }

    @Test
    void testGetAsString_ObjectWithNullId() {
        // Arrange
        Proveedor proveedor = new Proveedor();
        proveedor.setId(null);

        // Act
        String result = converter.getAsString(mockFacesContext, mockComponent, proveedor);

        // Assert (Cubre la condición 'if (proveedor.getId() == null)')
        assertEquals("", result, "Debe devolver cadena vacía si el ID del objeto es nulo.");
    }

    // --- 2. Tests de getAsObject (String a Objeto) ---

    @Test
    void testGetAsObject_Success() throws Exception {
        // Arrange
        Proveedor proveedorEsperado = new Proveedor();
        proveedorEsperado.setId(TEST_ID);

        // Simular que el DAO encuentra la entidad
        when(mockDAO.finById(eq(TEST_ID))).thenReturn(proveedorEsperado);

        // Act (Cubre el bloque try{} exitoso)
        Proveedor result = converter.getAsObject(mockFacesContext, mockComponent, TEST_ID_STRING);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        // Verificar que el DAO fue llamado con el ID correcto
        verify(mockDAO, times(1)).finById(TEST_ID);
    }

    @Test
    void testGetAsObject_NullOrEmptyOrLiteralNullString() {
        // Act & Assert (Cubre la condición 'if (value == null || value.trim().isEmpty() || value.equals("null"))')
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, null), "Debe devolver null para String nulo.");
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, ""), "Debe devolver null para String vacío.");
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, "  "), "Debe devolver null para String en blanco.");
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, "null"), "Debe devolver null para la cadena 'null'.");

        // Verificación: El DAO nunca debe ser llamado en estos casos
        verifyNoInteractions(mockDAO);
    }

    @Test
    void testGetAsObject_InvalidNumberFormat() {
        // Arrange
        String invalidString = "abc"; // Causa NumberFormatException

        // Act & Assert (Cubre el bloque catch NumberFormatException)
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, invalidString),
                "Debe devolver null si el String no es un entero válido.");

        // Verificación: El DAO nunca debe ser llamado
        verifyNoInteractions(mockDAO);
    }
}

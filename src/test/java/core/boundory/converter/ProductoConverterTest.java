package core.boundory.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.ProductoConverter;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Producto;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductoConverterTest {

    // Dependencia inyectada (DAO)
    @Mock
    private ProductoDAO mockDAO;

    // Mocks de JSF
    @Mock
    private FacesContext mockFacesContext;

    @Mock
    private UIComponent mockComponent;

    // Clase a probar, inyectando el mockDAO
    @InjectMocks
    private ProductoConverter converter;

    // UUID de prueba
    private final UUID TEST_UUID = UUID.fromString("1a2b3c4d-5e6f-7080-9102-131415161718");
    private final String TEST_UUID_STRING = "1a2b3c4d-5e6f-7080-9102-131415161718";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- 1. Tests de getAsString (Objeto a String) ---

    @Test
    void testGetAsString_Success() {
        // Arrange
        Producto producto = new Producto();
        producto.setId(TEST_UUID);

        // Act
        String result = converter.getAsString(mockFacesContext, mockComponent, producto);

        // Assert (Cubre el retorno del ID como String)
        assertEquals(TEST_UUID_STRING, result, "Debe devolver el UUID del objeto como String.");
    }

    @Test
    void testGetAsString_NullObject() {
        // Act
        String result = converter.getAsString(mockFacesContext, mockComponent, null);

        // Assert (Cubre la condición 'if (value == null)')
        assertNull(result, "Debe devolver null si el objeto es nulo.");
    }

    @Test
    void testGetAsString_ObjectWithNullId() {
        // Arrange
        Producto producto = new Producto();
        producto.setId(null);

        // Act
        String result = converter.getAsString(mockFacesContext, mockComponent, producto);

        // Assert (Cubre la condición 'if (value.getId() == null)')
        assertNull(result, "Debe devolver null si el ID del objeto es nulo.");
    }

    // --- 2. Tests de getAsObject (String a Objeto) ---

    @Test
    void testGetAsObject_Success() throws Exception {
        // Arrange
        Producto productoEsperado = new Producto();
        productoEsperado.setId(TEST_UUID);

        // Simular que el DAO encuentra la entidad
        when(mockDAO.finById(eq(TEST_UUID))).thenReturn(productoEsperado);

        // Act (Cubre el bloque try{} exitoso)
        Producto result = converter.getAsObject(mockFacesContext, mockComponent, TEST_UUID_STRING);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_UUID, result.getId());
        // Verificar que el DAO fue llamado con el UUID correcto
        verify(mockDAO, times(1)).finById(TEST_UUID);
    }

    @Test
    void testGetAsObject_NullOrEmptyString() {
        // Act & Assert (Cubre la condición 'if (value == null || value.trim().isEmpty())')
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, null), "Debe devolver null para String nulo.");
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, ""), "Debe devolver null para String vacío.");
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, "  "), "Debe devolver null para String en blanco.");

        // Verificación: El DAO nunca debe ser llamado
        verifyNoInteractions(mockDAO);
    }

    @Test
    void testGetAsObject_InvalidUUIDFormat() {
        // Arrange
        String invalidString = "not-a-valid-uuid-format"; // Causa IllegalArgumentException/Exception

        // Act & Assert (Cubre el bloque catch (Exception e))
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, invalidString),
                "Debe devolver null si el String no es un UUID válido.");

        // Verificación: El DAO nunca debe ser llamado
        verifyNoInteractions(mockDAO);
    }

    @Test
    void testGetAsObject_DaoLookupException() throws Exception {
        // Arrange
        // Simular que el DAO lanza una excepción (cubre el catch Exception e)
        when(mockDAO.finById(any(UUID.class))).thenThrow(new RuntimeException("Simulated DB error"));

        // Act
        Producto result = converter.getAsObject(mockFacesContext, mockComponent, TEST_UUID_STRING);

        // Assert
        assertNull(result, "Debe devolver null si hay una excepción durante la búsqueda.");

        // Verificación: Se intentó buscar con el DAO
        verify(mockDAO, times(1)).finById(TEST_UUID);
    }
}
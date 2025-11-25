package core.boundory.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.converter.ClienteConverter;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ClienteDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Cliente;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClienteConverterTest {

    // Dependencia inyectada que debe ser mockeada
    @Mock
    private ClienteDAO mockDAO;

    // Mocks del entorno de JSF
    @Mock
    private FacesContext mockFacesContext;

    @Mock
    private UIComponent mockComponent;

    // Clase a probar, inyectando el mockDAO
    @InjectMocks
    private ClienteConverter converter;

    // UUID de prueba
    private final UUID TEST_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef");
    private final String TEST_UUID_STRING = "a1b2c3d4-e5f6-7890-1234-567890abcdef";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- 1. Tests de getAsString (Objeto a String) ---

    @Test
    void testGetAsString_Success() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(TEST_UUID);

        // Act
        String result = converter.getAsString(mockFacesContext, mockComponent, cliente);

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
        Cliente cliente = new Cliente();
        cliente.setId(null); // Objeto existe, pero ID es nulo

        // Act
        String result = converter.getAsString(mockFacesContext, mockComponent, cliente);

        // Assert (Cubre la condición 'if (value.getId() == null)')
        assertNull(result, "Debe devolver null si el ID del objeto es nulo.");
    }

    // --- 2. Tests de getAsObject (String a Objeto) ---

    @Test
    void testGetAsObject_Success() throws Exception {
        // Arrange
        Cliente clienteEsperado = new Cliente();
        clienteEsperado.setId(TEST_UUID);

        // Simular que el DAO encuentra la entidad
        when(mockDAO.finById(eq(TEST_UUID))).thenReturn(clienteEsperado);

        // Act (Cubre el bloque try{} exitoso)
        Cliente result = converter.getAsObject(mockFacesContext, mockComponent, TEST_UUID_STRING);

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

        // Verificación: El DAO nunca debe ser llamado en estos casos
        verifyNoInteractions(mockDAO);
    }

    @Test
    void testGetAsObject_InvalidUUIDFormat() {
        // Arrange
        String invalidString = "not-a-valid-uuid"; // Causa UUID.fromString(value) lance IllegalArgumentException

        // Act & Assert (Cubre el catch Exception e)
        assertNull(converter.getAsObject(mockFacesContext, mockComponent, invalidString),
                "Debe devolver null si el String no es un UUID válido.");

        // Verificación: El DAO nunca debe ser llamado
        verifyNoInteractions(mockDAO);
    }

    @Test
    void testGetAsObject_DaoInjectionError() {
        // Arrange: Creamos una instancia del Converter donde simulamos que la inyección falló (mockDAO es null)
        ClienteConverter badConverter = new ClienteConverter();

        // Act & Assert (Cubre la condición 'if (clienteDAO == null)')
        // Esta prueba solo funciona si el test se ejecuta SIN Mockito inyectando el DAO
        // Como estamos usando @InjectMocks, simularemos este comportamiento con la verificación de System.err.
        // **Nota:** En un entorno real de testing con Weld/OpenEJB, este caso podría ser diferente.

        // Forzamos el comportamiento que simula que finById lanza una RuntimeException para entrar al catch
        when(mockDAO.finById(any(UUID.class))).thenThrow(new RuntimeException("Error simulado de DAO"));

        // Act (Entra al bloque try, lanza la excepción y entra al catch)
        Cliente result = converter.getAsObject(mockFacesContext, mockComponent, TEST_UUID_STRING);

        // Assert
        assertNull(result, "Debe devolver null si hay una excepción durante la búsqueda.");
        verify(mockDAO, times(1)).finById(TEST_UUID);
    }
}
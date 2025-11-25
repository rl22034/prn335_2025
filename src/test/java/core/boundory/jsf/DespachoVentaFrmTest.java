package core.boundory.jsf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.DespachoVentaFrm;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.EstadoVenta;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.VentaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Cliente;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Venta;
// Importamos LazyDataModel sin raw type en el código de prueba
import org.primefaces.model.LazyDataModel;

import java.util.UUID;
import java.util.List;
import jakarta.faces.model.SelectItem;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class DespachoVentaFrmTest {

    @Mock
    private VentaDAO mockVentaDAO;

    // 💡 CORRECCIÓN DE LA ClassCastException (Línea 33, antes línea 40 aprox.):
    // Especificar el tipo genérico (<Venta>) de LazyDataModel
    @Mock
    private LazyDataModel<Venta> mockLazyModel;

    @InjectMocks
    private DespachoVentaFrm despachoVentaFrm;

    private Venta mockVenta;

    private final UUID TEST_UUID_VENTA = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID TEST_UUID_CLIENTE = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @BeforeEach
    void setUp() {
        // La ClassCastException ocurría aquí, en el proceso de inyección de Mockito
        MockitoAnnotations.openMocks(this); // Línea 45 (aprox.)

        // ... (Resto del setup) ...
        despachoVentaFrm.setLazyModel(mockLazyModel);

        mockVenta = new Venta();
        mockVenta.setId(TEST_UUID_VENTA);

        Cliente mockCliente = new Cliente();
        mockCliente.setId(TEST_UUID_CLIENTE);

        mockVenta.setIdCliente(mockCliente);
        mockVenta.setEstado("DESPACHADA");
    }

    // -------------------------------------------------------------------------
    // Pruebas de Inicialización y Carga de Datos (Métodos Públicos)
    // -------------------------------------------------------------------------

    @Test
    void testInit_Success() {
        despachoVentaFrm.init();
        verify(mockLazyModel, times(1)).load(eq(0), eq(10), any(), any());
    }

    @Test
    void testInit_ExceptionOnLoad() {
        // Usamos doThrow para simular un error en la carga del LazyModel
        doThrow(new RuntimeException("DB Error")).when(mockLazyModel).load(eq(0), eq(10), any(), any());
        despachoVentaFrm.init();
        // Verificamos que al menos se intentó cargar, aunque fallara
        verify(mockLazyModel, times(1)).load(eq(0), eq(10), any(), any());
    }

    @Test
    void testCargarRegistros_NoLazyModel() {
        despachoVentaFrm.setLazyModel(null);
        despachoVentaFrm.cargarRegistros();
        // Aseguramos que no hubo interacción si el modelo es nulo
        verifyNoInteractions(mockLazyModel);
    }

    // -------------------------------------------------------------------------
    // Pruebas de Métodos Auxiliares (Métodos Públicos)
    // -------------------------------------------------------------------------

    @Test
    void testGetEstadosDisponibles() {
        // Act
        List<SelectItem> estados = despachoVentaFrm.getEstadosDisponibles();

        // Assert
        assertNotNull(estados);
        assertEquals(EstadoVenta.values().length, estados.size());

        SelectItem pendiente = estados.stream()
                .filter(item -> item.getValue().equals(EstadoVenta.PENDIENTE.name()))
                .findFirst()
                .orElse(null);

        assertNotNull(pendiente);
        assertEquals(EstadoVenta.PENDIENTE.name(), pendiente.getValue());
    }

}
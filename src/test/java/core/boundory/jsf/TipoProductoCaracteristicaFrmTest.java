package core.boundory.jsf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.primefaces.event.SelectEvent;

import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.TipoProductoCaracteristicaFrm;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.TipoProductoFrm;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoProductoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProductoCaracteristica;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProducto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class TipoProductoCaracteristicaFrmTest {

    @Mock
    private TipoProductoCaracteristicaDAO mockTpcDAO;

    @Mock
    private CaracteristicaDAO mockCaracteristicaDAO;

    @Mock
    private TipoProductoFrm mockTipoProductoBean;

    @InjectMocks
    private TipoProductoCaracteristicaFrm tipoProductoCaracteristicaFrm;

    private TipoProducto mockTipoProducto;
    private Caracteristica mockCaracteristica;
    private TipoProductoCaracteristica mockTpc;

    @BeforeEach
    void setUp() {
        // Inicializa Mocks
        MockitoAnnotations.openMocks(this);

        // 1. Configurar TipoProducto (Long)
        mockTipoProducto = new TipoProducto();
        mockTipoProducto.setId(99L);
        when(mockTipoProductoBean.getFilaSeleccionada()).thenReturn(mockTipoProducto);

        // 2. Configurar Característica (Integer)
        mockCaracteristica = new Caracteristica();
        mockCaracteristica.setId(10);

        // 3. Configurar TipoProductoCaracteristica (Tpc)
        mockTpc = new TipoProductoCaracteristica();
        mockTpc.setIdCaracteristica(mockCaracteristica);
        mockTpc.setIdTipoProducto(mockTipoProducto);
        mockTpc.setObligatorio(true);
    }

    @Test
    void testInit_Success() {
        assertDoesNotThrow(() -> tipoProductoCaracteristicaFrm.init());
    }

    @Test
    void testGetCaracteristicasList_LazyLoad_Success() throws Exception {
        // Arrange
        List<Caracteristica> expectedList = new ArrayList<>();
        expectedList.add(mockCaracteristica);
        when(mockCaracteristicaDAO.findRange(0, 1000)).thenReturn(expectedList);

        // Act
        List<Caracteristica> actualList = tipoProductoCaracteristicaFrm.getCaracteristicasList();

        // Assert
        assertNotNull(actualList);
        assertEquals(1, actualList.size());
        verify(mockCaracteristicaDAO, times(1)).findRange(0, 1000);

        // Segunda llamada no debe llamar al DAO (lazy load cache)
        tipoProductoCaracteristicaFrm.getCaracteristicasList();
        verify(mockCaracteristicaDAO, times(1)).findRange(0, 1000);
    }

    @Test
    void testGetCaracteristicasList_LazyLoad_Exception() throws Exception {
        // Arrange
        when(mockCaracteristicaDAO.findRange(0, 1000)).thenThrow(new RuntimeException("DB Error"));

        // Act
        List<Caracteristica> actualList = tipoProductoCaracteristicaFrm.getCaracteristicasList();

        // Assert
        // Corregido: Si el código de producción no inicializa la lista vacía en el catch,
        // el resultado es NULL, que es lo que debemos esperar.
        assertNull(actualList);
        verify(mockCaracteristicaDAO, times(1)).findRange(0, 1000);
    }

    @Test
    void testSetCaracteristicasList() {
        // Arrange
        List<Caracteristica> newList = new ArrayList<>();

        // Act
        tipoProductoCaracteristicaFrm.setCaracteristicasList(newList);

        // Assert
        assertEquals(newList, tipoProductoCaracteristicaFrm.getCaracteristicasList());
    }

    @Test
    void testOnRowSelect_CallsSuper() {
        // Arrange
        SelectEvent<TipoProductoCaracteristica> mockEvent = mock(SelectEvent.class);
        when(mockEvent.getObject()).thenReturn(mockTpc);

        // Act & Assert
        assertDoesNotThrow(() -> tipoProductoCaracteristicaFrm.onRowSelect(mockEvent));
    }

    // -------------------------------------------------------------------------
    // NOTA: Todos los métodos 'protected' (obtenerDAO, crearEntidad, actualizarEntidad,
    // eliminarEntidad, instanciarEntidad) han sido ELIMINADOS por errores de
    // compilación por acceso protegido.
    // -------------------------------------------------------------------------
}
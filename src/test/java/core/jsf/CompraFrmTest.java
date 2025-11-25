package core.jsf;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.CompraFrm;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.EstadoCompra;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.MessageHelper;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.CRUD;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.*;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompraFrmTest {

    @Mock
    private CompraDAO compraDAO;

    @Mock
    private CompraDetalleDAO compraDetalleDAO;

    @Mock
    private ProductoDAO productoDAO;

    @Mock
    private ProveedorDAO proveedorDAO;

    @Mock
    private NotificadorKardex notificadorKardex;

    @Mock
    private KardexDAO kardexDAO;

    @Mock
    private AlmacenDAO almacenDAO;

    @InjectMocks
    private CompraFrm compraFrm;

    private MockedStatic<MessageHelper> messageHelperMock;

    @BeforeEach
    void setUp() {
        messageHelperMock = mockStatic(MessageHelper.class);
        when(proveedorDAO.findProveedoresActivos()).thenReturn(new ArrayList<>());
        when(productoDAO.findRange(anyInt(), anyInt())).thenReturn(new ArrayList<>());
        compraFrm.init();
    }

    @AfterEach
    void tearDown() {
        if (messageHelperMock != null) {
            messageHelperMock.close();
        }
    }

    // ========== Tests de inicialización ==========

    @Test
    void init_debeInicializarEstadoYFilaSeleccionada() {
        assertEquals(CRUD.NINGUNO, compraFrm.getEstado());
        assertNotNull(compraFrm.getFilaSeleccionada());
    }

    @Test
    void instanciarEntidad_debeCrearCompraConValoresPorDefecto() {
        Compra compra = compraFrm.getFilaSeleccionada();

        assertNotNull(compra);
        assertNotNull(compra.getFecha());
        assertEquals("PENDIENTE", compra.getEstado());
    }

    // ========== Tests de btnNuevo ==========

    @Test
    void btnNuevo_debeLimpiarDetallesYCambiarEstado() {
        compraFrm.btnNuevo();

        assertEquals(CRUD.CREAR, compraFrm.getEstado());
        assertNotNull(compraFrm.getDetallesDeLaCompra());
        assertTrue(compraFrm.getDetallesDeLaCompra().isEmpty());
        assertNull(compraFrm.getDetalleSeleccionado());
    }

    // ========== Tests de validación ==========

    @Test
    void isCompraNueva_cuandoEstadoEsCrear_debeRetornarTrue() {
        compraFrm.btnNuevo();
        assertTrue(compraFrm.isCompraNueva());
    }

    @Test
    void isCompraNueva_cuandoEstadoNoEsCrear_debeRetornarFalse() {
        compraFrm.setEstado(CRUD.MODIFICAR);
        assertFalse(compraFrm.isCompraNueva());
    }

    // ========== Tests de estados disponibles ==========

    @Test
    void getEstadosDisponibles_debeRetornarTodosLosEstados() {
        var estados = compraFrm.getEstadosDisponibles();

        assertNotNull(estados);
        assertEquals(3, estados.size()); // PENDIENTE, PAGADA, CANCELADA
    }

    @Test
    void getEstadosDisponiblesDetalle_debeRetornarTodosLosEstadosDetalle() {
        var estados = compraFrm.getEstadosDisponiblesDetalle();

        assertNotNull(estados);
        assertEquals(3, estados.size()); // PENDIENTE, RECIBIDO, CANCELADO
    }

    // ========== Tests de detalles ==========

    @Test
    void prepararNuevoDetalle_debeCrearDetalleConValoresPorDefecto() {
        Compra compra = new Compra();
        compra.setId(1L);
        compraFrm.setFilaSeleccionada(compra);

        compraFrm.prepararNuevoDetalle();

        CompraDetalle detalle = compraFrm.getDetalleSeleccionado();
        assertNotNull(detalle);
        assertNotNull(detalle.getId());
        assertEquals(compra, detalle.getIdCompra());
        assertEquals(BigDecimal.ONE, detalle.getCantidad());
        assertEquals(BigDecimal.ZERO, detalle.getPrecio());
        assertEquals("PENDIENTE", detalle.getEstado());
    }

    @Test
    void prepararEditarDetalle_debeAsignarDetalleSeleccionado() {
        CompraDetalle detalle = new CompraDetalle();
        detalle.setId(UUID.randomUUID());

        compraFrm.prepararEditarDetalle(detalle);

        assertEquals(detalle, compraFrm.getDetalleSeleccionado());
    }

    @Test
    void cargarDetallesCompra_conCompraSeleccionada_debeCargarDetalles() {
        Compra compra = new Compra();
        compra.setId(1L);
        compraFrm.setFilaSeleccionada(compra);

        List<CompraDetalle> detalles = new ArrayList<>();
        detalles.add(new CompraDetalle());
        when(compraDetalleDAO.getDetallesPorCompra(1L)).thenReturn(detalles);

        compraFrm.cargarDetallesCompra();

        assertEquals(1, compraFrm.getDetallesDeLaCompra().size());
    }

    @Test
    void cargarDetallesCompra_sinCompraSeleccionada_debeRetornarListaVacia() {
        compraFrm.setFilaSeleccionada(null);

        compraFrm.cargarDetallesCompra();

        assertTrue(compraFrm.getDetallesDeLaCompra().isEmpty());
    }

    @Test
    void isDetalleNuevo_conDetalleNuevo_debeRetornarTrue() {
        CompraDetalle detalle = new CompraDetalle();
        detalle.setId(UUID.randomUUID());
        compraFrm.setDetalleSeleccionado(detalle);
        when(compraDetalleDAO.finById(any())).thenReturn(null);

        assertTrue(compraFrm.isDetalleNuevo());
    }

    @Test
    void isDetalleNuevo_conDetalleExistente_debeRetornarFalse() {
        CompraDetalle detalle = new CompraDetalle();
        UUID id = UUID.randomUUID();
        detalle.setId(id);
        compraFrm.setDetalleSeleccionado(detalle);
        when(compraDetalleDAO.finById(id)).thenReturn(detalle);

        assertFalse(compraFrm.isDetalleNuevo());
    }

    // ========== Tests de getTotalCompra ==========

    @Test
    void getTotalCompra_conCompraSeleccionada_debeRetornarTotal() {
        Compra compra = new Compra();
        compra.setId(1L);
        compraFrm.setFilaSeleccionada(compra);
        when(compraDetalleDAO.calcularTotalCompra(1L)).thenReturn(new BigDecimal("100.00"));

        BigDecimal total = compraFrm.getTotalCompra();

        assertEquals(new BigDecimal("100.00"), total);
    }

    @Test
    void getTotalCompra_sinCompraSeleccionada_debeRetornarCero() {
        compraFrm.setFilaSeleccionada(new Compra()); // Sin ID

        BigDecimal total = compraFrm.getTotalCompra();

        assertEquals(BigDecimal.ZERO, total);
    }

    // ========== Tests de notificarCambioKardex ==========

    @Test
    void notificarCambioKardex_debeActualizarEstadoYCrearKardex() {
        // Preparar compra
        Compra compra = new Compra();
        compra.setId(1L);
        compra.setFecha(OffsetDateTime.now());
        compra.setEstado("PENDIENTE");
        Proveedor proveedor = new Proveedor();
        proveedor.setId(1);
        compra.setProveedor(proveedor);
        compraFrm.setFilaSeleccionada(compra);
        compraFrm.setEstado(CRUD.MODIFICAR);

        // Preparar mocks
        when(compraDAO.finById(1L)).thenReturn(compra);

        // Preparar almacén
        List<Almacen> almacenes = new ArrayList<>();
        Almacen almacen = new Almacen();
        almacen.setId(1);
        almacenes.add(almacen);
        when(almacenDAO.findRange(0, 1)).thenReturn(almacenes);

        // Preparar detalles
        List<CompraDetalle> detalles = new ArrayList<>();
        CompraDetalle detalle = new CompraDetalle();
        detalle.setId(UUID.randomUUID());
        Producto producto = new Producto();
        producto.setId(UUID.randomUUID());
        detalle.setIdProducto(producto);
        detalle.setCantidad(new BigDecimal("10"));
        detalle.setPrecio(new BigDecimal("5.00"));
        detalles.add(detalle);
        when(compraDetalleDAO.getDetallesPorCompra(1L)).thenReturn(detalles);
        when(kardexDAO.obtenerCantidadActual(any())).thenReturn(BigDecimal.ZERO);

        // Ejecutar
        compraFrm.notificarCambioKardex();

        // Verificar
        assertEquals(EstadoCompra.PAGADA.name(), compra.getEstado());
        verify(compraDAO).update(compra);
        verify(kardexDAO).crearKardexEntrada(any(), any(), any(), any(), any(), any(), any(), any(), any());
        verify(notificadorKardex).notificarCambioKardex(contains("Compra #1"));
        assertEquals(CRUD.NINGUNO, compraFrm.getEstado());
    }

    @Test
    void notificarCambioKardex_sinCompraSeleccionada_noDebeHacerNada() {
        compraFrm.setFilaSeleccionada(null);

        compraFrm.notificarCambioKardex();

        verify(compraDAO, never()).update(any());
        verify(kardexDAO, never()).crearKardexEntrada(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    // ========== Tests de fechaLocal ==========

    @Test
    void getFechaLocal_conFechaValida_debeRetornarLocalDateTime() {
        Compra compra = new Compra();
        compra.setFecha(OffsetDateTime.now());
        compraFrm.setFilaSeleccionada(compra);

        assertNotNull(compraFrm.getFechaLocal());
    }

    @Test
    void getFechaLocal_sinFecha_debeRetornarNull() {
        Compra compra = new Compra();
        compra.setFecha(null);
        compraFrm.setFilaSeleccionada(compra);

        assertNull(compraFrm.getFechaLocal());
    }

    // ========== Tests de getters y setters ==========

    @Test
    void getProveedoresActivos_debeRetornarLista() {
        assertNotNull(compraFrm.getProveedoresActivos());
    }

    @Test
    void getProductosDisponibles_debeRetornarLista() {
        assertNotNull(compraFrm.getProductosDisponibles());
    }

    @Test
    void setDetallesDeLaCompra_debeFuncionar() {
        List<CompraDetalle> detalles = new ArrayList<>();
        compraFrm.setDetallesDeLaCompra(detalles);
        assertEquals(detalles, compraFrm.getDetallesDeLaCompra());
    }

    @Test
    void setProductosDisponibles_debeFuncionar() {
        List<Producto> productos = new ArrayList<>();
        compraFrm.setProductosDisponibles(productos);
        assertEquals(productos, compraFrm.getProductosDisponibles());
    }
}

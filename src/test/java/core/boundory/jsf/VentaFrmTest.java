package core.boundory.jsf;

import jakarta.faces.model.SelectItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.primefaces.PrimeFaces;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.*;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ClienteDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.VentaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.VentaDetalleDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Cliente;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Producto;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Venta;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.VentaDetalle;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VentaFrmTest {

    @Mock
    private VentaDAO ventaDAO;
    @Mock
    private ClienteDAO clienteDAO;
    @Mock
    private VentaDetalleDAO ventaDetalleDAO;
    @Mock
    private ProductoDAO productoDAO;
    @Mock
    private EntityManager entityManager;
    @Mock
    private TypedQuery<Venta> typedQuery;
    @Mock
    private PrimeFaces primeFaces;

    private VentaFrm frm;
    private MockedStatic<MessageHelper> messageHelperMock;
    private MockedStatic<PrimeFaces> primeFacesMock;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        messageHelperMock = mockStatic(MessageHelper.class);
        primeFacesMock = mockStatic(PrimeFaces.class);
        primeFacesMock.when(PrimeFaces::current).thenReturn(primeFaces);

        frm = new VentaFrm();
        inyectarCampo("ventaDAO", ventaDAO);
        inyectarCampo("clienteDAO", clienteDAO);
        inyectarCampo("ventaDetalleDAO", ventaDetalleDAO);
        inyectarCampo("productoDAO", productoDAO);

        when(productoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
    }

    @AfterEach
    void tearDown() {
        if (messageHelperMock != null) messageHelperMock.close();
        if (primeFacesMock != null) primeFacesMock.close();
    }

    private void inyectarCampo(String nombre, Object valor) throws Exception {
        Field field = VentaFrm.class.getDeclaredField(nombre);
        field.setAccessible(true);
        field.set(frm, valor);
    }

    private Venta crearVenta(UUID id) {
        Venta v = new Venta();
        v.setId(id);
        v.setFecha(OffsetDateTime.now());
        v.setEstado("PENDIENTE");
        v.setIdCliente(crearCliente(UUID.randomUUID()));
        return v;
    }

    private Cliente crearCliente(UUID id) {
        Cliente c = new Cliente();
        c.setId(id);
        c.setNombre("Cliente Test");
        c.setActivo(true);
        return c;
    }

    private VentaDetalle crearVentaDetalle(UUID id) {
        VentaDetalle vd = new VentaDetalle();
        vd.setId(id);
        vd.setCantidad(BigDecimal.ONE);
        vd.setPrecio(BigDecimal.TEN);
        vd.setEstado("PENDIENTE");
        return vd;
    }

    // ========== Tests para init ==========
    @Test
    void testInit() {
        when(productoDAO.findRange(0, 1000)).thenReturn(Arrays.asList(new Producto()));
        frm.init();
        assertEquals(CRUD.NINGUNO, frm.getEstado());
        assertNotNull(frm.getProductosDisponibles());
    }

    @Test
    void testInitConExceptionEnProductos() throws Exception {
        inyectarCampo("productoDAO", null);
        assertDoesNotThrow(() -> frm.init());
    }

    // ========== Tests para obtenerDAO ==========
    @Test
    void testObtenerDAO() {
        assertEquals(ventaDAO, frm.getVentaDAO());
    }

    // ========== Tests para validarAntesDeCrear ==========
    @Test
    void testValidarAntesDeCrearExitoso() {
        Venta entidad = crearVenta(null);
        frm.setFilaSeleccionada(entidad);
        frm.btnAgregar();
        verify(ventaDAO).crear(any());
    }

    @Test
    void testValidarAntesDeCrearFechaNull() {
        Venta entidad = new Venta();
        entidad.setFecha(null);
        entidad.setIdCliente(crearCliente(UUID.randomUUID()));
        entidad.setEstado("PENDIENTE");
        frm.setFilaSeleccionada(entidad);
        frm.btnAgregar();
        verify(ventaDAO, never()).crear(any());
    }

    @Test
    void testValidarAntesDeCrearClienteNull() {
        Venta entidad = new Venta();
        entidad.setFecha(OffsetDateTime.now());
        entidad.setIdCliente(null);
        entidad.setEstado("PENDIENTE");
        frm.setFilaSeleccionada(entidad);
        frm.btnAgregar();
        verify(ventaDAO, never()).crear(any());
    }

    @Test
    void testValidarAntesDeCrearEstadoNull() {
        Venta entidad = new Venta();
        entidad.setFecha(OffsetDateTime.now());
        entidad.setIdCliente(crearCliente(UUID.randomUUID()));
        entidad.setEstado(null);
        frm.setFilaSeleccionada(entidad);
        frm.btnAgregar();
        verify(ventaDAO, never()).crear(any());
    }

    @Test
    void testValidarAntesDeCrearGeneraUUID() {
        Venta entidad = crearVenta(null);
        frm.setFilaSeleccionada(entidad);
        frm.btnAgregar();
        assertNotNull(entidad.getId());
    }

    // ========== Tests para validarAntesDeActualizar ==========
    @Test
    void testValidarAntesDeActualizarExitoso() {
        UUID id = UUID.randomUUID();
        Venta entidad = crearVenta(id);
        frm.setFilaSeleccionada(entidad);
        when(ventaDAO.finById(id)).thenReturn(entidad);
        frm.btnActualizar();
        verify(ventaDAO).update(any());
    }

    @Test
    void testValidarAntesDeActualizarCamposNull() {
        UUID id = UUID.randomUUID();
        Venta entidad = new Venta();
        entidad.setId(id);
        entidad.setFecha(null);
        frm.setFilaSeleccionada(entidad);
        when(ventaDAO.finById(id)).thenReturn(entidad);
        frm.btnActualizar();
        verify(ventaDAO, never()).update(any());
    }

    // ========== Tests para validarAntesDeEliminar ==========
    @Test
    void testValidarAntesDeEliminarExitoso() {
        UUID id = UUID.randomUUID();
        Venta entidad = crearVenta(id);
        frm.setFilaSeleccionada(entidad);
        when(ventaDAO.finById(id)).thenReturn(entidad);
        when(ventaDetalleDAO.getDetallesPorVenta(id)).thenReturn(new ArrayList<>());
        frm.btnEliminar();
        verify(ventaDAO).delete(any());
    }

    @Test
    void testValidarAntesDeEliminarConDetalles() {
        UUID id = UUID.randomUUID();
        Venta entidad = crearVenta(id);
        frm.setFilaSeleccionada(entidad);
        when(ventaDAO.finById(id)).thenReturn(entidad);
        when(ventaDetalleDAO.getDetallesPorVenta(id)).thenReturn(Arrays.asList(crearVentaDetalle(UUID.randomUUID())));
        frm.btnEliminar();
        verify(ventaDAO, never()).delete(any());
    }

    @Test
    void testValidarAntesDeEliminarDetallesNull() {
        UUID id = UUID.randomUUID();
        Venta entidad = crearVenta(id);
        frm.setFilaSeleccionada(entidad);
        when(ventaDAO.finById(id)).thenReturn(entidad);
        when(ventaDetalleDAO.getDetallesPorVenta(id)).thenReturn(null);
        frm.btnEliminar();
        verify(ventaDAO).delete(any());
    }

    // ========== Tests para buscarEntidades ==========
    @Test
    void testBuscarEntidades() {
        when(ventaDAO.getEntityManager()).thenReturn(entityManager);
        when(entityManager.createQuery(anyString(), eq(Venta.class))).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(anyInt())).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(anyInt())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(crearVenta(UUID.randomUUID())));
        frm.initLazyModel();
        List<?> result = frm.getLazyModel().load(0, 10, null, null);
        assertEquals(1, result.size());
    }

    // ========== Tests para instanciarEntidad ==========
    @Test
    void testInstanciarEntidad() {
        frm.btnNuevo();
        Venta nueva = frm.getFilaSeleccionada();
        assertNotNull(nueva);
        assertNotNull(nueva.getFecha());
        assertEquals("PENDIENTE", nueva.getEstado());
    }

    // ========== Tests para btnNuevo ==========
    @Test
    void testBtnNuevo() {
        frm.setDetallesDeLaVenta(Arrays.asList(crearVentaDetalle(UUID.randomUUID())));
        frm.setDetalleSeleccionado(crearVentaDetalle(UUID.randomUUID()));
        frm.btnNuevo();
        assertEquals(CRUD.CREAR, frm.getEstado());
        assertTrue(frm.getDetallesDeLaVenta().isEmpty());
    }

    // ========== Tests para getClientesActivos ==========
    @Test
    void testGetClientesActivos() {
        when(clienteDAO.findRange(0, 1000)).thenReturn(Arrays.asList(crearCliente(UUID.randomUUID())));
        List<Cliente> clientes = frm.getClientesActivos();
        assertEquals(1, clientes.size());
    }

    @Test
    void testGetClientesActivosConException() throws Exception {
        inyectarCampo("clienteDAO", null);
        List<Cliente> clientes = frm.getClientesActivos();
        assertTrue(clientes.isEmpty());
    }

    // ========== Tests para isVentaNueva ==========
    @Test
    void testIsVentaNuevaTrue() {
        frm.setEstado(CRUD.CREAR);
        assertTrue(frm.isVentaNueva());
    }

    @Test
    void testIsVentaNuevaFalse() {
        frm.setEstado(CRUD.MODIFICAR);
        assertFalse(frm.isVentaNueva());
    }

    // ========== Tests para cargarDetallesVenta ==========
    @Test
    void testCargarDetallesVentaConId() {
        UUID id = UUID.randomUUID();
        Venta venta = crearVenta(id);
        frm.setFilaSeleccionada(venta);
        when(ventaDetalleDAO.getDetallesPorVenta(id)).thenReturn(Arrays.asList(crearVentaDetalle(UUID.randomUUID())));
        frm.cargarDetallesVenta();
        assertEquals(1, frm.getDetallesDeLaVenta().size());
    }

    @Test
    void testCargarDetallesVentaSinId() {
        Venta venta = new Venta();
        venta.setId(null);
        frm.setFilaSeleccionada(venta);
        frm.cargarDetallesVenta();
        assertTrue(frm.getDetallesDeLaVenta().isEmpty());
    }

    @Test
    void testCargarDetallesVentaFilaNull() {
        frm.setFilaSeleccionada(null);
        frm.cargarDetallesVenta();
        assertTrue(frm.getDetallesDeLaVenta().isEmpty());
    }

    @Test
    void testCargarDetallesVentaConException() throws Exception {
        UUID id = UUID.randomUUID();
        Venta venta = crearVenta(id);
        frm.setFilaSeleccionada(venta);
        inyectarCampo("ventaDetalleDAO", null);
        frm.cargarDetallesVenta();
        assertTrue(frm.getDetallesDeLaVenta().isEmpty());
    }

    // ========== Tests para prepararEditarDetalle ==========
    @Test
    void testPrepararEditarDetalle() {
        VentaDetalle detalle = crearVentaDetalle(UUID.randomUUID());
        frm.prepararEditarDetalle(detalle);
        assertEquals(detalle, frm.getDetalleSeleccionado());
    }

    // ========== Tests para guardarDetalle ==========
    @Test
    void testGuardarDetalleCrear() {
        UUID ventaId = UUID.randomUUID();
        Venta venta = crearVenta(ventaId);
        frm.setFilaSeleccionada(venta);

        VentaDetalle detalle = crearVentaDetalle(UUID.randomUUID());
        frm.setDetalleSeleccionado(detalle);

        when(ventaDetalleDAO.finById(any())).thenReturn(null);
        when(ventaDetalleDAO.getDetallesPorVenta(ventaId)).thenReturn(new ArrayList<>());

        frm.guardarDetalle();

        verify(ventaDetalleDAO).crear(detalle);
        verify(primeFaces).executeScript("PF('dlgVentaDetalle').hide()");
    }

    @Test
    void testGuardarDetalleActualizar() {
        UUID ventaId = UUID.randomUUID();
        Venta venta = crearVenta(ventaId);
        frm.setFilaSeleccionada(venta);

        VentaDetalle detalle = crearVentaDetalle(UUID.randomUUID());
        frm.setDetalleSeleccionado(detalle);

        when(ventaDetalleDAO.finById(any())).thenReturn(detalle);
        when(ventaDetalleDAO.getDetallesPorVenta(ventaId)).thenReturn(new ArrayList<>());

        frm.guardarDetalle();

        verify(ventaDetalleDAO).update(detalle);
    }

    @Test
    void testGuardarDetalleConException() throws Exception {
        VentaDetalle detalle = crearVentaDetalle(UUID.randomUUID());
        frm.setDetalleSeleccionado(detalle);
        inyectarCampo("ventaDetalleDAO", null);
        frm.guardarDetalle();
    }

    // ========== Tests para eliminarDetalle ==========
    @Test
    void testEliminarDetalleExitoso() {
        UUID ventaId = UUID.randomUUID();
        Venta venta = crearVenta(ventaId);
        frm.setFilaSeleccionada(venta);

        VentaDetalle detalle = crearVentaDetalle(UUID.randomUUID());
        when(ventaDetalleDAO.getDetallesPorVenta(ventaId)).thenReturn(new ArrayList<>());

        frm.eliminarDetalle(detalle);

        verify(ventaDetalleDAO).delete(detalle);
    }

    @Test
    void testEliminarDetalleConException() throws Exception {
        VentaDetalle detalle = crearVentaDetalle(UUID.randomUUID());
        inyectarCampo("ventaDetalleDAO", null);
        frm.eliminarDetalle(detalle);
    }

    // ========== Tests para isDetalleNuevo ==========
    @Test
    void testIsDetalleNuevoDetalleNull() {
        frm.setDetalleSeleccionado(null);
        assertTrue(frm.isDetalleNuevo());
    }

    @Test
    void testIsDetalleNuevoIdNull() {
        VentaDetalle detalle = new VentaDetalle();
        detalle.setId(null);
        frm.setDetalleSeleccionado(detalle);
        assertTrue(frm.isDetalleNuevo());
    }

    @Test
    void testIsDetalleNuevoNoExisteEnBD() {
        VentaDetalle detalle = crearVentaDetalle(UUID.randomUUID());
        frm.setDetalleSeleccionado(detalle);
        when(ventaDetalleDAO.finById(any())).thenReturn(null);
        assertTrue(frm.isDetalleNuevo());
    }

    @Test
    void testIsDetalleNuevoExisteEnBD() {
        VentaDetalle detalle = crearVentaDetalle(UUID.randomUUID());
        frm.setDetalleSeleccionado(detalle);
        when(ventaDetalleDAO.finById(any())).thenReturn(detalle);
        assertFalse(frm.isDetalleNuevo());
    }

    @Test
    void testIsDetalleNuevoConException() {
        VentaDetalle detalle = crearVentaDetalle(UUID.randomUUID());
        frm.setDetalleSeleccionado(detalle);
        when(ventaDetalleDAO.finById(any())).thenThrow(new RuntimeException("Error"));
        assertTrue(frm.isDetalleNuevo());
    }

    // ========== Tests para prepararNuevoDetalle ==========
    @Test
    void testPrepararNuevoDetalle() {
        Venta venta = crearVenta(UUID.randomUUID());
        frm.setFilaSeleccionada(venta);
        frm.prepararNuevoDetalle();
        VentaDetalle detalle = frm.getDetalleSeleccionado();
        assertNotNull(detalle);
        assertNotNull(detalle.getId());
        assertEquals(venta, detalle.getIdVenta());
        assertEquals(BigDecimal.ONE, detalle.getCantidad());
        assertEquals(BigDecimal.ZERO, detalle.getPrecio());
        assertEquals("PENDIENTE", detalle.getEstado());
    }

    // ========== Tests para getFechaLocal / setFechaLocal ==========
    @Test
    void testGetFechaLocalConFecha() {
        Venta venta = crearVenta(UUID.randomUUID());
        frm.setFilaSeleccionada(venta);
        LocalDateTime fechaLocal = frm.getFechaLocal();
        assertNotNull(fechaLocal);
    }

    @Test
    void testGetFechaLocalFilaNull() {
        frm.setFilaSeleccionada(null);
        assertNull(frm.getFechaLocal());
    }

    @Test
    void testGetFechaLocalFechaNull() {
        Venta venta = new Venta();
        venta.setFecha(null);
        frm.setFilaSeleccionada(venta);
        assertNull(frm.getFechaLocal());
    }

    @Test
    void testSetFechaLocalConValor() {
        Venta venta = new Venta();
        frm.setFilaSeleccionada(venta);
        LocalDateTime fechaLocal = LocalDateTime.now();
        frm.setFechaLocal(fechaLocal);
        assertNotNull(venta.getFecha());
    }

    @Test
    void testSetFechaLocalNull() {
        Venta venta = new Venta();
        venta.setFecha(OffsetDateTime.now());
        frm.setFilaSeleccionada(venta);
        frm.setFechaLocal(null);
        assertNull(venta.getFecha());
    }

    @Test
    void testSetFechaLocalFilaNull() {
        frm.setFilaSeleccionada(null);
        assertDoesNotThrow(() -> frm.setFechaLocal(LocalDateTime.now()));
    }

    // ========== Tests para getEstadosDisponibles ==========
    @Test
    void testGetEstadosDisponibles() {
        List<SelectItem> estados = frm.getEstadosDisponibles();
        assertEquals(EstadoVenta.values().length, estados.size());
    }

    // ========== Tests para getEstadosDisponiblesDetalle ==========
    @Test
    void testGetEstadosDisponiblesDetalle() {
        List<SelectItem> estados = frm.getEstadosDisponiblesDetalle();
        assertEquals(EstadoVentaDetalle.values().length, estados.size());
    }

    // ========== Tests para getters y setters ==========
    @Test
    void testGettersYSetters() {
        // VentaDAO
        VentaDAO nuevoVentaDAO = mock(VentaDAO.class);
        frm.setVentaDAO(nuevoVentaDAO);
        assertEquals(nuevoVentaDAO, frm.getVentaDAO());

        // ClienteDAO
        ClienteDAO nuevoClienteDAO = mock(ClienteDAO.class);
        frm.setClienteDAO(nuevoClienteDAO);
        assertEquals(nuevoClienteDAO, frm.getClienteDAO());

        // VentaDetalleDAO
        VentaDetalleDAO nuevoVentaDetalleDAO = mock(VentaDetalleDAO.class);
        frm.setVentaDetalleDAO(nuevoVentaDetalleDAO);
        assertEquals(nuevoVentaDetalleDAO, frm.getVentaDetalleDAO());

        // ProductoDAO
        ProductoDAO nuevoProductoDAO = mock(ProductoDAO.class);
        frm.setProductoDAO(nuevoProductoDAO);
        assertEquals(nuevoProductoDAO, frm.getProductoDAO());

        // DetallesDeLaVenta
        List<VentaDetalle> detalles = new ArrayList<>();
        frm.setDetallesDeLaVenta(detalles);
        assertEquals(detalles, frm.getDetallesDeLaVenta());

        // DetalleSeleccionado
        VentaDetalle detalle = crearVentaDetalle(UUID.randomUUID());
        frm.setDetalleSeleccionado(detalle);
        assertEquals(detalle, frm.getDetalleSeleccionado());

        // ProductosDisponibles
        List<Producto> productos = new ArrayList<>();
        frm.setProductosDisponibles(productos);
        assertEquals(productos, frm.getProductosDisponibles());
    }

    @Test
    void testGetDetalleSeleccionadoNull() {
        frm.setDetalleSeleccionado(null);
        VentaDetalle resultado = frm.getDetalleSeleccionado();
        assertNotNull(resultado);
    }
}
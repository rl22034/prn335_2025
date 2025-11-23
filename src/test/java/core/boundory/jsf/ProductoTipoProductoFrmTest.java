package core.boundory.jsf;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.CRUD;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.MessageHelper;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.ProductoFrm;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.ProductoTipoProductoFrm;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.*;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.*;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductoTipoProductoFrmTest {

    @Mock
    private ProductoTipoProductoDAO productoTipoProductoDAO;
    @Mock
    private TipoProductoDAO tipoProductoDAO;
    @Mock
    private TipoProductoCaracteristicaDAO tipoProductoCaracteristicaDAO;
    @Mock
    private ProductoTipoProductoCaracteristicaDAO productoTipoProductoCaracteristicaDAO;
    @Mock
    private ProductoFrm productoBean;

    private ProductoTipoProductoFrm frm;
    private MockedStatic<MessageHelper> messageHelperMock;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Mockear MessageHelper estático
        messageHelperMock = mockStatic(MessageHelper.class);

        frm = new ProductoTipoProductoFrm();
        inyectarCampo("productoTipoProductoDAO", productoTipoProductoDAO);
        inyectarCampo("tipoProductoDAO", tipoProductoDAO);
        inyectarCampo("tipoProductoCaracteristicaDAO", tipoProductoCaracteristicaDAO);
        inyectarCampo("productoTipoProductoCaracteristicaDAO", productoTipoProductoCaracteristicaDAO);
        inyectarCampo("productoBean", productoBean);
    }

    @AfterEach
    void tearDown() {
        if (messageHelperMock != null) {
            messageHelperMock.close();
        }
    }

    private void inyectarCampo(String nombre, Object valor) throws Exception {
        Field field = ProductoTipoProductoFrm.class.getDeclaredField(nombre);
        field.setAccessible(true);
        field.set(frm, valor);
    }

    private Producto crearProductoConId(UUID id) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombreProducto("Producto Test");
        return p;
    }

    private TipoProducto crearTipoProducto(Long id) {
        TipoProducto tp = new TipoProducto();
        tp.setId(id);
        tp.setNombre("Tipo Test");
        tp.setActivo(true);
        return tp;
    }

    private TipoProductoCaracteristica crearCaracteristica(Long id, boolean obligatoria) {
        TipoProductoCaracteristica tpc = new TipoProductoCaracteristica();
        tpc.setId(id);
        tpc.setObligatorio(obligatoria);
        Caracteristica c = new Caracteristica();
        c.setId(id.intValue());
        c.setNombre("Caracteristica " + id);
        tpc.setIdCaracteristica(c);
        return tpc;
    }

    @Test
    void testInit() {
        when(productoBean.getFilaSeleccionada()).thenReturn(null);
        frm.init();
        assertNotNull(frm.getValoresCaracteristicas());
        assertNotNull(frm.getCaracteristicasPickList());
        assertEquals(CRUD.NINGUNO, frm.getEstado());
    }

    @Test
    void testRecargarTablaConLazyModelNull() {
        frm.init();
        frm.setLazyModel(null);
        assertDoesNotThrow(() -> frm.recargarTabla());
    }

    @Test
    void testRecargarTablaConLazyModelExistente() {
        Producto producto = crearProductoConId(UUID.randomUUID());
        when(productoBean.getFilaSeleccionada()).thenReturn(producto);
        frm.init();
        frm.initLazyModel();
        assertDoesNotThrow(() -> frm.recargarTabla());
    }

    @Test
    void testCrearEntidadSinTipoProducto() {
        frm.init();
        frm.setTipoProductoSeleccionado(null);
        assertDoesNotThrow(() -> frm.btnAgregar());
    }

    @Test
    void testCrearEntidadSinProductoSeleccionado() {
        frm.init();
        frm.setTipoProductoSeleccionado(crearTipoProducto(1L));
        when(productoBean.getFilaSeleccionada()).thenReturn(null);
        when(tipoProductoCaracteristicaDAO.findByTipoProducto(1L)).thenReturn(new ArrayList<>());
        frm.setCaracteristicasPickList(new DualListModel<>(new ArrayList<>(), new ArrayList<>()));
        assertDoesNotThrow(() -> frm.btnAgregar());
    }

    @Test
    void testCrearEntidadProductoSinId() {
        frm.init();
        frm.setTipoProductoSeleccionado(crearTipoProducto(1L));
        Producto productoSinId = new Producto();
        productoSinId.setId(null);
        when(productoBean.getFilaSeleccionada()).thenReturn(productoSinId);
        when(tipoProductoCaracteristicaDAO.findByTipoProducto(1L)).thenReturn(new ArrayList<>());
        frm.setCaracteristicasPickList(new DualListModel<>(new ArrayList<>(), new ArrayList<>()));
        assertDoesNotThrow(() -> frm.btnAgregar());
    }

    @Test
    void testCrearEntidadExitoso() {
        frm.init();
        TipoProducto tipo = crearTipoProducto(1L);
        frm.setTipoProductoSeleccionado(tipo);
        Producto producto = crearProductoConId(UUID.randomUUID());
        when(productoBean.getFilaSeleccionada()).thenReturn(producto);
        when(tipoProductoCaracteristicaDAO.findByTipoProducto(1L)).thenReturn(new ArrayList<>());
        frm.setCaracteristicasPickList(new DualListModel<>(new ArrayList<>(), new ArrayList<>()));
        ProductoTipoProducto entidad = new ProductoTipoProducto();
        entidad.setId(UUID.randomUUID());
        frm.setFilaSeleccionada(entidad);
        assertDoesNotThrow(() -> frm.btnAgregar());
        verify(productoTipoProductoDAO).crear(any(ProductoTipoProducto.class));
    }

    @Test
    void testActualizarEntidadSinTipoProducto() {
        frm.init();
        frm.setTipoProductoSeleccionado(null);
        ProductoTipoProducto entidad = new ProductoTipoProducto();
        entidad.setId(UUID.randomUUID());
        frm.setFilaSeleccionada(entidad);
        frm.setEstado(CRUD.MODIFICAR);
        assertDoesNotThrow(() -> frm.btnActualizar());
    }

    @Test
    void testActualizarEntidadExitoso() {
        frm.init();
        TipoProducto tipo = crearTipoProducto(1L);
        frm.setTipoProductoSeleccionado(tipo);
        when(tipoProductoCaracteristicaDAO.findByTipoProducto(1L)).thenReturn(new ArrayList<>());
        frm.setCaracteristicasPickList(new DualListModel<>(new ArrayList<>(), new ArrayList<>()));
        ProductoTipoProducto entidad = new ProductoTipoProducto();
        UUID id = UUID.randomUUID();
        entidad.setId(id);
        frm.setFilaSeleccionada(entidad);
        when(productoTipoProductoDAO.finById(id)).thenReturn(entidad);
        assertDoesNotThrow(() -> frm.btnActualizar());
        verify(productoTipoProductoDAO).update(any(ProductoTipoProducto.class));
    }

    @Test
    void testValidarAntesDeEliminar() {
        ProductoTipoProducto entidad = new ProductoTipoProducto();
        UUID id = UUID.randomUUID();
        entidad.setId(id);
        frm.setFilaSeleccionada(entidad);
        when(productoTipoProductoDAO.finById(id)).thenReturn(entidad);
        frm.btnEliminar();
        verify(productoTipoProductoCaracteristicaDAO).eliminarPorProductoTipoProducto(id);
    }

    @Test
    void testBuscarEntidadesConProducto() {
        UUID productoId = UUID.randomUUID();
        Producto producto = crearProductoConId(productoId);
        when(productoBean.getFilaSeleccionada()).thenReturn(producto);
        when(productoTipoProductoDAO.findByIdProducto(productoId, 0, 10)).thenReturn(Arrays.asList(new ProductoTipoProducto()));
        frm.init();
        frm.initLazyModel();
        assertNotNull(frm.getLazyModel());
    }

    @Test
    void testBuscarEntidadesSinProducto() {
        when(productoBean.getFilaSeleccionada()).thenReturn(null);
        frm.init();
        frm.initLazyModel();
        assertNotNull(frm.getLazyModel());
    }

    @Test
    void testContarEntidadesConProducto() {
        UUID productoId = UUID.randomUUID();
        Producto producto = crearProductoConId(productoId);
        when(productoBean.getFilaSeleccionada()).thenReturn(producto);
        when(productoTipoProductoDAO.countByIdProducto(productoId)).thenReturn(5L);
        frm.init();
        frm.initLazyModel();
        assertEquals(5, frm.getLazyModel().count(null));
    }

    @Test
    void testContarEntidadesSinProducto() {
        when(productoBean.getFilaSeleccionada()).thenReturn(null);
        frm.init();
        frm.initLazyModel();
        assertEquals(0, frm.getLazyModel().count(null));
    }

    @Test
    void testInstanciarEntidadConProducto() {
        Producto producto = crearProductoConId(UUID.randomUUID());
        when(productoBean.getFilaSeleccionada()).thenReturn(producto);
        frm.init();
        ProductoTipoProducto nuevo = frm.getFilaSeleccionada();
        assertNotNull(nuevo);
        assertNotNull(nuevo.getId());
        assertTrue(nuevo.getActivo());
        assertEquals(producto, nuevo.getIdProducto());
    }

    @Test
    void testInstanciarEntidadSinProducto() {
        when(productoBean.getFilaSeleccionada()).thenReturn(null);
        frm.init();
        ProductoTipoProducto nuevo = frm.getFilaSeleccionada();
        assertNotNull(nuevo);
        assertNull(nuevo.getIdProducto());
    }

    @Test
    void testBtnNuevo() {
        when(productoBean.getFilaSeleccionada()).thenReturn(crearProductoConId(UUID.randomUUID()));
        frm.init();
        frm.setTipoProductoSeleccionado(crearTipoProducto(1L));
        frm.getValoresCaracteristicas().put(1L, "valor");
        frm.btnNuevo();
        assertNull(frm.getTipoProductoSeleccionado());
        assertTrue(frm.getValoresCaracteristicas().isEmpty());
        assertEquals(CRUD.CREAR, frm.getEstado());
    }

    @Test
    void testOnRowSelectConTipoProducto() {
        frm.init();
        ProductoTipoProducto ptp = new ProductoTipoProducto();
        ptp.setId(UUID.randomUUID());
        TipoProducto tipo = crearTipoProducto(1L);
        ptp.setIdTipoProducto(tipo);
        when(tipoProductoCaracteristicaDAO.findByTipoProducto(1L)).thenReturn(new ArrayList<>());
        when(productoTipoProductoCaracteristicaDAO.findByProductoTipoProducto(any())).thenReturn(new ArrayList<>());
        @SuppressWarnings("unchecked")
        SelectEvent<ProductoTipoProducto> event = mock(SelectEvent.class);
        when(event.getObject()).thenReturn(ptp);
        frm.onRowSelect(event);
        assertEquals(tipo, frm.getTipoProductoSeleccionado());
        assertEquals(CRUD.MODIFICAR, frm.getEstado());
    }

    @Test
    void testOnRowSelectSinTipoProducto() {
        frm.init();
        ProductoTipoProducto ptp = new ProductoTipoProducto();
        ptp.setIdTipoProducto(null);
        @SuppressWarnings("unchecked")
        SelectEvent<ProductoTipoProducto> event = mock(SelectEvent.class);
        when(event.getObject()).thenReturn(ptp);
        frm.onRowSelect(event);
        assertEquals(CRUD.MODIFICAR, frm.getEstado());
    }

    @Test
    void testOnTipoProductoChangeNull() {
        frm.init();
        frm.setTipoProductoSeleccionado(null);
        frm.onTipoProductoChange();
        assertTrue(frm.getCaracteristicasPickList().getSource().isEmpty());
    }

    @Test
    void testOnTipoProductoChangeConTipo() {
        frm.init();
        TipoProducto tipo = crearTipoProducto(1L);
        frm.setTipoProductoSeleccionado(tipo);
        when(tipoProductoCaracteristicaDAO.findByTipoProducto(1L)).thenReturn(new ArrayList<>());
        ProductoTipoProducto ptp = new ProductoTipoProducto();
        frm.setFilaSeleccionada(ptp);
        frm.onTipoProductoChange();
        assertEquals(tipo, frm.getFilaSeleccionada().getIdTipoProducto());
    }

    @Test
    void testCargarCaracteristicasModoNuevo() {
        frm.init();
        TipoProducto tipo = crearTipoProducto(1L);
        frm.setTipoProductoSeleccionado(tipo);
        TipoProductoCaracteristica obligatoria = crearCaracteristica(1L, true);
        TipoProductoCaracteristica opcional = crearCaracteristica(2L, false);
        when(tipoProductoCaracteristicaDAO.findByTipoProducto(1L)).thenReturn(Arrays.asList(obligatoria, opcional));
        ProductoTipoProducto ptp = new ProductoTipoProducto();
        ptp.setId(null);
        frm.setFilaSeleccionada(ptp);
        frm.onTipoProductoChange();
        assertEquals(1, frm.getCaracteristicasPickList().getSource().size());
        assertEquals(1, frm.getCaracteristicasPickList().getTarget().size());
    }

    @Test
    void testCargarCaracteristicasModoEdicion() {
        frm.init();
        TipoProducto tipo = crearTipoProducto(1L);
        frm.setTipoProductoSeleccionado(tipo);
        TipoProductoCaracteristica tpc1 = crearCaracteristica(1L, true);
        when(tipoProductoCaracteristicaDAO.findByTipoProducto(1L)).thenReturn(Arrays.asList(tpc1));
        ProductoTipoProducto ptp = new ProductoTipoProducto();
        UUID ptpId = UUID.randomUUID();
        ptp.setId(ptpId);
        frm.setFilaSeleccionada(ptp);
        ProductoTipoProductoCaracteristica ptpc = new ProductoTipoProductoCaracteristica();
        ptpc.setIdTipoProductoCaracteristica(tpc1);
        ptpc.setValor("valor existente");
        when(productoTipoProductoCaracteristicaDAO.findByProductoTipoProducto(ptpId)).thenReturn(Arrays.asList(ptpc));
        frm.onTipoProductoChange();
        assertEquals("valor existente", frm.getValoresCaracteristicas().get(1L));
    }

    @Test
    void testCargarCaracteristicasConValorNull() {
        frm.init();
        TipoProducto tipo = crearTipoProducto(1L);
        frm.setTipoProductoSeleccionado(tipo);
        TipoProductoCaracteristica tpc = crearCaracteristica(1L, false);
        when(tipoProductoCaracteristicaDAO.findByTipoProducto(1L)).thenReturn(Arrays.asList(tpc));
        ProductoTipoProducto ptp = new ProductoTipoProducto();
        ptp.setId(UUID.randomUUID());
        frm.setFilaSeleccionada(ptp);
        ProductoTipoProductoCaracteristica ptpc = new ProductoTipoProductoCaracteristica();
        ptpc.setIdTipoProductoCaracteristica(tpc);
        ptpc.setValor(null);
        when(productoTipoProductoCaracteristicaDAO.findByProductoTipoProducto(any())).thenReturn(Arrays.asList(ptpc));
        frm.onTipoProductoChange();
        assertEquals("", frm.getValoresCaracteristicas().get(1L));
    }

    @Test
    void testCargarCaracteristicasTipoSinId() {
        frm.init();
        TipoProducto tipo = new TipoProducto();
        tipo.setId(null);
        frm.setTipoProductoSeleccionado(tipo);
        frm.onTipoProductoChange();
        assertTrue(frm.getCaracteristicasPickList().getSource().isEmpty());
    }

    @Test
    void testCargarCaracteristicasObligatoriaNoGuardada() {
        frm.init();
        TipoProducto tipo = crearTipoProducto(1L);
        frm.setTipoProductoSeleccionado(tipo);
        TipoProductoCaracteristica obligatoria = crearCaracteristica(1L, true);
        when(tipoProductoCaracteristicaDAO.findByTipoProducto(1L)).thenReturn(Arrays.asList(obligatoria));
        ProductoTipoProducto ptp = new ProductoTipoProducto();
        ptp.setId(UUID.randomUUID());
        frm.setFilaSeleccionada(ptp);
        when(productoTipoProductoCaracteristicaDAO.findByProductoTipoProducto(any())).thenReturn(new ArrayList<>());
        frm.onTipoProductoChange();
        assertEquals(1, frm.getCaracteristicasPickList().getTarget().size());
    }

    @Test
    void testCargarCaracteristicasConException() {
        frm.init();
        TipoProducto tipo = crearTipoProducto(1L);
        frm.setTipoProductoSeleccionado(tipo);
        when(tipoProductoCaracteristicaDAO.findByTipoProducto(1L)).thenThrow(new RuntimeException("Error"));
        frm.onTipoProductoChange();
        assertTrue(frm.getCaracteristicasPickList().getSource().isEmpty());
    }

    @Test
    void testValidarObligatoriasFaltante() {
        // Inicialización y configuración del escenario
        frm.init();
        TipoProducto tipo = crearTipoProducto(1L);
        frm.setTipoProductoSeleccionado(tipo);

        // Configuración de la característica obligatoria faltante
        TipoProductoCaracteristica obligatoria = crearCaracteristica(1L, true);
        when(tipoProductoCaracteristicaDAO.findByTipoProducto(1L)).thenReturn(Arrays.asList(obligatoria));

        // La característica obligatoria está ausente en la lista seleccionada
        frm.setCaracteristicasPickList(new DualListModel<>(new ArrayList<>(), new ArrayList<>()));

        Producto producto = crearProductoConId(UUID.randomUUID());
        when(productoBean.getFilaSeleccionada()).thenReturn(producto);
        ProductoTipoProducto entidad = new ProductoTipoProducto();
        entidad.setId(UUID.randomUUID());
        frm.setFilaSeleccionada(entidad);

        // Ejecución
        frm.btnAgregar();

        // VERIFICACIÓN CORREGIDA: Espera la llamada con TRES argumentos String
        messageHelperMock.verify(() -> MessageHelper.addErrorMessage(
                        anyString(),
                        anyString(),
                        anyString()), // Se espera el tercer argumento (el mensaje de detalle)
                atLeastOnce()
        );
    }
    @Test
    void testValidarObligatoriasSinNombre() {
        frm.init();
        TipoProducto tipo = crearTipoProducto(1L);
        frm.setTipoProductoSeleccionado(tipo);
        TipoProductoCaracteristica obligatoria = new TipoProductoCaracteristica();
        obligatoria.setId(1L);
        obligatoria.setObligatorio(true);
        obligatoria.setIdCaracteristica(null);
        when(tipoProductoCaracteristicaDAO.findByTipoProducto(1L)).thenReturn(Arrays.asList(obligatoria));
        frm.setCaracteristicasPickList(new DualListModel<>(new ArrayList<>(), new ArrayList<>()));
        Producto producto = crearProductoConId(UUID.randomUUID());
        when(productoBean.getFilaSeleccionada()).thenReturn(producto);
        ProductoTipoProducto entidad = new ProductoTipoProducto();
        entidad.setId(UUID.randomUUID());
        frm.setFilaSeleccionada(entidad);
        frm.btnAgregar();
    }

    @Test
    void testGuardarCaracteristicasConValorNull() {
        frm.init();
        TipoProducto tipo = crearTipoProducto(1L);
        frm.setTipoProductoSeleccionado(tipo);
        TipoProductoCaracteristica tpc = crearCaracteristica(1L, false);
        List<TipoProductoCaracteristica> asignadas = new ArrayList<>();
        asignadas.add(tpc);
        frm.setCaracteristicasPickList(new DualListModel<>(new ArrayList<>(), asignadas));
        frm.getValoresCaracteristicas().clear();
        when(tipoProductoCaracteristicaDAO.findByTipoProducto(1L)).thenReturn(new ArrayList<>());
        Producto producto = crearProductoConId(UUID.randomUUID());
        when(productoBean.getFilaSeleccionada()).thenReturn(producto);
        ProductoTipoProducto entidad = new ProductoTipoProducto();
        entidad.setId(UUID.randomUUID());
        frm.setFilaSeleccionada(entidad);
        frm.btnAgregar();
        verify(productoTipoProductoCaracteristicaDAO).crear(any(ProductoTipoProductoCaracteristica.class));
    }

    @Test
    void testGetTiposProductoActivos() throws Exception {
        TipoProducto activo = crearTipoProducto(1L);
        TipoProducto inactivo = crearTipoProducto(2L);
        inactivo.setActivo(false);
        TipoProducto nullActivo = crearTipoProducto(3L);
        nullActivo.setActivo(null);
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(Arrays.asList(activo, inactivo, nullActivo));
        List<TipoProducto> resultado = frm.getTiposProductoActivos();
        assertEquals(1, resultado.size());
    }

    @Test
    void testGetTiposProductoActivosConException() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenThrow(new RuntimeException("Error"));
        List<TipoProducto> resultado = frm.getTiposProductoActivos();
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testGettersYSetters() {
        frm.init();
        DualListModel<TipoProductoCaracteristica> pickList = new DualListModel<>();
        frm.setCaracteristicasPickList(pickList);
        assertEquals(pickList, frm.getCaracteristicasPickList());
        Map<Long, String> valores = new HashMap<>();
        valores.put(1L, "test");
        frm.setValoresCaracteristicas(valores);
        assertEquals(valores, frm.getValoresCaracteristicas());
        TipoProducto tipo = crearTipoProducto(1L);
        frm.setTipoProductoSeleccionado(tipo);
        assertEquals(tipo, frm.getTipoProductoSeleccionado());
    }

    @Test
    void testBuscarEntidadesConException() {
        UUID productoId = UUID.randomUUID();
        Producto producto = crearProductoConId(productoId);
        when(productoBean.getFilaSeleccionada()).thenReturn(producto);
        when(productoTipoProductoDAO.findByIdProducto(any(), anyInt(), anyInt())).thenThrow(new RuntimeException("Error"));
        frm.init();
        frm.initLazyModel();
        List<?> result = frm.getLazyModel().load(0, 10, null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testContarEntidadesConException() {
        UUID productoId = UUID.randomUUID();
        Producto producto = crearProductoConId(productoId);
        when(productoBean.getFilaSeleccionada()).thenReturn(producto);
        when(productoTipoProductoDAO.countByIdProducto(any())).thenThrow(new RuntimeException("Error"));
        frm.init();
        frm.initLazyModel();
        int count = frm.getLazyModel().count(null);
        assertEquals(0, count);
    }

    @Test
    void testGuardarCaracteristicasConExceptionEnCrear() {
        frm.init();
        TipoProducto tipo = crearTipoProducto(1L);
        frm.setTipoProductoSeleccionado(tipo);
        TipoProductoCaracteristica tpc = crearCaracteristica(1L, false);
        List<TipoProductoCaracteristica> asignadas = new ArrayList<>();
        asignadas.add(tpc);
        frm.setCaracteristicasPickList(new DualListModel<>(new ArrayList<>(), asignadas));
        frm.getValoresCaracteristicas().put(1L, "valor");
        when(tipoProductoCaracteristicaDAO.findByTipoProducto(1L)).thenReturn(new ArrayList<>());
        doThrow(new RuntimeException("Error")).when(productoTipoProductoCaracteristicaDAO).crear(any());
        Producto producto = crearProductoConId(UUID.randomUUID());
        when(productoBean.getFilaSeleccionada()).thenReturn(producto);
        ProductoTipoProducto entidad = new ProductoTipoProducto();
        entidad.setId(UUID.randomUUID());
        frm.setFilaSeleccionada(entidad);
        frm.btnAgregar();
    }
}
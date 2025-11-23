package core.boundory.jsf;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.CRUD;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.MessageHelper;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.TipoProductoFrm;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoProductoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProducto;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoProductoCaracteristica;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TipoProductoFrmTest {

    @Mock
    private TipoProductoDAO tipoProductoDAO;
    @Mock
    private CaracteristicaDAO caracteristicaDAO;
    @Mock
    private TipoProductoCaracteristicaDAO tipoProductoCaracteristicaDAO;

    private TipoProductoFrm frm;
    private MockedStatic<MessageHelper> messageHelperMock;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        messageHelperMock = mockStatic(MessageHelper.class);

        frm = new TipoProductoFrm();
        inyectarCampo("tipoProductoDAO", tipoProductoDAO);
        inyectarCampo("caracteristicaDAO", caracteristicaDAO);
        inyectarCampo("tipoProductoCaracteristicaDAO", tipoProductoCaracteristicaDAO);
    }

    @AfterEach
    void tearDown() {
        if (messageHelperMock != null) {
            messageHelperMock.close();
        }
    }

    private void inyectarCampo(String nombre, Object valor) throws Exception {
        Field field = TipoProductoFrm.class.getDeclaredField(nombre);
        field.setAccessible(true);
        field.set(frm, valor);
    }

    private TipoProducto crearTipoProducto(Long id, String nombre, TipoProducto padre) {
        TipoProducto tp = new TipoProducto();
        tp.setId(id);
        tp.setNombre(nombre);
        tp.setActivo(true);
        tp.setIdTipoProductoPadre(padre);
        return tp;
    }

    // ========== Tests para init ==========
    @Test
    void testInit() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();
        assertEquals(CRUD.NINGUNO, frm.getEstado());
        assertNotNull(frm.getFilaSeleccionada());
        assertNotNull(frm.getRootNode());
    }

    // ========== Tests para cargarArbol ==========
    @Test
    void testCargarArbolVacio() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.cargarArbol();
        assertNotNull(frm.getRootNode());
        assertEquals(0, frm.getRootNode().getChildCount());
    }

    @Test
    void testCargarArbolConDatos() throws Exception {
        TipoProducto raiz = crearTipoProducto(1L, "Raiz", null);
        TipoProducto hijo = crearTipoProducto(2L, "Hijo", raiz);
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(Arrays.asList(raiz, hijo));
        frm.cargarArbol();
        assertNotNull(frm.getRootNode());
        assertEquals(1, frm.getRootNode().getChildCount());
    }

    @Test
    void testCargarArbolConMultiplesNiveles() throws Exception {
        TipoProducto raiz = crearTipoProducto(1L, "Raiz", null);
        TipoProducto hijo = crearTipoProducto(2L, "Hijo", raiz);
        TipoProducto nieto = crearTipoProducto(3L, "Nieto", hijo);
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(Arrays.asList(raiz, hijo, nieto));
        frm.cargarArbol();
        assertEquals(1, frm.getRootNode().getChildCount());
    }

    @Test
    void testCargarArbolConException() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenThrow(new RuntimeException("Error"));
        assertDoesNotThrow(() -> frm.cargarArbol());
    }

    // ========== Tests para onNodeSelect ==========
    @Test
    void testOnNodeSelectConNodo() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();

        TipoProducto tipo = crearTipoProducto(1L, "Test", null);
        TreeNode<TipoProducto> nodo = new DefaultTreeNode<>(tipo, null);
        frm.setSelectedNode(nodo);

        when(tipoProductoCaracteristicaDAO.findByTipoProducto(1L)).thenReturn(new ArrayList<>());

        frm.onNodeSelect();

        assertEquals(tipo, frm.getFilaSeleccionada());
        assertEquals(CRUD.MODIFICAR, frm.getEstado());
    }

    @Test
    void testOnNodeSelectSinNodo() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();
        frm.setSelectedNode(null);
        frm.onNodeSelect();
        assertEquals(CRUD.NINGUNO, frm.getEstado());
    }

    // ========== Tests para cargarCaracteristicas ==========
    @Test
    void testCargarCaracteristicasConId() throws Exception {
        TipoProducto tipo = crearTipoProducto(1L, "Test", null);
        frm.setFilaSeleccionada(tipo);

        List<TipoProductoCaracteristica> lista = new ArrayList<>();
        lista.add(new TipoProductoCaracteristica());
        when(tipoProductoCaracteristicaDAO.findByTipoProducto(1L)).thenReturn(lista);

        frm.cargarCaracteristicas();

        assertEquals(1, frm.getCaracteristicasList().size());
    }

    @Test
    void testCargarCaracteristicasSinId() {
        TipoProducto tipo = new TipoProducto();
        tipo.setId(null);
        frm.setFilaSeleccionada(tipo);

        frm.cargarCaracteristicas();

        assertTrue(frm.getCaracteristicasList().isEmpty());
    }

    @Test
    void testCargarCaracteristicasFilaNull() {
        frm.setFilaSeleccionada(null);
        frm.cargarCaracteristicas();
        // getFilaSeleccionada() crea una nueva instancia si es null
    }

    @Test
    void testCargarCaracteristicasConException() throws Exception {
        TipoProducto tipo = crearTipoProducto(1L, "Test", null);
        frm.setFilaSeleccionada(tipo);
        when(tipoProductoCaracteristicaDAO.findByTipoProducto(1L)).thenThrow(new RuntimeException("Error"));

        frm.cargarCaracteristicas();

        assertTrue(frm.getCaracteristicasList().isEmpty());
    }

    // ========== Tests para getTipoProductoList ==========
    @Test
    void testGetTipoProductoListPrimeraVez() throws Exception {
        TipoProducto tipo = crearTipoProducto(1L, "Test", null);
        frm.setFilaSeleccionada(tipo);

        List<TipoProducto> lista = Arrays.asList(crearTipoProducto(2L, "Otro", null));
        when(tipoProductoDAO.findValidosParaPadre(1L)).thenReturn(lista);

        List<TipoProducto> resultado = frm.getTipoProductoList();

        assertEquals(1, resultado.size());
    }

    @Test
    void testGetTipoProductoListConFilaNull() throws Exception {
        frm.setFilaSeleccionada(null);

        List<TipoProducto> lista = Arrays.asList(crearTipoProducto(1L, "Test", null));
        when(tipoProductoDAO.findValidosParaPadre(null)).thenReturn(lista);

        // getFilaSeleccionada crea nueva instancia, pero getId() será null
        List<TipoProducto> resultado = frm.getTipoProductoList();
        assertNotNull(resultado);
    }

    @Test
    void testGetTipoProductoListConException() throws Exception {
        TipoProducto tipo = crearTipoProducto(1L, "Test", null);
        frm.setFilaSeleccionada(tipo);
        when(tipoProductoDAO.findValidosParaPadre(1L)).thenThrow(new RuntimeException("Error"));

        List<TipoProducto> resultado = frm.getTipoProductoList();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void testGetTipoProductoListCacheada() throws Exception {
        TipoProducto tipo = crearTipoProducto(1L, "Test", null);
        frm.setFilaSeleccionada(tipo);

        List<TipoProducto> lista = Arrays.asList(crearTipoProducto(2L, "Otro", null));
        when(tipoProductoDAO.findValidosParaPadre(1L)).thenReturn(lista);

        frm.getTipoProductoList();
        frm.getTipoProductoList();

        verify(tipoProductoDAO, times(1)).findValidosParaPadre(1L);
    }

    // ========== Tests para btnNuevo ==========
    @Test
    void testBtnNuevo() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();

        frm.btnNuevo();

        assertEquals(CRUD.CREAR, frm.getEstado());
        assertNull(frm.getSelectedNode());
        assertTrue(frm.getFilaSeleccionada().getActivo());
    }

    // ========== Tests para btnAgregar ==========
    @Test
    void testBtnAgregarExitoso() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();

        TipoProducto tipo = crearTipoProducto(null, "Nuevo", null);
        frm.setFilaSeleccionada(tipo);

        frm.btnAgregar();

        verify(tipoProductoDAO).crear(tipo);
        assertEquals(CRUD.NINGUNO, frm.getEstado());
        messageHelperMock.verify(() -> MessageHelper.addInfoMessage(anyString(), anyString()));
    }

    @Test
    void testBtnAgregarNombreNull() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();

        TipoProducto tipo = new TipoProducto();
        tipo.setNombre(null);
        frm.setFilaSeleccionada(tipo);

        frm.btnAgregar();

        verify(tipoProductoDAO, never()).crear(any());
    }

    @Test
    void testBtnAgregarNombreVacio() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();

        TipoProducto tipo = new TipoProducto();
        tipo.setNombre("   ");
        frm.setFilaSeleccionada(tipo);

        frm.btnAgregar();

        verify(tipoProductoDAO, never()).crear(any());
    }

    @Test
    void testBtnAgregarConException() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();

        TipoProducto tipo = crearTipoProducto(null, "Nuevo", null);
        frm.setFilaSeleccionada(tipo);
        doThrow(new RuntimeException("Error DB")).when(tipoProductoDAO).crear(any());

        frm.btnAgregar();

        messageHelperMock.verify(() -> MessageHelper.addErrorMessage(anyString(), anyString(), anyString()));
    }

    // ========== Tests para btnActualizar ==========
    @Test
    void testBtnActualizarExitoso() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();

        TipoProducto tipo = crearTipoProducto(1L, "Actualizado", null);
        frm.setFilaSeleccionada(tipo);

        frm.btnActualizar();

        verify(tipoProductoDAO).update(tipo);
        assertEquals(CRUD.NINGUNO, frm.getEstado());
    }

    @Test
    void testBtnActualizarNombreNull() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();

        TipoProducto tipo = new TipoProducto();
        tipo.setId(1L);
        tipo.setNombre(null);
        frm.setFilaSeleccionada(tipo);

        frm.btnActualizar();

        verify(tipoProductoDAO, never()).update(any());
    }

    @Test
    void testBtnActualizarNombreVacio() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();

        TipoProducto tipo = new TipoProducto();
        tipo.setId(1L);
        tipo.setNombre("");
        frm.setFilaSeleccionada(tipo);

        frm.btnActualizar();

        verify(tipoProductoDAO, never()).update(any());
    }

    @Test
    void testBtnActualizarConException() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();

        TipoProducto tipo = crearTipoProducto(1L, "Test", null);
        frm.setFilaSeleccionada(tipo);
        doThrow(new RuntimeException("Error")).when(tipoProductoDAO).update(any());

        frm.btnActualizar();
    }

    // ========== Tests para btnEliminar ==========
    @Test
    void testBtnEliminarExitoso() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();

        TipoProducto tipo = crearTipoProducto(1L, "Eliminar", null);
        TipoProducto original = crearTipoProducto(1L, "Eliminar", null);
        frm.setFilaSeleccionada(tipo);
        when(tipoProductoDAO.finById(1L)).thenReturn(original);

        frm.btnEliminar();

        verify(tipoProductoDAO).delete(tipo);
        assertEquals(CRUD.NINGUNO, frm.getEstado());
    }

    @Test
    void testBtnEliminarRegistroNoExiste() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();

        TipoProducto tipo = crearTipoProducto(1L, "Test", null);
        frm.setFilaSeleccionada(tipo);
        when(tipoProductoDAO.finById(1L)).thenReturn(null);

        frm.btnEliminar();

        verify(tipoProductoDAO, never()).delete(any());
    }

    @Test
    void testBtnEliminarNombreCambiado() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();

        TipoProducto tipo = crearTipoProducto(1L, "Modificado", null);
        TipoProducto original = crearTipoProducto(1L, "Original", null);
        frm.setFilaSeleccionada(tipo);
        when(tipoProductoDAO.finById(1L)).thenReturn(original);

        frm.btnEliminar();

        verify(tipoProductoDAO, never()).delete(any());
    }

    @Test
    void testBtnEliminarConException() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();

        TipoProducto tipo = crearTipoProducto(1L, "Test", null);
        TipoProducto original = crearTipoProducto(1L, "Test", null);
        frm.setFilaSeleccionada(tipo);
        when(tipoProductoDAO.finById(1L)).thenReturn(original);
        doThrow(new RuntimeException("Error")).when(tipoProductoDAO).delete(any());

        frm.btnEliminar();
    }

    // ========== Tests para limpiarSeleccionado ==========
    @Test
    void testLimpiarSeleccionado() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();

        frm.setFilaSeleccionada(crearTipoProducto(1L, "Test", null));
        frm.setSelectedNode(new DefaultTreeNode<>());
        frm.setEstado(CRUD.MODIFICAR);

        frm.limpiarSeleccionado();

        assertNull(frm.getSelectedNode());
        assertEquals(CRUD.NINGUNO, frm.getEstado());
    }

    // ========== Tests para getFilaSeleccionada ==========
    @Test
    void testGetFilaSeleccionadaNull() {
        frm.setFilaSeleccionada(null);
        TipoProducto resultado = frm.getFilaSeleccionada();
        assertNotNull(resultado);
        assertTrue(resultado.getActivo());
    }

    // ========== Tests para getters y setters ==========
    @Test
    void testGettersYSetters() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();

        // RootNode
        TreeNode<TipoProducto> root = new DefaultTreeNode<>();
        frm.setRootNode(root);
        assertEquals(root, frm.getRootNode());

        // SelectedNode
        TreeNode<TipoProducto> selected = new DefaultTreeNode<>();
        frm.setSelectedNode(selected);
        assertEquals(selected, frm.getSelectedNode());

        // FilaSeleccionada
        TipoProducto tipo = crearTipoProducto(1L, "Test", null);
        frm.setFilaSeleccionada(tipo);
        assertEquals(tipo, frm.getFilaSeleccionada());

        // Estado
        frm.setEstado(CRUD.CREAR);
        assertEquals(CRUD.CREAR, frm.getEstado());

        // TipoProductoDAO
        assertEquals(tipoProductoDAO, frm.getTipoProductoDAO());
        TipoProductoDAO nuevoDAO = mock(TipoProductoDAO.class);
        frm.setTipoProductoDAO(nuevoDAO);
        assertEquals(nuevoDAO, frm.getTipoProductoDAO());

        // CaracteristicaDAO
        assertEquals(caracteristicaDAO, frm.getCaracteristicaDAO());
        CaracteristicaDAO nuevoCarDAO = mock(CaracteristicaDAO.class);
        frm.setCaracteristicaDAO(nuevoCarDAO);
        assertEquals(nuevoCarDAO, frm.getCaracteristicaDAO());

        // CaracteristicasList
        List<TipoProductoCaracteristica> lista = new ArrayList<>();
        frm.setCaracteristicasList(lista);
        assertEquals(lista, frm.getCaracteristicasList());

        // TipoProductoList
        List<TipoProducto> listaTp = new ArrayList<>();
        frm.setTipoProductoList(listaTp);
    }

    // ========== Tests para mostrarError (vía btnAgregar/btnActualizar/btnEliminar) ==========
    @Test
    void testMostrarErrorConClaveI18n() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();

        TipoProducto tipo = new TipoProducto();
        tipo.setNombre(null);
        frm.setFilaSeleccionada(tipo);

        frm.btnAgregar();

        // Verifica que se llama con clave i18n (sin espacio, con punto)
        messageHelperMock.verify(() -> MessageHelper.addErrorMessage(eq("mensaje.titulo.error"), eq("validacion.nombre.requerido")));
    }

    @Test
    void testMostrarErrorConTextoNormal() throws Exception {
        when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());
        frm.init();

        TipoProducto tipo = crearTipoProducto(null, "Nuevo", null);
        frm.setFilaSeleccionada(tipo);
        doThrow(new RuntimeException("Error de base de datos")).when(tipoProductoDAO).crear(any());

        frm.btnAgregar();

        messageHelperMock.verify(() -> MessageHelper.addErrorMessage(anyString(), anyString(), anyString()));
    }
}
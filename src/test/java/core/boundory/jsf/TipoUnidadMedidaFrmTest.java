package core.boundory.jsf;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.primefaces.PrimeFaces;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.CRUD;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.MessageHelper;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.TipoUnidadMedidaFrm;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoUnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.UnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoUnidadMedida;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.UnidadMedida;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TipoUnidadMedidaFrmTest {

    @Mock
    private TipoUnidadMedidaDAO tipoUnidadMedidaDAO;
    @Mock
    private UnidadMedidaDAO unidadMedidaDAO;
    @Mock
    private PrimeFaces primeFaces;

    private TipoUnidadMedidaFrm frm;
    private MockedStatic<MessageHelper> messageHelperMock;
    private MockedStatic<PrimeFaces> primeFacesMock;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        messageHelperMock = mockStatic(MessageHelper.class);
        primeFacesMock = mockStatic(PrimeFaces.class);
        primeFacesMock.when(PrimeFaces::current).thenReturn(primeFaces);

        frm = new TipoUnidadMedidaFrm();
        inyectarCampo("tipoUnidadMedidaDAO", tipoUnidadMedidaDAO);
        inyectarCampo("unidadMedidaDAO", unidadMedidaDAO);
    }

    @AfterEach
    void tearDown() {
        if (messageHelperMock != null) messageHelperMock.close();
        if (primeFacesMock != null) primeFacesMock.close();
    }

    private void inyectarCampo(String nombre, Object valor) throws Exception {
        Field field = TipoUnidadMedidaFrm.class.getDeclaredField(nombre);
        field.setAccessible(true);
        field.set(frm, valor);
    }

    private TipoUnidadMedida crearTipoUnidadMedida(Integer id, String nombre) {
        TipoUnidadMedida tum = new TipoUnidadMedida();
        tum.setId(id);
        tum.setNombre(nombre);
        tum.setActivo(true);
        return tum;
    }

    private UnidadMedida crearUnidadMedida(Integer id) {
        UnidadMedida um = new UnidadMedida();
        um.setId(id);
        um.setEquivalencia(BigDecimal.ONE);
        um.setActivo(true);
        return um;
    }

    // ========== Tests para obtenerDAO ==========
    @Test
    void testObtenerDAO() {
        assertEquals(tipoUnidadMedidaDAO, frm.getTipoUnidadMedidaDAO());
    }

    // ========== Tests para validarAntesDeCrear ==========
    @Test
    void testValidarAntesDeCrearExitoso() {
        TipoUnidadMedida entidad = crearTipoUnidadMedida(null, "Longitud");
        frm.setFilaSeleccionada(entidad);
        frm.btnAgregar();
        verify(tipoUnidadMedidaDAO).crear(any());
    }

    @Test
    void testValidarAntesDeCrearNombreNull() {
        TipoUnidadMedida entidad = new TipoUnidadMedida();
        entidad.setNombre(null);
        frm.setFilaSeleccionada(entidad);
        frm.btnAgregar();
        verify(tipoUnidadMedidaDAO, never()).crear(any());
    }

    @Test
    void testValidarAntesDeCrearNombreVacio() {
        TipoUnidadMedida entidad = new TipoUnidadMedida();
        entidad.setNombre("   ");
        frm.setFilaSeleccionada(entidad);
        frm.btnAgregar();
        verify(tipoUnidadMedidaDAO, never()).crear(any());
    }

    // ========== Tests para validarAntesDeActualizar ==========
    @Test
    void testValidarAntesDeActualizarExitoso() {
        TipoUnidadMedida entidad = crearTipoUnidadMedida(1, "Peso");
        frm.setFilaSeleccionada(entidad);
        when(tipoUnidadMedidaDAO.finById(1)).thenReturn(entidad);
        frm.btnActualizar();
        verify(tipoUnidadMedidaDAO).update(any());
    }

    @Test
    void testValidarAntesDeActualizarNombreNull() {
        TipoUnidadMedida entidad = new TipoUnidadMedida();
        entidad.setId(1);
        entidad.setNombre(null);
        frm.setFilaSeleccionada(entidad);
        when(tipoUnidadMedidaDAO.finById(1)).thenReturn(entidad);
        frm.btnActualizar();
        verify(tipoUnidadMedidaDAO, never()).update(any());
    }

    @Test
    void testValidarAntesDeActualizarNombreVacio() {
        TipoUnidadMedida entidad = new TipoUnidadMedida();
        entidad.setId(1);
        entidad.setNombre("");
        frm.setFilaSeleccionada(entidad);
        when(tipoUnidadMedidaDAO.finById(1)).thenReturn(entidad);
        frm.btnActualizar();
        verify(tipoUnidadMedidaDAO, never()).update(any());
    }

    // ========== Tests para validarAntesDeEliminar ==========
    @Test
    void testValidarAntesDeEliminarExitoso() {
        TipoUnidadMedida entidad = crearTipoUnidadMedida(1, "Volumen");
        frm.setFilaSeleccionada(entidad);
        when(tipoUnidadMedidaDAO.finById(1)).thenReturn(entidad);
        when(unidadMedidaDAO.getUnidadesPorTipoUnidadMedida(1)).thenReturn(new ArrayList<>());
        frm.btnEliminar();
        verify(tipoUnidadMedidaDAO).delete(any());
    }

    @Test
    void testValidarAntesDeEliminarConUnidadesAsociadas() {
        TipoUnidadMedida entidad = crearTipoUnidadMedida(1, "Tiempo");
        frm.setFilaSeleccionada(entidad);
        when(tipoUnidadMedidaDAO.finById(1)).thenReturn(entidad);
        when(unidadMedidaDAO.getUnidadesPorTipoUnidadMedida(1))
                .thenReturn(Arrays.asList(crearUnidadMedida(1)));
        frm.btnEliminar();
        verify(tipoUnidadMedidaDAO, never()).delete(any());
    }

    @Test
    void testValidarAntesDeEliminarConUnidadesNull() {
        TipoUnidadMedida entidad = crearTipoUnidadMedida(1, "Temperatura");
        frm.setFilaSeleccionada(entidad);
        when(tipoUnidadMedidaDAO.finById(1)).thenReturn(entidad);
        when(unidadMedidaDAO.getUnidadesPorTipoUnidadMedida(1)).thenReturn(null);
        frm.btnEliminar();
        verify(tipoUnidadMedidaDAO).delete(any());
    }

    // ========== Tests para instanciarEntidad ==========
    @Test
    void testInstanciarEntidad() {
        frm.btnNuevo();
        TipoUnidadMedida nueva = frm.getFilaSeleccionada();
        assertNotNull(nueva);
        assertTrue(nueva.getActivo());
        assertNull(nueva.getId());
    }

    // ========== Tests para btnNuevo ==========
    @Test
    void testBtnNuevo() {
        frm.setUnidadesDeMedida(Arrays.asList(crearUnidadMedida(1)));
        frm.setDetalleSeleccionado(crearUnidadMedida(2));
        frm.btnNuevo();
        assertEquals(CRUD.CREAR, frm.getEstado());
        assertTrue(frm.getUnidadesDeMedida().isEmpty());
    }

    // ========== Tests para isTipoNuevo ==========
    @Test
    void testIsTipoNuevoTrue() {
        frm.setEstado(CRUD.CREAR);
        assertTrue(frm.isTipoNuevo());
    }

    @Test
    void testIsTipoNuevoFalse() {
        frm.setEstado(CRUD.MODIFICAR);
        assertFalse(frm.isTipoNuevo());
    }

    @Test
    void testIsTipoNuevoNinguno() {
        frm.setEstado(CRUD.NINGUNO);
        assertFalse(frm.isTipoNuevo());
    }

    // ========== Tests para cargarDetalles ==========
    @Test
    void testCargarDetallesConId() {
        TipoUnidadMedida tipo = crearTipoUnidadMedida(1, "Longitud");
        frm.setFilaSeleccionada(tipo);
        List<UnidadMedida> unidades = Arrays.asList(crearUnidadMedida(1), crearUnidadMedida(2));
        when(unidadMedidaDAO.getUnidadesPorTipoUnidadMedida(1)).thenReturn(unidades);
        frm.cargarDetalles();
        assertEquals(2, frm.getUnidadesDeMedida().size());
    }

    @Test
    void testCargarDetallesSinId() {
        TipoUnidadMedida tipo = new TipoUnidadMedida();
        tipo.setId(null);
        frm.setFilaSeleccionada(tipo);
        frm.cargarDetalles();
        assertTrue(frm.getUnidadesDeMedida().isEmpty());
    }

    @Test
    void testCargarDetallesFilaNull() {
        frm.setFilaSeleccionada(null);
        frm.cargarDetalles();
        assertTrue(frm.getUnidadesDeMedida().isEmpty());
    }

    @Test
    void testCargarDetallesConException() {
        TipoUnidadMedida tipo = crearTipoUnidadMedida(1, "Test");
        frm.setFilaSeleccionada(tipo);
        when(unidadMedidaDAO.getUnidadesPorTipoUnidadMedida(1)).thenThrow(new RuntimeException("Error"));
        frm.cargarDetalles();
        assertTrue(frm.getUnidadesDeMedida().isEmpty());
    }

    @Test
    void testCargarDetallesDAONull() throws Exception {
        TipoUnidadMedida tipo = crearTipoUnidadMedida(1, "Test");
        frm.setFilaSeleccionada(tipo);
        inyectarCampo("unidadMedidaDAO", null);
        frm.cargarDetalles();
        assertTrue(frm.getUnidadesDeMedida().isEmpty());
    }

    // ========== Tests para prepararNuevoDetalle ==========
    @Test
    void testPrepararNuevoDetalle() {
        TipoUnidadMedida tipo = crearTipoUnidadMedida(1, "Longitud");
        frm.setFilaSeleccionada(tipo);
        frm.prepararNuevoDetalle();
        UnidadMedida detalle = frm.getDetalleSeleccionado();
        assertNotNull(detalle);
        assertEquals(tipo, detalle.getIdTipoUnidadMedida());
        assertTrue(detalle.getActivo());
    }

    // ========== Tests para guardarDetalle ==========
    @Test
    void testGuardarDetalleCrear() {
        TipoUnidadMedida tipo = crearTipoUnidadMedida(1, "Longitud");
        frm.setFilaSeleccionada(tipo);
        UnidadMedida detalle = crearUnidadMedida(null);
        frm.setDetalleSeleccionado(detalle);
        when(unidadMedidaDAO.getUnidadesPorTipoUnidadMedida(1)).thenReturn(new ArrayList<>());
        frm.guardarDetalle();
        verify(unidadMedidaDAO).crear(detalle);
        verify(primeFaces).executeScript("PF('dlgUnidadMedida').hide()");
    }

    @Test
    void testGuardarDetalleActualizar() {
        TipoUnidadMedida tipo = crearTipoUnidadMedida(1, "Longitud");
        frm.setFilaSeleccionada(tipo);
        UnidadMedida detalle = crearUnidadMedida(1);
        frm.setDetalleSeleccionado(detalle);
        when(unidadMedidaDAO.getUnidadesPorTipoUnidadMedida(1)).thenReturn(new ArrayList<>());
        frm.guardarDetalle();
        verify(unidadMedidaDAO).update(detalle);
    }

    @Test
    void testGuardarDetalleConException() {
        TipoUnidadMedida tipo = crearTipoUnidadMedida(1, "Test");
        frm.setFilaSeleccionada(tipo);
        UnidadMedida detalle = crearUnidadMedida(null);
        frm.setDetalleSeleccionado(detalle);
        doThrow(new RuntimeException("Error")).when(unidadMedidaDAO).crear(any());
        frm.guardarDetalle();
        messageHelperMock.verify(() -> MessageHelper.addErrorMessage(anyString(), anyString(), anyString()), atLeastOnce());
    }

    @Test
    void testGuardarDetalleDAONull() throws Exception {
        TipoUnidadMedida tipo = crearTipoUnidadMedida(1, "Test");
        frm.setFilaSeleccionada(tipo);
        UnidadMedida detalle = crearUnidadMedida(null);
        frm.setDetalleSeleccionado(detalle);
        inyectarCampo("unidadMedidaDAO", null);
        frm.guardarDetalle();
    }

    // ========== Tests para eliminarDetalle ==========
    @Test
    void testEliminarDetalleExitoso() {
        TipoUnidadMedida tipo = crearTipoUnidadMedida(1, "Longitud");
        frm.setFilaSeleccionada(tipo);
        UnidadMedida detalle = crearUnidadMedida(1);
        when(unidadMedidaDAO.getUnidadesPorTipoUnidadMedida(1)).thenReturn(new ArrayList<>());
        frm.eliminarDetalle(detalle);
        verify(unidadMedidaDAO).delete(detalle);
    }

    @Test
    void testEliminarDetalleConException() {
        TipoUnidadMedida tipo = crearTipoUnidadMedida(1, "Test");
        frm.setFilaSeleccionada(tipo);
        UnidadMedida detalle = crearUnidadMedida(1);
        doThrow(new RuntimeException("Error")).when(unidadMedidaDAO).delete(any());
        frm.eliminarDetalle(detalle);
    }

    @Test
    void testEliminarDetalleDAONull() throws Exception {
        UnidadMedida detalle = crearUnidadMedida(1);
        inyectarCampo("unidadMedidaDAO", null);
        frm.eliminarDetalle(detalle);
    }

    // ========== Tests para prepararEditarDetalle ==========
    @Test
    void testPrepararEditarDetalle() {
        UnidadMedida detalle = crearUnidadMedida(1);
        frm.prepararEditarDetalle(detalle);
        assertEquals(detalle, frm.getDetalleSeleccionado());
    }

    // ========== Tests para getters y setters ==========
    @Test
    void testGettersYSetters() {
        // TipoUnidadMedidaDAO
        TipoUnidadMedidaDAO nuevoDAO = mock(TipoUnidadMedidaDAO.class);
        frm.setTipoUnidadMedidaDAO(nuevoDAO);
        assertEquals(nuevoDAO, frm.getTipoUnidadMedidaDAO());

        // UnidadMedidaDAO
        UnidadMedidaDAO nuevoUmDAO = mock(UnidadMedidaDAO.class);
        frm.setUnidadMedidaDAO(nuevoUmDAO);
        assertEquals(nuevoUmDAO, frm.getUnidadMedidaDAO());

        // UnidadesDeMedida
        List<UnidadMedida> lista = new ArrayList<>();
        frm.setUnidadesDeMedida(lista);
        assertEquals(lista, frm.getUnidadesDeMedida());

        // DetalleSeleccionado
        UnidadMedida um = crearUnidadMedida(1);
        frm.setDetalleSeleccionado(um);
        assertEquals(um, frm.getDetalleSeleccionado());
    }

    @Test
    void testGetDetalleSeleccionadoNull() {
        frm.setDetalleSeleccionado(null);
        UnidadMedida resultado = frm.getDetalleSeleccionado();
        assertNotNull(resultado);
    }
}
package core.boundory.jsf;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.MessageHelper;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.UnidadMedidaFrm;
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

class UnidadMedidaFrmTest {

    @Mock
    private UnidadMedidaDAO unidadMedidaDAO;
    @Mock
    private TipoUnidadMedidaDAO tipoUnidadMedidaDAO;

    private UnidadMedidaFrm frm;
    private MockedStatic<MessageHelper> messageHelperMock;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        messageHelperMock = mockStatic(MessageHelper.class);

        frm = new UnidadMedidaFrm();
        inyectarCampo("unidadMedidaDAO", unidadMedidaDAO);
        inyectarCampo("tipoUnidadMedidaDAO", tipoUnidadMedidaDAO);
    }

    @AfterEach
    void tearDown() {
        if (messageHelperMock != null) messageHelperMock.close();
    }

    private void inyectarCampo(String nombre, Object valor) throws Exception {
        Field field = UnidadMedidaFrm.class.getDeclaredField(nombre);
        field.setAccessible(true);
        field.set(frm, valor);
    }

    private UnidadMedida crearUnidadMedida(Integer id, BigDecimal equivalencia) {
        UnidadMedida um = new UnidadMedida();
        um.setId(id);
        um.setEquivalencia(equivalencia);
        um.setActivo(true);
        return um;
    }

    private TipoUnidadMedida crearTipoUnidadMedida(Integer id, String nombre, Boolean activo) {
        TipoUnidadMedida tum = new TipoUnidadMedida();
        tum.setId(id);
        tum.setNombre(nombre);
        tum.setActivo(activo);
        return tum;
    }

    // ========== Tests para obtenerDAO ==========
    @Test
    void testObtenerDAO() {
        assertEquals(unidadMedidaDAO, frm.getUnidadMedidaDAO());
    }

    // ========== Tests para validarAntesDeCrear ==========
    @Test
    void testValidarAntesDeCrearExitoso() {
        UnidadMedida entidad = crearUnidadMedida(null, BigDecimal.ONE);
        frm.setFilaSeleccionada(entidad);
        frm.btnAgregar();
        verify(unidadMedidaDAO).crear(any());
    }

    @Test
    void testValidarAntesDeCrearEquivalenciaNull() {
        UnidadMedida entidad = new UnidadMedida();
        entidad.setEquivalencia(null);
        frm.setFilaSeleccionada(entidad);
        frm.btnAgregar();
        verify(unidadMedidaDAO, never()).crear(any());
        messageHelperMock.verify(() -> MessageHelper.addErrorMessage(eq("mensaje.titulo.error"), eq("validacion.equivalencia.requerida")));
    }

    // ========== Tests para validarAntesDeActualizar ==========
    @Test
    void testValidarAntesDeActualizarExitoso() {
        UnidadMedida entidad = crearUnidadMedida(1, BigDecimal.TEN);
        frm.setFilaSeleccionada(entidad);
        when(unidadMedidaDAO.finById(1)).thenReturn(entidad);
        frm.btnActualizar();
        verify(unidadMedidaDAO).update(any());
    }

    @Test
    void testValidarAntesDeActualizarEquivalenciaNull() {
        UnidadMedida entidad = new UnidadMedida();
        entidad.setId(1);
        entidad.setEquivalencia(null);
        frm.setFilaSeleccionada(entidad);
        when(unidadMedidaDAO.finById(1)).thenReturn(entidad);
        frm.btnActualizar();
        verify(unidadMedidaDAO, never()).update(any());
    }

    // ========== Tests para buscarEntidades ==========
    @Test
    void testBuscarEntidadesConFiltro() {
        frm.setIdTipoUnidadMedida(1);
        List<UnidadMedida> lista = Arrays.asList(crearUnidadMedida(1, BigDecimal.ONE));
        when(unidadMedidaDAO.findByTipoUnidadMedida(1, 0, 10)).thenReturn(lista);
        frm.initLazyModel();
        List<?> result = frm.getLazyModel().load(0, 10, null, null);
        assertEquals(1, result.size());
        verify(unidadMedidaDAO).findByTipoUnidadMedida(1, 0, 10);
    }

    @Test
    void testBuscarEntidadesSinFiltro() {
        frm.setIdTipoUnidadMedida(null);
        List<UnidadMedida> lista = Arrays.asList(crearUnidadMedida(1, BigDecimal.ONE));
        when(unidadMedidaDAO.findRange(0, 10)).thenReturn(lista);
        frm.initLazyModel();
        List<?> result = frm.getLazyModel().load(0, 10, null, null);
        assertEquals(1, result.size());
        verify(unidadMedidaDAO).findRange(0, 10);
    }

    @Test
    void testBuscarEntidadesConException() {
        frm.setIdTipoUnidadMedida(1);
        when(unidadMedidaDAO.findByTipoUnidadMedida(anyInt(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Error"));
        frm.initLazyModel();
        List<?> result = frm.getLazyModel().load(0, 10, null, null);
        assertTrue(result.isEmpty());
    }

    // ========== Tests para contarEntidades ==========
    @Test
    void testContarEntidadesConFiltro() {
        frm.setIdTipoUnidadMedida(1);
        when(unidadMedidaDAO.countByTipoUnidadMedida(1)).thenReturn(5L);
        frm.initLazyModel();
        int count = frm.getLazyModel().count(null);
        assertEquals(5, count);
        verify(unidadMedidaDAO).countByTipoUnidadMedida(1);
    }

    @Test
    void testContarEntidadesSinFiltro() {
        frm.setIdTipoUnidadMedida(null);
        when(unidadMedidaDAO.count()).thenReturn(10L);
        frm.initLazyModel();
        int count = frm.getLazyModel().count(null);
        assertEquals(10, count);
        verify(unidadMedidaDAO).count();
    }

    @Test
    void testContarEntidadesConException() {
        frm.setIdTipoUnidadMedida(1);
        when(unidadMedidaDAO.countByTipoUnidadMedida(anyInt()))
                .thenThrow(new RuntimeException("Error"));
        frm.initLazyModel();
        int count = frm.getLazyModel().count(null);
        assertEquals(0, count);
    }

    // ========== Tests para instanciarEntidad ==========
    @Test
    void testInstanciarEntidadSinFiltro() {
        frm.setIdTipoUnidadMedida(null);
        frm.btnNuevo();
        UnidadMedida nueva = frm.getFilaSeleccionada();
        assertNotNull(nueva);
        assertTrue(nueva.getActivo());
        assertNull(nueva.getIdTipoUnidadMedida());
    }

    @Test
    void testInstanciarEntidadConFiltro() {
        frm.setIdTipoUnidadMedida(5);
        frm.btnNuevo();
        UnidadMedida nueva = frm.getFilaSeleccionada();
        assertNotNull(nueva);
        assertTrue(nueva.getActivo());
        assertNotNull(nueva.getIdTipoUnidadMedida());
        assertEquals(5, nueva.getIdTipoUnidadMedida().getId());
    }

    // ========== Tests para getTipoUnidadMedidaList ==========
    @Test
    void testGetTipoUnidadMedidaListPrimeraVez() {
        TipoUnidadMedida activo1 = crearTipoUnidadMedida(1, "Longitud", true);
        TipoUnidadMedida activo2 = crearTipoUnidadMedida(2, "Peso", true);
        TipoUnidadMedida inactivo = crearTipoUnidadMedida(3, "Inactivo", false);
        TipoUnidadMedida nullActivo = crearTipoUnidadMedida(4, "NullActivo", null);

        when(tipoUnidadMedidaDAO.findRange(0, 1000))
                .thenReturn(Arrays.asList(activo1, activo2, inactivo, nullActivo));

        List<TipoUnidadMedida> resultado = frm.getTipoUnidadMedidaList();

        assertEquals(2, resultado.size());
        verify(tipoUnidadMedidaDAO).findRange(0, 1000);
    }

    @Test
    void testGetTipoUnidadMedidaListCacheada() {
        TipoUnidadMedida activo = crearTipoUnidadMedida(1, "Test", true);
        when(tipoUnidadMedidaDAO.findRange(0, 1000)).thenReturn(Arrays.asList(activo));

        frm.getTipoUnidadMedidaList();
        frm.getTipoUnidadMedidaList();

        verify(tipoUnidadMedidaDAO, times(1)).findRange(0, 1000);
    }

    @Test
    void testGetTipoUnidadMedidaListConException() {
        when(tipoUnidadMedidaDAO.findRange(0, 1000)).thenThrow(new RuntimeException("Error"));

        List<TipoUnidadMedida> resultado = frm.getTipoUnidadMedidaList();

        assertTrue(resultado.isEmpty());
    }

    // ========== Tests para setIdTipoUnidadMedida ==========
    @Test
    void testSetIdTipoUnidadMedidaConValor() {
        when(unidadMedidaDAO.countByTipoUnidadMedida(5)).thenReturn(0L);
        when(unidadMedidaDAO.findByTipoUnidadMedida(5, 0, 10)).thenReturn(new ArrayList<>());

        frm.setIdTipoUnidadMedida(5);

        assertEquals(5, frm.getIdTipoUnidadMedida());
        assertNotNull(frm.getLazyModel());
    }

    @Test
    void testSetIdTipoUnidadMedidaNull() {
        frm.setIdTipoUnidadMedida(null);
        assertNull(frm.getIdTipoUnidadMedida());
    }

    // ========== Tests para getters y setters ==========
    @Test
    void testGettersYSetters() {
        // UnidadMedidaDAO
        UnidadMedidaDAO nuevoDAO = mock(UnidadMedidaDAO.class);
        frm.setUnidadMedidaDAO(nuevoDAO);
        assertEquals(nuevoDAO, frm.getUnidadMedidaDAO());

        // TipoUnidadMedidaDAO
        TipoUnidadMedidaDAO nuevoTipoDAO = mock(TipoUnidadMedidaDAO.class);
        frm.setTipoUnidadMedidaDAO(nuevoTipoDAO);
        assertEquals(nuevoTipoDAO, frm.getTipoUnidadMedidaDAO());

        // IdTipoUnidadMedida
        frm.setIdTipoUnidadMedida(10);
        assertEquals(10, frm.getIdTipoUnidadMedida());
    }

    // ========== Tests adicionales para cobertura completa ==========
    @Test
    void testBuscarEntidadesRetornaNull() {
        frm.setIdTipoUnidadMedida(null);
        when(unidadMedidaDAO.findRange(anyInt(), anyInt())).thenReturn(null);
        frm.initLazyModel();
        List<?> result = frm.getLazyModel().load(0, 10, null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testValidarCrearYActualizarConMismaValidacion() {
        // Probar que ambas validaciones usan la misma lógica
        UnidadMedida sinEquivalencia = new UnidadMedida();
        sinEquivalencia.setEquivalencia(null);

        frm.setFilaSeleccionada(sinEquivalencia);
        frm.btnAgregar();
        verify(unidadMedidaDAO, never()).crear(any());

        sinEquivalencia.setId(1);
        when(unidadMedidaDAO.finById(1)).thenReturn(sinEquivalencia);
        frm.btnActualizar();
        verify(unidadMedidaDAO, never()).update(any());
    }
}
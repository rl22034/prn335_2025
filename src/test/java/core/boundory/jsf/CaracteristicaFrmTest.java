package core.boundory.jsf;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.CRUD;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.CaracteristicaFrm;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.MessageHelper;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoUnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoUnidadMedida;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests para CaracteristicaFrm - Gestión de Características")
class CaracteristicaFrmTest {

    private CaracteristicaFrm frm;
    private CaracteristicaDAO caracteristicaDAO;
    private TipoUnidadMedidaDAO tipoUnidadMedidaDAO;
    private MockedStatic<MessageHelper> messageHelperMock;

    @BeforeEach
    void setUp() {
        frm = new CaracteristicaFrm();
        caracteristicaDAO = mock(CaracteristicaDAO.class);
        tipoUnidadMedidaDAO = mock(TipoUnidadMedidaDAO.class);

        inyectarCampo(frm, "caracteristicaDAO", caracteristicaDAO);
        inyectarCampo(frm, "tipoUnidadMedidaDAO", tipoUnidadMedidaDAO);

        messageHelperMock = mockStatic(MessageHelper.class);
    }

    @AfterEach
    void tearDown() {
        if (messageHelperMock != null) {
            messageHelperMock.close();
        }
    }

    private void inyectarCampo(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException e) {
            try {
                Field field = target.getClass().getSuperclass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
            } catch (Exception ex) {
                fail("Error inyectando campo " + fieldName + ": " + ex.getMessage());
            }
        } catch (Exception e) {
            fail("Error inyectando campo " + fieldName + ": " + e.getMessage());
        }
    }

    @Nested
    @DisplayName("Inicialización")
    class InicializacionTests {

        @Test
        @DisplayName("init() debe inicializar correctamente")
        void init_debeInicializar() {
            // When
            frm.init();

            // Then
            assertNotNull(frm.getFilaSeleccionada());
            assertEquals(CRUD.NINGUNO, frm.getEstado());
        }

        @Test
        @DisplayName("instanciarEntidad() debe crear característica activa")
        void instanciarEntidad_debeCrearCaracteristicaActiva() {
            // When
            frm.init();
            Caracteristica caracteristica = frm.getFilaSeleccionada();

            // Then
            assertNotNull(caracteristica);
            assertTrue(caracteristica.getActivo());
        }
    }

    @Nested
    @DisplayName("Botón Nuevo")
    class BotonNuevoTests {

        @Test
        @DisplayName("btnNuevo() debe crear característica activa")
        void btnNuevo_debeCrearCaracteristica() {
            // Given
            frm.init();

            // When
            frm.btnNuevo();

            // Then
            assertNotNull(frm.getFilaSeleccionada());
            assertTrue(frm.getFilaSeleccionada().getActivo());
            assertEquals(CRUD.CREAR, frm.getEstado());
        }
    }

    @Nested
    @DisplayName("Botón Agregar - Crear Característica")
    class BotonAgregarTests {

        @BeforeEach
        void setUp() {
            frm.init();
        }

        @Test
        @DisplayName("btnAgregar() sin TipoUnidadMedida debe mostrar error")
        void btnAgregar_sinTipoUnidadMedida_debeMostrarError() {
            // Given
            Caracteristica caracteristica = new Caracteristica();
            caracteristica.setNombre("Color");
            caracteristica.setIdTipoUnidadMedida(null);
            frm.setFilaSeleccionada(caracteristica);
            frm.setEstado(CRUD.CREAR);

            // When
            frm.btnAgregar();

            // Then
            messageHelperMock.verify(() ->
                            MessageHelper.addErrorMessage(
                                    eq("mensaje.titulo.error"),
                                    anyString()
                            ),
                    atLeastOnce()
            );
        }

        @Test
        @DisplayName("btnAgregar() sin nombre debe mostrar error")
        void btnAgregar_sinNombre_debeMostrarError() {
            // Given
            TipoUnidadMedida tipo = new TipoUnidadMedida();
            tipo.setId(1);

            Caracteristica caracteristica = new Caracteristica();
            caracteristica.setNombre(null);
            caracteristica.setIdTipoUnidadMedida(tipo);
            frm.setFilaSeleccionada(caracteristica);
            frm.setEstado(CRUD.CREAR);

            // When
            frm.btnAgregar();

            // Then
            messageHelperMock.verify(() ->
                            MessageHelper.addErrorMessage(
                                    eq("mensaje.titulo.error"),
                                    anyString()
                            ),
                    atLeastOnce()
            );
        }

        @Test
        @DisplayName("btnAgregar() con nombre vacío debe mostrar error")
        void btnAgregar_conNombreVacio_debeMostrarError() {
            // Given
            TipoUnidadMedida tipo = new TipoUnidadMedida();
            tipo.setId(1);

            Caracteristica caracteristica = new Caracteristica();
            caracteristica.setNombre("   ");
            caracteristica.setIdTipoUnidadMedida(tipo);
            frm.setFilaSeleccionada(caracteristica);
            frm.setEstado(CRUD.CREAR);

            // When
            frm.btnAgregar();

            // Then
            messageHelperMock.verify(() ->
                            MessageHelper.addErrorMessage(
                                    eq("mensaje.titulo.error"),
                                    anyString()
                            ),
                    atLeastOnce()
            );
        }

        @Test
        @DisplayName("btnAgregar() con datos válidos debe crear")
        void btnAgregar_conDatosValidos_debeCrear() throws Exception {
            // Given
            TipoUnidadMedida tipo = new TipoUnidadMedida();
            tipo.setId(1);

            Caracteristica caracteristica = new Caracteristica();
            caracteristica.setNombre("Peso");
            caracteristica.setIdTipoUnidadMedida(tipo);
            caracteristica.setActivo(true);
            frm.setFilaSeleccionada(caracteristica);
            frm.setEstado(CRUD.CREAR);

            // When
            frm.btnAgregar();

            // Then
            verify(caracteristicaDAO).crear(caracteristica);
            messageHelperMock.verify(() ->
                    MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.crear.exito")
            );
        }
    }

    @Nested
    @DisplayName("Botón Actualizar - Modificar Característica")
    class BotonActualizarTests {

        @BeforeEach
        void setUp() {
            frm.init();
        }

        @Test
        @DisplayName("btnActualizar() sin TipoUnidadMedida debe mostrar error")
        void btnActualizar_sinTipoUnidadMedida_debeMostrarError() {
            // Given
            Caracteristica caracteristica = new Caracteristica();
            caracteristica.setId(1);
            caracteristica.setNombre("Test");
            caracteristica.setIdTipoUnidadMedida(null);
            frm.setFilaSeleccionada(caracteristica);
            frm.setEstado(CRUD.MODIFICAR);

            // When
            frm.btnActualizar();

            // Then
            messageHelperMock.verify(() ->
                            MessageHelper.addErrorMessage(
                                    eq("mensaje.titulo.error"),
                                    anyString()
                            ),
                    atLeastOnce()
            );
        }

        @Test
        @DisplayName("btnActualizar() sin nombre debe mostrar error")
        void btnActualizar_sinNombre_debeMostrarError() {
            // Given
            TipoUnidadMedida tipo = new TipoUnidadMedida();
            tipo.setId(1);

            Caracteristica caracteristica = new Caracteristica();
            caracteristica.setId(1);
            caracteristica.setNombre(null);
            caracteristica.setIdTipoUnidadMedida(tipo);
            frm.setFilaSeleccionada(caracteristica);
            frm.setEstado(CRUD.MODIFICAR);

            // When
            frm.btnActualizar();

            // Then
            messageHelperMock.verify(() ->
                            MessageHelper.addErrorMessage(
                                    eq("mensaje.titulo.error"),
                                    anyString()
                            ),
                    atLeastOnce()
            );
        }

        @Test
        @DisplayName("btnActualizar() con datos válidos debe actualizar")
        void btnActualizar_conDatosValidos_debeActualizar() throws Exception {
            // Given
            TipoUnidadMedida tipo = new TipoUnidadMedida();
            tipo.setId(2);

            Caracteristica caracteristica = new Caracteristica();
            caracteristica.setId(1);
            caracteristica.setNombre("Dimensión");
            caracteristica.setIdTipoUnidadMedida(tipo);
            caracteristica.setActivo(true);
            frm.setFilaSeleccionada(caracteristica);
            frm.setEstado(CRUD.MODIFICAR);

            // Mock finById para validación en DefaultFrm.actualizarEntidad()
            when(caracteristicaDAO.finById(1)).thenReturn(caracteristica);

            // When
            frm.btnActualizar();

            // Then
            verify(caracteristicaDAO).update(caracteristica);
            messageHelperMock.verify(() ->
                    MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.actualizar.exito")
            );
        }
    }

    @Nested
    @DisplayName("Botón Eliminar - Validaciones Especiales")
    class BotonEliminarTests {

        @BeforeEach
        void setUp() {
            frm.init();
        }

        @Test
        @DisplayName("btnEliminar() cuando el registro no existe debe mostrar error")
        void btnEliminar_registroNoExiste_debeMostrarError() throws Exception {
            // Given
            Integer id = 1;
            TipoUnidadMedida tipo = new TipoUnidadMedida();
            tipo.setId(1);

            Caracteristica caracteristica = new Caracteristica();
            caracteristica.setId(id);
            caracteristica.setNombre("Test");
            caracteristica.setIdTipoUnidadMedida(tipo);
            frm.setFilaSeleccionada(caracteristica);
            frm.setEstado(CRUD.MODIFICAR);

            when(caracteristicaDAO.finById(id)).thenReturn(null);

            // When
            frm.btnEliminar();

            // Then
            messageHelperMock.verify(() ->
                            MessageHelper.addErrorMessage(
                                    eq("mensaje.titulo.error"),
                                    anyString()
                            ),
                    atLeastOnce()
            );
            verify(caracteristicaDAO, never()).delete(any());
        }

        @Test
        @DisplayName("btnEliminar() cuando el nombre cambió debe mostrar error")
        void btnEliminar_nombreCambiado_debeMostrarError() throws Exception {
            // Given
            Integer id = 1;
            TipoUnidadMedida tipo = new TipoUnidadMedida();
            tipo.setId(1);

            Caracteristica caracteristicaSeleccionada = new Caracteristica();
            caracteristicaSeleccionada.setId(id);
            caracteristicaSeleccionada.setNombre("Nombre Modificado");
            caracteristicaSeleccionada.setIdTipoUnidadMedida(tipo);

            Caracteristica caracteristicaOriginal = new Caracteristica();
            caracteristicaOriginal.setId(id);
            caracteristicaOriginal.setNombre("Nombre Original");
            caracteristicaOriginal.setIdTipoUnidadMedida(tipo);

            frm.setFilaSeleccionada(caracteristicaSeleccionada);
            frm.setEstado(CRUD.MODIFICAR);

            when(caracteristicaDAO.finById(id)).thenReturn(caracteristicaOriginal);

            // When
            frm.btnEliminar();

            // Then
            messageHelperMock.verify(() ->
                            MessageHelper.addErrorMessage(
                                    eq("mensaje.titulo.error"),
                                    anyString()
                            ),
                    atLeastOnce()
            );
            verify(caracteristicaDAO, never()).delete(any());
        }

        @Test
        @DisplayName("btnEliminar() con validaciones correctas debe eliminar")
        void btnEliminar_validacionesCorrectas_debeEliminar() throws Exception {
            // Given
            Integer id = 1;
            TipoUnidadMedida tipo = new TipoUnidadMedida();
            tipo.setId(1);

            Caracteristica caracteristica = new Caracteristica();
            caracteristica.setId(id);
            caracteristica.setNombre("Color");
            caracteristica.setIdTipoUnidadMedida(tipo);

            Caracteristica caracteristicaOriginal = new Caracteristica();
            caracteristicaOriginal.setId(id);
            caracteristicaOriginal.setNombre("Color");
            caracteristicaOriginal.setIdTipoUnidadMedida(tipo);

            frm.setFilaSeleccionada(caracteristica);
            frm.setEstado(CRUD.MODIFICAR);

            when(caracteristicaDAO.finById(id)).thenReturn(caracteristicaOriginal);

            // When
            frm.btnEliminar();

            // Then
            verify(caracteristicaDAO).finById(id);
            verify(caracteristicaDAO).delete(caracteristica);
            messageHelperMock.verify(() ->
                    MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.eliminar.exito")
            );
        }
    }

    @Nested
    @DisplayName("Método getTipoUnidadMedidaList")
    class GetTipoUnidadMedidaListTests {

        @Test
        @DisplayName("getTipoUnidadMedidaList() debe cargar lista")
        void getTipoUnidadMedidaList_debeCargarLista() throws Exception {
            // Given
            List<TipoUnidadMedida> tipos = Arrays.asList(
                    crearTipoUnidadMedida("Kilogramos"),
                    crearTipoUnidadMedida("Metros")
            );
            when(tipoUnidadMedidaDAO.findRange(0, 1000)).thenReturn(tipos);

            // When
            List<TipoUnidadMedida> resultado = frm.getTipoUnidadMedidaList();

            // Then
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            verify(tipoUnidadMedidaDAO).findRange(0, 1000);
        }

        @Test
        @DisplayName("getTipoUnidadMedidaList() debe cachear lista")
        void getTipoUnidadMedidaList_debeCachearLista() throws Exception {
            // Given
            List<TipoUnidadMedida> tipos = Arrays.asList(
                    crearTipoUnidadMedida("Litros")
            );
            when(tipoUnidadMedidaDAO.findRange(0, 1000)).thenReturn(tipos);

            // When
            frm.getTipoUnidadMedidaList();
            frm.getTipoUnidadMedidaList();

            // Then
            verify(tipoUnidadMedidaDAO, times(1)).findRange(0, 1000);
        }

        @Test
        @DisplayName("getTipoUnidadMedidaList() con excepción debe manejar error")
        void getTipoUnidadMedidaList_conExcepcion_debeManejarError() throws Exception {
            // Given
            when(tipoUnidadMedidaDAO.findRange(0, 1000))
                    .thenThrow(new RuntimeException("Error"));

            // When
            List<TipoUnidadMedida> resultado = frm.getTipoUnidadMedidaList();

            // Then
            assertNull(resultado);
        }

        private TipoUnidadMedida crearTipoUnidadMedida(String nombre) {
            TipoUnidadMedida tipo = new TipoUnidadMedida();
            tipo.setId((int) (Math.random() * 100));
            tipo.setNombre(nombre);
            return tipo;
        }
    }

    @Nested
    @DisplayName("Búsqueda y Conteo")
    class BusquedaConteoTests {

        @Test
        @DisplayName("findRange() debe cargar características")
        void findRange_debeCargarCaracteristicas() throws Exception {
            // Given
            frm.init();
            List<Caracteristica> caracteristicas = Arrays.asList(
                    crearCaracteristica("Color"),
                    crearCaracteristica("Peso")
            );
            when(caracteristicaDAO.findRange(anyInt(), anyInt())).thenReturn(caracteristicas);
            when(caracteristicaDAO.count()).thenReturn(2L);

            // When
            frm.findRange();

            // Then
            assertNotNull(frm.getEntidadesList());
            verify(caracteristicaDAO, atLeastOnce()).findRange(anyInt(), anyInt());
        }

        private Caracteristica crearCaracteristica(String nombre) {
            Caracteristica caracteristica = new Caracteristica();
            caracteristica.setId((int) (Math.random() * 1000));
            caracteristica.setNombre(nombre);
            caracteristica.setActivo(true);
            return caracteristica;
        }
    }

    @Nested
    @DisplayName("Selección de Fila")
    class SeleccionFilaTests {

        @Test
        @DisplayName("onRowSelect() debe cambiar estado a MODIFICAR")
        void onRowSelect_debeCambiarEstado() {
            // Given
            frm.init();
            TipoUnidadMedida tipo = new TipoUnidadMedida();
            tipo.setId(1);

            Caracteristica caracteristica = new Caracteristica();
            caracteristica.setId(1);
            caracteristica.setNombre("Test");
            caracteristica.setIdTipoUnidadMedida(tipo);
            frm.setFilaSeleccionada(caracteristica);

            SelectEvent<Caracteristica> event = mock(SelectEvent.class);
            when(event.getObject()).thenReturn(caracteristica);

            // When
            frm.onRowSelect(event);

            // Then
            assertEquals(CRUD.MODIFICAR, frm.getEstado());
        }
    }

    @Nested
    @DisplayName("Getters y Setters")
    class GettersSettersTests {

        @Test
        @DisplayName("Debe get/set filaSeleccionada")
        void debeGetSetFilaSeleccionada() {
            // Given
            Caracteristica caracteristica = new Caracteristica();
            caracteristica.setNombre("Test");

            // When
            frm.setFilaSeleccionada(caracteristica);

            // Then
            assertEquals(caracteristica, frm.getFilaSeleccionada());
        }

        @Test
        @DisplayName("Debe get/set entidadesList")
        void debeGetSetEntidadesList() {
            // Given
            List<Caracteristica> caracteristicas = Arrays.asList(
                    new Caracteristica(),
                    new Caracteristica()
            );

            // When
            frm.setEntidadesList(caracteristicas);

            // Then
            assertEquals(caracteristicas, frm.getEntidadesList());
            assertEquals(2, frm.getEntidadesList().size());
        }

        @Test
        @DisplayName("Debe get/set caracteristicaDAO")
        void debeGetSetCaracteristicaDAO() {
            // Given
            CaracteristicaDAO nuevoDAO = mock(CaracteristicaDAO.class);

            // When
            frm.setCaracteristicaDAO(nuevoDAO);

            // Then
            assertEquals(nuevoDAO, frm.getCaracteristicaDAO());
        }

        @Test
        @DisplayName("Debe get/set tipoUnidadMedidaDAO")
        void debeGetSetTipoUnidadMedidaDAO() {
            // Given
            TipoUnidadMedidaDAO nuevoDAO = mock(TipoUnidadMedidaDAO.class);

            // When
            frm.setTipoUnidadMedidaDAO(nuevoDAO);

            // Then
            assertEquals(nuevoDAO, frm.getTipoUnidadMedidaDAO());
        }

        @Test
        @DisplayName("Debe get/set tipoUnidadMedidaList")
        void debeGetSetTipoUnidadMedidaList() {
            // Given
            List<TipoUnidadMedida> tipos = Arrays.asList(
                    new TipoUnidadMedida(),
                    new TipoUnidadMedida()
            );

            // When
            frm.setTipoUnidadMedidaList(tipos);

            // Then
            assertEquals(tipos, frm.getTipoUnidadMedidaList());
            assertEquals(2, frm.getTipoUnidadMedidaList().size());
        }
    }
}
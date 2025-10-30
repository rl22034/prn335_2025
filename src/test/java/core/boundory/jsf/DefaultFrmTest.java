package core.boundory.jsf;

import core.boundory.jsf.helpers.TestEntity;
import core.boundory.jsf.helpers.TestEntityFrm;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.CRUD;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.MessageHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests para DefaultFrm - Clase Base de Managed Beans")
class DefaultFrmTest {

    private TestEntityFrm frm;
    private MockedStatic<MessageHelper> messageHelperMock;

    @BeforeEach
    void setUp() {
        frm = new TestEntityFrm();
        messageHelperMock = mockStatic(MessageHelper.class);
    }

    @AfterEach
    void tearDown() {
        if (messageHelperMock != null) {
            messageHelperMock.close();
        }
        frm.limpiarDataStore();
    }

    @Nested
    @DisplayName("Inicialización y Configuración Inicial")
    class InicializacionTests {

        @Test
        @DisplayName("init() debe inicializar el estado en NINGUNO")
        void init_debeInicializarEstadoEnNinguno() {
            frm.init();
            assertEquals(CRUD.NINGUNO, frm.getEstado(),
                    "El estado inicial debe ser NINGUNO");
        }

        @Test
        @DisplayName("init() debe instanciar filaSeleccionada")
        void init_debeInstanciarFilaSeleccionada() {
            frm.init();
            assertNotNull(frm.getFilaSeleccionada(),
                    "filaSeleccionada no debe ser null después de init()");
        }

        @Test
        @DisplayName("init() debe inicializar el LazyDataModel")
        void init_debeInicializarLazyModel() {
            frm.init();
            assertNotNull(frm.getLazyModel(),
                    "LazyDataModel debe estar inicializado");
        }

        @Test
        @DisplayName("init() debe cargar la lista de entidades")
        void init_debeCargarListaDeEntidades() {
            frm.agregarEntidadesAlStore(Arrays.asList(
                    new TestEntity(1L, "Entidad 1"),
                    new TestEntity(2L, "Entidad 2"),
                    new TestEntity(3L, "Entidad 3")
            ));

            frm.init();

            assertNotNull(frm.getEntidadesList(),
                    "La lista de entidades no debe ser null");
        }

        @Test
        @DisplayName("findRange() con datos debe cargar entidades correctamente")
        void findRange_conDatos_debeCargarEntidades() {
            frm.agregarEntidadesAlStore(Arrays.asList(
                    new TestEntity(1L, "Test1"),
                    new TestEntity(2L, "Test2")
            ));

            frm.findRange();

            assertNotNull(frm.getEntidadesList());
        }

        @Test
        @DisplayName("findRange() con error debe manejar la excepción")
        void findRange_conError_debeCapturarExcepcion() {
            frm.setDebeArrojarExcepcion(true);

            assertDoesNotThrow(() -> frm.findRange(),
                    "findRange() debe capturar excepciones internamente");
        }
    }

    @Nested
    @DisplayName("Botón Nuevo - Preparar para Crear")
    class BotonNuevoTests {

        @BeforeEach
        void setUp() {
            frm.init();
        }

        @Test
        @DisplayName("btnNuevo() debe cambiar el estado a CREAR")
        void btnNuevo_debeCambiarEstadoACrear() {
            frm.btnNuevo();
            assertEquals(CRUD.CREAR, frm.getEstado(),
                    "El estado debe cambiar a CREAR");
        }

        @Test
        @DisplayName("btnNuevo() debe instanciar una nueva entidad limpia")
        void btnNuevo_debeInstanciarNuevaEntidad() {
            TestEntity entidadAnterior = new TestEntity(1L, "Anterior");
            frm.setFilaSeleccionada(entidadAnterior);

            frm.btnNuevo();

            assertNotNull(frm.getFilaSeleccionada());
            assertNull(frm.getFilaSeleccionada().getId(),
                    "La nueva entidad debe tener ID null");
        }

        @Test
        @DisplayName("btnNuevo() no debe lanzar excepción")
        void btnNuevo_noDebeLanzarExcepcion() {
            assertDoesNotThrow(() -> frm.btnNuevo(),
                    "btnNuevo() nunca debe lanzar excepción");
        }
    }

    @Nested
    @DisplayName("Botón Agregar - Crear Nueva Entidad")
    class BotonAgregarTests {

        @BeforeEach
        void setUp() {
            frm.init();
        }

        @Test
        @DisplayName("btnAgregar() debe crear la entidad exitosamente")
        void btnAgregar_debeCrearEntidadExitosamente() {
            TestEntity nueva = new TestEntity(null, "Nueva Entidad");
            frm.setFilaSeleccionada(nueva);
            int cantidadInicial = frm.getDataStore().size();

            frm.btnAgregar();

            assertEquals(cantidadInicial + 1, frm.getDataStore().size(),
                    "Debe haber una entidad más en el store");
        }

        @Test
        @DisplayName("btnAgregar() debe mostrar mensaje de éxito")
        void btnAgregar_debeMostrarMensajeDeExito() {
            TestEntity nueva = new TestEntity(null, "Test");
            frm.setFilaSeleccionada(nueva);

            frm.btnAgregar();

            messageHelperMock.verify(() ->
                            MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.crear.exito"),
                    times(1)
            );
        }

        @Test
        @DisplayName("btnAgregar() debe cambiar el estado a NINGUNO después de crear")
        void btnAgregar_debeCambiarEstadoANinguno() {
            frm.setEstado(CRUD.CREAR);
            TestEntity nueva = new TestEntity(null, "Test");
            frm.setFilaSeleccionada(nueva);

            frm.btnAgregar();

            assertEquals(CRUD.NINGUNO, frm.getEstado(),
                    "El estado debe volver a NINGUNO después de crear");
        }

        @Test
        @DisplayName("btnAgregar() debe limpiar filaSeleccionada después de crear")
        void btnAgregar_debeLimpiarFilaSeleccionada() {
            TestEntity nueva = new TestEntity(null, "Test");
            frm.setFilaSeleccionada(nueva);

            frm.btnAgregar();

            assertNotNull(frm.getFilaSeleccionada());
            assertNull(frm.getFilaSeleccionada().getId(),
                    "filaSeleccionada debe ser una nueva instancia vacía");
        }

        @Test
        @DisplayName("btnAgregar() con error debe mostrar mensaje de error")
        void btnAgregar_conError_debeMostrarMensajeDeError() {
            frm.setDebeArrojarExcepcion(true);
            frm.setMensajeExcepcion("validacion.nombre.requerido");
            TestEntity nueva = new TestEntity(null, "Test");
            frm.setFilaSeleccionada(nueva);

            frm.btnAgregar();

            messageHelperMock.verify(() ->
                            MessageHelper.addErrorMessage(
                                    eq("mensaje.titulo.error"),
                                    anyString()
                            ),
                    atLeastOnce()
            );
        }
    }

    @Nested
    @DisplayName("Botón Eliminar - Eliminar Entidad")
    class BotonEliminarTests {

        @BeforeEach
        void setUp() {
            frm.init();
        }

        @Test
        @DisplayName("btnEliminar() debe eliminar la entidad exitosamente")
        void btnEliminar_debeEliminarEntidadExitosamente() {
            TestEntity entidad = new TestEntity(1L, "A Eliminar");
            frm.agregarEntidadesAlStore(Collections.singletonList(entidad));
            frm.setFilaSeleccionada(entidad);
            int cantidadInicial = frm.getDataStore().size();

            frm.btnEliminar();

            assertEquals(cantidadInicial - 1, frm.getDataStore().size(),
                    "Debe haber una entidad menos en el store");
        }

        @Test
        @DisplayName("btnEliminar() debe mostrar mensaje de éxito")
        void btnEliminar_debeMostrarMensajeDeExito() {
            TestEntity entidad = new TestEntity(1L, "Test");
            frm.agregarEntidadesAlStore(Collections.singletonList(entidad));
            frm.setFilaSeleccionada(entidad);

            frm.btnEliminar();

            messageHelperMock.verify(() ->
                            MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.eliminar.exito"),
                    times(1)
            );
        }

        @Test
        @DisplayName("btnEliminar() debe cambiar el estado a NINGUNO")
        void btnEliminar_debeCambiarEstadoANinguno() {
            frm.setEstado(CRUD.MODIFICAR);
            TestEntity entidad = new TestEntity(1L, "Test");
            frm.setFilaSeleccionada(entidad);

            frm.btnEliminar();

            assertEquals(CRUD.NINGUNO, frm.getEstado(),
                    "El estado debe volver a NINGUNO después de eliminar");
        }

        @Test
        @DisplayName("btnEliminar() con error debe mostrar mensaje de error")
        void btnEliminar_conError_debeMostrarMensajeDeError() {
            frm.setDebeArrojarExcepcion(true);
            TestEntity entidad = new TestEntity(1L, "Test");
            frm.setFilaSeleccionada(entidad);

            frm.btnEliminar();

            messageHelperMock.verify(() ->
                            MessageHelper.addErrorMessage(
                                    eq("mensaje.titulo.error"),
                                    anyString(),
                                    anyString()
                            ),
                    atLeastOnce()
            );
        }
    }

    @Nested
    @DisplayName("Botón Actualizar - Modificar Entidad")
    class BotonActualizarTests {

        @BeforeEach
        void setUp() {
            frm.init();
        }

        @Test
        @DisplayName("btnActualizar() debe actualizar la entidad exitosamente")
        void btnActualizar_debeActualizarEntidadExitosamente() {
            TestEntity entidad = new TestEntity(1L, "Original");
            frm.agregarEntidadesAlStore(Collections.singletonList(entidad));

            TestEntity modificada = new TestEntity(1L, "Modificada");
            frm.setFilaSeleccionada(modificada);

            frm.btnActualizar();

            messageHelperMock.verify(() ->
                            MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.actualizar.exito"),
                    times(1)
            );
        }

        @Test
        @DisplayName("btnActualizar() debe cambiar el estado a NINGUNO")
        void btnActualizar_debeCambiarEstadoANinguno() {
            frm.setEstado(CRUD.MODIFICAR);
            TestEntity entidad = new TestEntity(1L, "Test");
            frm.setFilaSeleccionada(entidad);

            frm.btnActualizar();

            assertEquals(CRUD.NINGUNO, frm.getEstado(),
                    "El estado debe volver a NINGUNO después de actualizar");
        }

        @Test
        @DisplayName("btnActualizar() con error debe mostrar mensaje de error")
        void btnActualizar_conError_debeMostrarMensajeDeError() {
            frm.setDebeArrojarExcepcion(true);
            frm.setMensajeExcepcion("Error al actualizar");
            TestEntity entidad = new TestEntity(1L, "Test");
            frm.setFilaSeleccionada(entidad);

            frm.btnActualizar();

            messageHelperMock.verify(() ->
                            MessageHelper.addErrorMessage(
                                    eq("mensaje.titulo.error"),
                                    anyString(),
                                    anyString()
                            ),
                    atLeastOnce()
            );
        }
    }

    @Nested
    @DisplayName("Selección de Fila en DataTable")
    class SeleccionFilaTests {

        @Test
        @DisplayName("onRowSelect() debe establecer la fila seleccionada")
        void onRowSelect_debeEstablecerFilaSeleccionada() {
            TestEntity entidad = new TestEntity(1L, "Seleccionada");
            SelectEvent<TestEntity> event = mock(SelectEvent.class);
            when(event.getObject()).thenReturn(entidad);

            frm.onRowSelect(event);

            assertEquals(entidad, frm.getFilaSeleccionada(),
                    "La fila seleccionada debe ser la del evento");
        }

        @Test
        @DisplayName("onRowSelect() debe cambiar el estado a MODIFICAR")
        void onRowSelect_debeCambiarEstadoAModificar() {
            frm.setEstado(CRUD.NINGUNO);
            TestEntity entidad = new TestEntity(1L, "Test");
            SelectEvent<TestEntity> event = mock(SelectEvent.class);
            when(event.getObject()).thenReturn(entidad);

            frm.onRowSelect(event);

            assertEquals(CRUD.MODIFICAR, frm.getEstado(),
                    "El estado debe cambiar a MODIFICAR al seleccionar una fila");
        }


        @Test
        @DisplayName("limpiarSeleccionado() debe instanciar nueva entidad vacía")
        void limpiarSeleccionado_debeInstanciarNuevaEntidad() {
            // Given - entidad con datos
            TestEntity entidadOriginal = new TestEntity(1L, "Test");
            frm.setFilaSeleccionada(entidadOriginal);

            // When - limpiar
            frm.limpiarSeleccionado();

            // Then - debe retornar una nueva instancia vacía (lazy initialization)
            TestEntity resultado = frm.getFilaSeleccionada();
            assertNotNull(resultado, "getFilaSeleccionada() nunca debe retornar null");
            assertNull(resultado.getId(), "La nueva instancia debe tener ID null");
            assertNotSame(entidadOriginal, resultado, "Debe ser una nueva instancia");
        }

        @Test
        @DisplayName("limpiarSeleccionado() debe cambiar el estado a NINGUNO")
        void limpiarSeleccionado_debeCambiarEstadoANinguno() {
            frm.setEstado(CRUD.MODIFICAR);

            frm.limpiarSeleccionado();

            assertEquals(CRUD.NINGUNO, frm.getEstado(),
                    "El estado debe volver a NINGUNO después de limpiar");
        }
    }

    @Nested
    @DisplayName("LazyDataModel - Paginación y Carga Diferida")
    class LazyDataModelTests {

        private LazyDataModel<TestEntity> lazyModel;

        @BeforeEach
        void setUp() {
            frm.agregarEntidadesAlStore(Arrays.asList(
                    new TestEntity(1L, "Entidad 1"),
                    new TestEntity(2L, "Entidad 2"),
                    new TestEntity(3L, "Entidad 3"),
                    new TestEntity(4L, "Entidad 4"),
                    new TestEntity(5L, "Entidad 5")
            ));
            frm.init();
            lazyModel = frm.getLazyModel();
        }

        @Test
        @DisplayName("count() debe retornar la cantidad correcta de entidades")
        void count_debeRetornarCantidadCorrecta() {
            int count = lazyModel.count(null);

            assertEquals(5, count,
                    "El count debe retornar el número total de entidades");
        }

        @Test
        @DisplayName("load() debe cargar las entidades correctamente")
        void load_debeCargarEntidadesCorrectamente() {
            List<TestEntity> resultado = lazyModel.load(0, 10, null, null);

            assertNotNull(resultado);
            assertEquals(5, resultado.size(),
                    "Debe cargar todas las 5 entidades disponibles");
        }

        @Test
        @DisplayName("getRowKey() debe retornar el ID como String")
        void getRowKey_debeRetornarIdComoString() {
            TestEntity entidad = new TestEntity(42L, "Test");

            String rowKey = lazyModel.getRowKey(entidad);

            assertEquals("42", rowKey,
                    "El rowKey debe ser el ID convertido a String");
        }

        @Test
        @DisplayName("getRowData() debe encontrar la entidad por rowKey")
        void getRowData_debeEncontrarEntidadPorRowKey() {
            List<TestEntity> datos = Arrays.asList(
                    new TestEntity(1L, "Test1"),
                    new TestEntity(2L, "Test2"),
                    new TestEntity(3L, "Test3")
            );
            lazyModel.setWrappedData(datos);

            TestEntity encontrada = lazyModel.getRowData("2");

            assertNotNull(encontrada);
            assertEquals(2L, encontrada.getId(),
                    "Debe encontrar la entidad con ID 2");
            assertEquals("Test2", encontrada.getNombre());
        }

    }
}
package core.boundory.jsf;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.AlmacenFrm;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.CRUD;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.MessageHelper;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.TipoAlmacenFrm;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.AlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoAlmacenDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Almacen;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoAlmacen;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests para AlmacenFrm - Gestión de Almacenes")
class AlmacenFrmTest {

    private AlmacenFrm frm;
    private AlmacenDAO almacenDAO;
    private TipoAlmacenDAO tipoAlmacenDAO;
    private TipoAlmacenFrm tipoAlmacenBean;
    private MockedStatic<MessageHelper> messageHelperMock;

    @BeforeEach
    void setUp() {
        frm = new AlmacenFrm();
        almacenDAO = mock(AlmacenDAO.class);
        tipoAlmacenDAO = mock(TipoAlmacenDAO.class);
        tipoAlmacenBean = mock(TipoAlmacenFrm.class);

        inyectarCampo(frm, "almacenDAO", almacenDAO);
        inyectarCampo(frm, "tipoAlmacenDAO", tipoAlmacenDAO);
        inyectarCampo(frm, "tipoAlmacenBean", tipoAlmacenBean);

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
        @DisplayName("instanciarEntidad() debe crear almacén activo")
        void instanciarEntidad_debeCrearAlmacenActivo() {
            // When
            frm.init();
            Almacen almacen = frm.getFilaSeleccionada();

            // Then
            assertNotNull(almacen);
            assertTrue(almacen.getActivo());
        }

        @Test
        @DisplayName("instanciarEntidad() debe asignar TipoAlmacen del bean padre")
        void instanciarEntidad_debeAsignarTipoAlmacen() {
            // Given
            TipoAlmacen tipo = new TipoAlmacen();
            tipo.setId(1);
            when(tipoAlmacenBean.getFilaSeleccionada()).thenReturn(tipo);

            // When
            frm.init();

            // Then
            assertNotNull(frm.getFilaSeleccionada().getIdTipoAlmacen());
            assertEquals(1, frm.getFilaSeleccionada().getIdTipoAlmacen().getId());
        }
    }

    @Nested
    @DisplayName("Botón Nuevo")
    class BotonNuevoTests {

        @Test
        @DisplayName("btnNuevo() debe crear almacén con TipoAlmacen")
        void btnNuevo_debeCrearAlmacenConTipo() {
            // Given
            frm.init();
            TipoAlmacen tipo = new TipoAlmacen();
            tipo.setId(5);
            when(tipoAlmacenBean.getFilaSeleccionada()).thenReturn(tipo);

            // When
            frm.btnNuevo();

            // Then
            assertNotNull(frm.getFilaSeleccionada());
            assertTrue(frm.getFilaSeleccionada().getActivo());
            assertEquals(CRUD.CREAR, frm.getEstado());
            assertEquals(5, frm.getFilaSeleccionada().getIdTipoAlmacen().getId());
        }
    }

    @Nested
    @DisplayName("Botón Agregar - Crear Almacén")
    class BotonAgregarTests {

        @BeforeEach
        void setUp() {
            frm.init();
        }

        @Test
        @DisplayName("btnAgregar() sin TipoAlmacen debe mostrar error")
        void btnAgregar_sinTipoAlmacen_debeMostrarError() {
            // Given
            Almacen almacen = new Almacen();
            almacen.setIdTipoAlmacen(null);
            almacen.setObservaciones("Test");
            frm.setFilaSeleccionada(almacen);
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
        @DisplayName("btnAgregar() con TipoAlmacen válido debe crear")
        void btnAgregar_conTipoAlmacenValido_debeCrear() throws Exception {
            // Given
            TipoAlmacen tipo = new TipoAlmacen();
            tipo.setId(1);

            Almacen almacen = new Almacen();
            almacen.setObservaciones("Almacén Central");
            almacen.setIdTipoAlmacen(tipo);
            almacen.setActivo(true);

            frm.setFilaSeleccionada(almacen);
            frm.setEstado(CRUD.CREAR);

            // When
            frm.btnAgregar();

            // Then
            verify(almacenDAO).crear(almacen);
            messageHelperMock.verify(() ->
                    MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.crear.exito")
            );
        }
    }

    @Nested
    @DisplayName("Botón Actualizar - Modificar Almacén")
    class BotonActualizarTests {

        @BeforeEach
        void setUp() {
            frm.init();
        }

        @Test
        @DisplayName("btnActualizar() sin TipoAlmacen debe mostrar error")
        void btnActualizar_sinTipoAlmacen_debeMostrarError() {
            // Given
            Almacen almacen = new Almacen();
            almacen.setId(1);
            almacen.setObservaciones("Test");
            almacen.setIdTipoAlmacen(null);
            frm.setFilaSeleccionada(almacen);
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
            TipoAlmacen tipo = new TipoAlmacen();
            tipo.setId(2);

            Almacen almacen = new Almacen();
            almacen.setId(1);
            almacen.setObservaciones("Almacén Actualizado");
            almacen.setIdTipoAlmacen(tipo);
            almacen.setActivo(true);

            frm.setFilaSeleccionada(almacen);
            frm.setEstado(CRUD.MODIFICAR);

            // When
            frm.btnActualizar();

            // Then
            verify(almacenDAO).update(almacen);
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
            TipoAlmacen tipo = new TipoAlmacen();
            tipo.setId(1);

            Almacen almacen = new Almacen();
            almacen.setId(id);
            almacen.setObservaciones("Test");
            almacen.setIdTipoAlmacen(tipo);

            frm.setFilaSeleccionada(almacen);
            frm.setEstado(CRUD.MODIFICAR);

            when(almacenDAO.finById(id)).thenReturn(null);

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
            verify(almacenDAO, never()).delete(any());
        }

        @Test
        @DisplayName("btnEliminar() cuando el TipoAlmacen cambió debe mostrar error")
        void btnEliminar_tipoAlmacenCambiado_debeMostrarError() throws Exception {
            // Given
            Integer id = 1;

            TipoAlmacen tipoSeleccionado = new TipoAlmacen();
            tipoSeleccionado.setId(1);

            TipoAlmacen tipoOriginal = new TipoAlmacen();
            tipoOriginal.setId(2);

            Almacen almacenSeleccionado = new Almacen();
            almacenSeleccionado.setId(id);
            almacenSeleccionado.setObservaciones("Test");
            almacenSeleccionado.setIdTipoAlmacen(tipoSeleccionado);

            Almacen almacenOriginal = new Almacen();
            almacenOriginal.setId(id);
            almacenOriginal.setObservaciones("Test");
            almacenOriginal.setIdTipoAlmacen(tipoOriginal);

            frm.setFilaSeleccionada(almacenSeleccionado);
            frm.setEstado(CRUD.MODIFICAR);

            when(almacenDAO.finById(id)).thenReturn(almacenOriginal);

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
            verify(almacenDAO, never()).delete(any());
        }

        @Test
        @DisplayName("btnEliminar() con validaciones correctas debe eliminar")
        void btnEliminar_validacionesCorrectas_debeEliminar() throws Exception {
            // Given
            Integer id = 1;

            TipoAlmacen tipo = new TipoAlmacen();
            tipo.setId(1);

            Almacen almacen = new Almacen();
            almacen.setId(id);
            almacen.setObservaciones("Almacén Test");
            almacen.setIdTipoAlmacen(tipo);

            Almacen almacenOriginal = new Almacen();
            almacenOriginal.setId(id);
            almacenOriginal.setObservaciones("Almacén Test");
            almacenOriginal.setIdTipoAlmacen(tipo);

            frm.setFilaSeleccionada(almacen);
            frm.setEstado(CRUD.MODIFICAR);

            when(almacenDAO.finById(id)).thenReturn(almacenOriginal);

            // When
            frm.btnEliminar();

            // Then
            verify(almacenDAO).finById(id);
            verify(almacenDAO).delete(almacen);
            messageHelperMock.verify(() ->
                    MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.eliminar.exito")
            );
        }
    }

    @Nested
    @DisplayName("Búsqueda y Conteo - Filtrado por TipoAlmacen")
    class BusquedaConteoTests {

        @Test
        @DisplayName("findRange() con TipoAlmacen seleccionado debe filtrar")
        void findRange_conTipoSeleccionado_debeFiltrar() throws Exception {
            // Given
            frm.init();

            TipoAlmacen tipo = new TipoAlmacen();
            tipo.setId(3);
            when(tipoAlmacenBean.getFilaSeleccionada()).thenReturn(tipo);

            List<Almacen> almacenes = Arrays.asList(
                    crearAlmacen("Almacén 1", tipo),
                    crearAlmacen("Almacén 2", tipo)
            );
            when(almacenDAO.findByTipoAlmacen(eq(3), anyInt(), anyInt())).thenReturn(almacenes);

            // When
            frm.findRange();

            // Then
            verify(almacenDAO, atLeastOnce()).findByTipoAlmacen(eq(3), anyInt(), anyInt());
        }

        @Test
        @DisplayName("findRange() sin TipoAlmacen seleccionado debe retornar lista vacía")
        void findRange_sinTipoSeleccionado_debeRetornarVacia() throws Exception {
            // Given
            frm.init();
            when(tipoAlmacenBean.getFilaSeleccionada()).thenReturn(null);

            // When
            frm.findRange();

            // Then
            verify(almacenDAO, never()).findByTipoAlmacen(anyInt(), anyInt(), anyInt());
        }

        private Almacen crearAlmacen(String observaciones, TipoAlmacen tipo) {
            Almacen almacen = new Almacen();
            almacen.setId((int) (Math.random() * 1000));
            almacen.setObservaciones(observaciones);
            almacen.setIdTipoAlmacen(tipo);
            almacen.setActivo(true);
            return almacen;
        }
    }
    @Nested
    @DisplayName("Método getTiposAlmacenActivos")
    class GetTiposAlmacenActivosTests {

        @Test
        @DisplayName("getTiposAlmacenActivos() debe retornar lista de tipos activos")
        void getTiposAlmacenActivos_debeRetornarLista() throws Exception {
            // Given
            List<TipoAlmacen> tipos = Arrays.asList(
                    crearTipoAlmacen("Tipo 1"),
                    crearTipoAlmacen("Tipo 2")
            );
            when(tipoAlmacenDAO.findTiposActivos()).thenReturn(tipos);

            // When
            List<TipoAlmacen> resultado = frm.getTiposAlmacenActivos();

            // Then
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            verify(tipoAlmacenDAO).findTiposActivos();
        }

        @Test
        @DisplayName("getTiposAlmacenActivos() con excepción debe retornar lista vacía")
        void getTiposAlmacenActivos_conExcepcion_debeRetornarVacia() throws Exception {
            // Given
            when(tipoAlmacenDAO.findTiposActivos()).thenThrow(new RuntimeException("Error"));

            // When
            List<TipoAlmacen> resultado = frm.getTiposAlmacenActivos();

            // Then
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }

        private TipoAlmacen crearTipoAlmacen(String nombre) {
            TipoAlmacen tipo = new TipoAlmacen();
            tipo.setId((int) (Math.random() * 100));
            tipo.setNombre(nombre);
            tipo.setActivo(true);
            return tipo;
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
            TipoAlmacen tipo = new TipoAlmacen();
            tipo.setId(1);

            Almacen almacen = new Almacen();
            almacen.setId(1);
            almacen.setObservaciones("Test");
            almacen.setIdTipoAlmacen(tipo);
            frm.setFilaSeleccionada(almacen);

            SelectEvent<Almacen> event = mock(SelectEvent.class);
            when(event.getObject()).thenReturn(almacen);

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
            TipoAlmacen tipo = new TipoAlmacen();
            tipo.setId(1);

            Almacen almacen = new Almacen();
            almacen.setObservaciones("Test");
            almacen.setIdTipoAlmacen(tipo);

            // When
            frm.setFilaSeleccionada(almacen);

            // Then
            assertEquals(almacen, frm.getFilaSeleccionada());
        }

        @Test
        @DisplayName("Debe get/set entidadesList")
        void debeGetSetEntidadesList() {
            // Given
            List<Almacen> almacenes = Arrays.asList(new Almacen(), new Almacen());

            // When
            frm.setEntidadesList(almacenes);

            // Then
            assertEquals(almacenes, frm.getEntidadesList());
            assertEquals(2, frm.getEntidadesList().size());
        }

        @Test
        @DisplayName("Debe get/set almacenDAO")
        void debeGetSetAlmacenDAO() {
            // Given
            AlmacenDAO nuevoDAO = mock(AlmacenDAO.class);

            // When
            frm.setAlmacenDAO(nuevoDAO);

            // Then
            assertEquals(nuevoDAO, frm.getAlmacenDAO());
        }

        @Test
        @DisplayName("Debe get/set tipoAlmacenDAO")
        void debeGetSetTipoAlmacenDAO() {
            // Given
            TipoAlmacenDAO nuevoDAO = mock(TipoAlmacenDAO.class);

            // When
            frm.setTipoAlmacenDAO(nuevoDAO);

            // Then
            assertEquals(nuevoDAO, frm.getTipoAlmacenDAO());
        }
    }
}

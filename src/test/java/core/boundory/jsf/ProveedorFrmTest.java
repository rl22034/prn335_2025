package core.boundory.jsf;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.CRUD;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.MessageHelper;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.ProveedorFrm;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProveedorDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Proveedor;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests para ProveedorFrm - Gestión de Proveedores")
class ProveedorFrmTest {

    private ProveedorFrm frm;
    private ProveedorDAO proveedorDAO;
    private MockedStatic<MessageHelper> messageHelperMock;

    @BeforeEach
    void setUp() {
        // Crear instancia real
        frm = new ProveedorFrm();

        // Crear mock
        proveedorDAO = mock(ProveedorDAO.class);

        // Inyectar mock usando reflexión
        inyectarCampo(frm, "proveedorDAO", proveedorDAO);

        // Mock estático
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
            assertNotNull(frm.getFilaSeleccionada(),
                    "filaSeleccionada debe estar inicializada");
            assertEquals(CRUD.NINGUNO, frm.getEstado(),
                    "Estado inicial debe ser NINGUNO");
        }

        @Test
        @DisplayName("instanciarEntidad() debe crear proveedor activo")
        void instanciarEntidad_debeCrearProveedorActivo() {
            // When
            frm.init();
            Proveedor proveedor = frm.getFilaSeleccionada();

            // Then
            assertNotNull(proveedor);
            assertTrue(proveedor.getActivo(),
                    "El proveedor nuevo debe estar activo");
        }
    }

    @Nested
    @DisplayName("Botón Nuevo")
    class BotonNuevoTests {

        @Test
        @DisplayName("btnNuevo() debe crear proveedor activo")
        void btnNuevo_debeCrearProveedor() {
            // Given
            frm.init();

            // When
            frm.btnNuevo();

            // Then
            assertNotNull(frm.getFilaSeleccionada(),
                    "Debe crear un proveedor nuevo");
            assertTrue(frm.getFilaSeleccionada().getActivo(),
                    "El proveedor debe estar activo");
            assertEquals(CRUD.CREAR, frm.getEstado(),
                    "El estado debe cambiar a CREAR");
        }

        @Test
        @DisplayName("btnNuevo() debe limpiar selección anterior")
        void btnNuevo_debeLimpiarSeleccionAnterior() {
            // Given
            frm.init();
            Proveedor anterior = new Proveedor();
            anterior.setId(100);
            anterior.setNombre("Proveedor Anterior");
            frm.setFilaSeleccionada(anterior);

            // When
            frm.btnNuevo();

            // Then
            assertNotEquals(anterior, frm.getFilaSeleccionada(),
                    "Debe crear una nueva instancia");
            assertNull(frm.getFilaSeleccionada().getId(),
                    "El nuevo proveedor no debe tener ID");
        }
    }

    @Nested
    @DisplayName("Botón Agregar - Crear Proveedor")
    class BotonAgregarTests {

        @BeforeEach
        void setUp() {
            frm.init();
        }

        @Test
        @DisplayName("btnAgregar() sin nombre debe mostrar error")
        void btnAgregar_sinNombre_debeMostrarError() {
            // Given
            Proveedor proveedor = new Proveedor();
            proveedor.setNombre(null);
            frm.setFilaSeleccionada(proveedor);
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
            Proveedor proveedor = new Proveedor();
            proveedor.setNombre("   ");
            frm.setFilaSeleccionada(proveedor);
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
        @DisplayName("btnAgregar() con nombre válido debe crear proveedor")
        void btnAgregar_conNombreValido_debeCrear() throws Exception {
            // Given
            Proveedor proveedor = new Proveedor();
            proveedor.setNombre("Distribuidora XYZ");
            proveedor.setActivo(true);
            frm.setFilaSeleccionada(proveedor);
            frm.setEstado(CRUD.CREAR);

            // When
            frm.btnAgregar();

            // Then
            verify(proveedorDAO).crear(proveedor);
            messageHelperMock.verify(() ->
                    MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.crear.exito")
            );
        }
    }

    @Nested
    @DisplayName("Botón Actualizar - Modificar Proveedor")
    class BotonActualizarTests {

        @BeforeEach
        void setUp() {
            frm.init();
        }

        @Test
        @DisplayName("btnActualizar() sin nombre debe mostrar error")
        void btnActualizar_sinNombre_debeMostrarError() {
            // Given
            Proveedor proveedor = new Proveedor();
            proveedor.setId(1);
            proveedor.setNombre(null);
            frm.setFilaSeleccionada(proveedor);
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
        @DisplayName("btnActualizar() con nombre válido debe actualizar")
        void btnActualizar_conNombreValido_debeActualizar() throws Exception {
            // Given
            Proveedor proveedor = new Proveedor();
            proveedor.setId(1);
            proveedor.setNombre("Distribuidora ABC");
            proveedor.setActivo(true);
            frm.setFilaSeleccionada(proveedor);
            frm.setEstado(CRUD.MODIFICAR);

            // Mock finById para validación en DefaultFrm.actualizarEntidad()
            when(proveedorDAO.finById(1)).thenReturn(proveedor);

            // When
            frm.btnActualizar();

            // Then
            verify(proveedorDAO).update(proveedor);
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
            Proveedor proveedor = new Proveedor();
            proveedor.setId(id);
            proveedor.setNombre("Proveedor Test");
            frm.setFilaSeleccionada(proveedor);
            frm.setEstado(CRUD.MODIFICAR);

            when(proveedorDAO.finById(id)).thenReturn(null);

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
            verify(proveedorDAO, never()).delete(any());
        }

        @Test
        @DisplayName("btnEliminar() cuando el nombre cambió debe mostrar error")
        void btnEliminar_nombreCambiado_debeMostrarError() throws Exception {
            // Given
            Integer id = 1;
            Proveedor proveedorSeleccionado = new Proveedor();
            proveedorSeleccionado.setId(id);
            proveedorSeleccionado.setNombre("Nombre Modificado");

            Proveedor proveedorOriginal = new Proveedor();
            proveedorOriginal.setId(id);
            proveedorOriginal.setNombre("Nombre Original");

            frm.setFilaSeleccionada(proveedorSeleccionado);
            frm.setEstado(CRUD.MODIFICAR);

            when(proveedorDAO.finById(id)).thenReturn(proveedorOriginal);

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
            verify(proveedorDAO, never()).delete(any());
        }

        @Test
        @DisplayName("btnEliminar() con validaciones correctas debe eliminar")
        void btnEliminar_validacionesCorrectas_debeEliminar() throws Exception {
            // Given
            Integer id = 1;
            Proveedor proveedor = new Proveedor();
            proveedor.setId(id);
            proveedor.setNombre("Distribuidora XYZ");

            Proveedor proveedorOriginal = new Proveedor();
            proveedorOriginal.setId(id);
            proveedorOriginal.setNombre("Distribuidora XYZ");

            frm.setFilaSeleccionada(proveedor);
            frm.setEstado(CRUD.MODIFICAR);

            when(proveedorDAO.finById(id)).thenReturn(proveedorOriginal);

            // When
            frm.btnEliminar();

            // Then
            verify(proveedorDAO).finById(id);
            verify(proveedorDAO).delete(proveedor);
            messageHelperMock.verify(() ->
                    MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.eliminar.exito")
            );
        }
    }

    @Nested
    @DisplayName("Búsqueda y Conteo")
    class BusquedaConteoTests {

        @Test
        @DisplayName("findRange() debe cargar proveedores correctamente")
        void findRange_debeCargarProveedores() throws Exception {
            // Given
            frm.init();
            List<Proveedor> proveedores = Arrays.asList(
                    crearProveedor("Proveedor 1"),
                    crearProveedor("Proveedor 2"),
                    crearProveedor("Proveedor 3")
            );
            when(proveedorDAO.findRange(anyInt(), anyInt())).thenReturn(proveedores);
            when(proveedorDAO.count()).thenReturn(3L);

            // When
            frm.findRange();

            // Then
            assertNotNull(frm.getEntidadesList());
            verify(proveedorDAO, atLeastOnce()).findRange(anyInt(), anyInt());
        }

        private Proveedor crearProveedor(String nombre) {
            Proveedor proveedor = new Proveedor();
            proveedor.setId((int) (Math.random() * 1000));
            proveedor.setNombre(nombre);
            proveedor.setActivo(true);
            return proveedor;
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
            Proveedor proveedor = new Proveedor();
            proveedor.setId(1);
            proveedor.setNombre("Test");
            frm.setFilaSeleccionada(proveedor);

            SelectEvent<Proveedor> event = mock(SelectEvent.class);
            when(event.getObject()).thenReturn(proveedor);

            // When
            frm.onRowSelect(event);

            // Then
            assertEquals(CRUD.MODIFICAR, frm.getEstado());
        }

        @Test
        @DisplayName("setFilaSeleccionada() debe establecer el proveedor")
        void setFilaSeleccionada_debeEstablecer() {
            // Given
            Proveedor proveedor = new Proveedor();
            proveedor.setId(1);
            proveedor.setNombre("Distribuidora Test");

            // When
            frm.setFilaSeleccionada(proveedor);

            // Then
            assertEquals(proveedor, frm.getFilaSeleccionada());
            assertEquals("Distribuidora Test", frm.getFilaSeleccionada().getNombre());
        }
    }

    @Nested
    @DisplayName("Getters y Setters")
    class GettersSettersTests {

        @Test
        @DisplayName("Debe establecer y obtener filaSeleccionada")
        void debeGetSetFilaSeleccionada() {
            // Given
            Proveedor proveedor = new Proveedor();
            proveedor.setNombre("Test Proveedor");

            // When
            frm.setFilaSeleccionada(proveedor);

            // Then
            assertEquals(proveedor, frm.getFilaSeleccionada());
        }

        @Test
        @DisplayName("Debe establecer y obtener entidadesList")
        void debeGetSetEntidadesList() {
            // Given
            List<Proveedor> proveedores = Arrays.asList(
                    new Proveedor(),
                    new Proveedor()
            );

            // When
            frm.setEntidadesList(proveedores);

            // Then
            assertEquals(proveedores, frm.getEntidadesList());
            assertEquals(2, frm.getEntidadesList().size());
        }

        @Test
        @DisplayName("Debe establecer y obtener proveedorDAO")
        void debeGetSetProveedorDAO() {
            // Given
            ProveedorDAO nuevoDAO = mock(ProveedorDAO.class);

            // When
            frm.setProveedorDAO(nuevoDAO);

            // Then
            assertEquals(nuevoDAO, frm.getProveedorDAO());
        }
    }
}

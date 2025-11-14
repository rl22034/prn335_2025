package core.boundory.jsf;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.CRUD;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.ClienteFrm;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.MessageHelper;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ClienteDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Cliente;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests para ClienteFrm - Gestión de Clientes")
class ClienteFrmTest {

    private ClienteFrm frm;
    private ClienteDAO clienteDAO;
    private MockedStatic<MessageHelper> messageHelperMock;

    @BeforeEach
    void setUp() {
        // Crear instancia real
        frm = new ClienteFrm();

        // Crear mock
        clienteDAO = mock(ClienteDAO.class);

        // Inyectar mock usando reflexión
        inyectarCampo(frm, "clienteDAO", clienteDAO);

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
            // Intentar en la clase padre
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
        @DisplayName("instanciarEntidad() debe crear cliente activo")
        void instanciarEntidad_debeCrearClienteActivo() {
            // When
            frm.init();
            Cliente cliente = frm.getFilaSeleccionada();

            // Then
            assertNotNull(cliente);
            assertTrue(cliente.getActivo(),
                    "El cliente nuevo debe estar activo");
        }
    }

    @Nested
    @DisplayName("Botón Nuevo")
    class BotonNuevoTests {

        @Test
        @DisplayName("btnNuevo() debe crear cliente activo")
        void btnNuevo_debeCrearCliente() {
            // Given
            frm.init();

            // When
            frm.btnNuevo();

            // Then
            assertNotNull(frm.getFilaSeleccionada(),
                    "Debe crear un cliente nuevo");
            assertTrue(frm.getFilaSeleccionada().getActivo(),
                    "El cliente debe estar activo");
            assertEquals(CRUD.CREAR, frm.getEstado(),
                    "El estado debe cambiar a CREAR");
        }

        @Test
        @DisplayName("btnNuevo() debe limpiar selección anterior")
        void btnNuevo_debeLimpiarSeleccionAnterior() {
            // Given
            frm.init();
            Cliente anterior = new Cliente();
            anterior.setId(UUID.randomUUID());
            anterior.setNombre("Cliente Anterior");
            frm.setFilaSeleccionada(anterior);

            // When
            frm.btnNuevo();

            // Then
            assertNotEquals(anterior, frm.getFilaSeleccionada(),
                    "Debe crear una nueva instancia");
            assertNull(frm.getFilaSeleccionada().getId(),
                    "El nuevo cliente no debe tener ID");
        }
    }

    @Nested
    @DisplayName("Botón Agregar - Crear Cliente")
    class BotonAgregarTests {

        @BeforeEach
        void setUp() {
            frm.init();
        }

        @Test
        @DisplayName("btnAgregar() sin nombre debe mostrar error")
        void btnAgregar_sinNombre_debeMostrarError() {
            // Given
            Cliente cliente = new Cliente();
            cliente.setNombre(null);
            frm.setFilaSeleccionada(cliente);
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
            Cliente cliente = new Cliente();
            cliente.setNombre("   ");
            frm.setFilaSeleccionada(cliente);
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
        @DisplayName("btnAgregar() con nombre válido debe crear cliente")
        void btnAgregar_conNombreValido_debeCrear() throws Exception {
            // Given
            Cliente cliente = new Cliente();
            cliente.setNombre("Juan Pérez");
            cliente.setActivo(true);
            frm.setFilaSeleccionada(cliente);
            frm.setEstado(CRUD.CREAR);

            // When
            frm.btnAgregar();

            // Then
            verify(clienteDAO).crear(cliente);
            messageHelperMock.verify(() ->
                    MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.crear.exito")
            );
        }
    }

    @Nested
    @DisplayName("Botón Actualizar - Modificar Cliente")
    class BotonActualizarTests {

        @BeforeEach
        void setUp() {
            frm.init();
        }

        @Test
        @DisplayName("btnActualizar() sin nombre debe mostrar error")
        void btnActualizar_sinNombre_debeMostrarError() {
            // Given
            Cliente cliente = new Cliente();
            cliente.setId(UUID.randomUUID());
            cliente.setNombre(null);
            frm.setFilaSeleccionada(cliente);
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
            Cliente cliente = new Cliente();
            cliente.setId(UUID.randomUUID());
            cliente.setNombre("María García");
            cliente.setActivo(true);
            frm.setFilaSeleccionada(cliente);
            frm.setEstado(CRUD.MODIFICAR);

            // Mock finById para validación en DefaultFrm.actualizarEntidad()
            when(clienteDAO.finById(cliente.getId())).thenReturn(cliente);

            // When
            frm.btnActualizar();

            // Then
            verify(clienteDAO).update(cliente);
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
            UUID id = UUID.randomUUID();
            Cliente cliente = new Cliente();
            cliente.setId(id);
            cliente.setNombre("Cliente Test");
            frm.setFilaSeleccionada(cliente);
            frm.setEstado(CRUD.MODIFICAR);

            when(clienteDAO.finById(id)).thenReturn(null);

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
            verify(clienteDAO, never()).delete(any());
        }

        @Test
        @DisplayName("btnEliminar() cuando el nombre cambió debe mostrar error")
        void btnEliminar_nombreCambiado_debeMostrarError() throws Exception {
            // Given
            UUID id = UUID.randomUUID();
            Cliente clienteSeleccionado = new Cliente();
            clienteSeleccionado.setId(id);
            clienteSeleccionado.setNombre("Nombre Modificado");

            Cliente clienteOriginal = new Cliente();
            clienteOriginal.setId(id);
            clienteOriginal.setNombre("Nombre Original");

            frm.setFilaSeleccionada(clienteSeleccionado);
            frm.setEstado(CRUD.MODIFICAR);

            when(clienteDAO.finById(id)).thenReturn(clienteOriginal);

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
            verify(clienteDAO, never()).delete(any());
        }

        @Test
        @DisplayName("btnEliminar() con validaciones correctas debe eliminar")
        void btnEliminar_validacionesCorrectas_debeEliminar() throws Exception {
            // Given
            UUID id = UUID.randomUUID();
            Cliente cliente = new Cliente();
            cliente.setId(id);
            cliente.setNombre("Juan Pérez");

            Cliente clienteOriginal = new Cliente();
            clienteOriginal.setId(id);
            clienteOriginal.setNombre("Juan Pérez");

            frm.setFilaSeleccionada(cliente);
            frm.setEstado(CRUD.MODIFICAR);

            when(clienteDAO.finById(id)).thenReturn(clienteOriginal);

            // When
            frm.btnEliminar();

            // Then
            verify(clienteDAO).finById(id);
            verify(clienteDAO).delete(cliente);
            messageHelperMock.verify(() ->
                    MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.eliminar.exito")
            );
        }
    }

    @Nested
    @DisplayName("Búsqueda y Conteo")
    class BusquedaConteoTests {

        @Test
        @DisplayName("findRange() debe cargar clientes correctamente")
        void findRange_debeCargarClientes() throws Exception {
            // Given
            frm.init();
            List<Cliente> clientes = Arrays.asList(
                    crearCliente("Cliente 1"),
                    crearCliente("Cliente 2"),
                    crearCliente("Cliente 3")
            );
            when(clienteDAO.findRange(anyInt(), anyInt())).thenReturn(clientes);
            when(clienteDAO.count()).thenReturn(3L);

            // When
            frm.findRange();

            // Then
            assertNotNull(frm.getEntidadesList());
            // No verificar cuántas veces se llamó, solo que se llamó
            verify(clienteDAO, atLeastOnce()).findRange(anyInt(), anyInt());
        }

        private Cliente crearCliente(String nombre) {
            Cliente cliente = new Cliente();
            cliente.setId(UUID.randomUUID());
            cliente.setNombre(nombre);
            cliente.setActivo(true);
            return cliente;
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
            Cliente cliente = new Cliente();
            cliente.setId(UUID.randomUUID());
            cliente.setNombre("Test");
            frm.setFilaSeleccionada(cliente);

            org.primefaces.event.SelectEvent<Cliente> event =
                    mock(org.primefaces.event.SelectEvent.class);
            when(event.getObject()).thenReturn(cliente);

            // When
            frm.onRowSelect(event);

            // Then
            assertEquals(CRUD.MODIFICAR, frm.getEstado());
        }

        @Test
        @DisplayName("setFilaSeleccionada() debe establecer el cliente")
        void setFilaSeleccionada_debeEstablecer() {
            // Given
            Cliente cliente = new Cliente();
            cliente.setId(UUID.randomUUID());
            cliente.setNombre("María López");

            // When
            frm.setFilaSeleccionada(cliente);

            // Then
            assertEquals(cliente, frm.getFilaSeleccionada());
            assertEquals("María López", frm.getFilaSeleccionada().getNombre());
        }
    }
}
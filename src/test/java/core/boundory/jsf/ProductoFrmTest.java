package core.boundory.jsf;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DualListModel;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.CRUD;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.MessageHelper;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.ProductoFrm;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoTipoProductoCaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoTipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.*;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests para ProductoFrm - Gestión de Productos")
class ProductoFrmTest {

    private ProductoFrm frm;
    private ProductoDAO productoDAO;
    private TipoProductoDAO tipoProductoDAO;
    private ProductoTipoProductoDAO productoTipoProductoDAO;
    private ProductoTipoProductoCaracteristicaDAO productoTipoProductoCaracteristicaDAO;
    private MockedStatic<MessageHelper> messageHelperMock;

    @BeforeEach
    void setUp() {
        // Crear instancia real
        frm = new ProductoFrm();

        // Crear mocks
        productoDAO = mock(ProductoDAO.class);
        tipoProductoDAO = mock(TipoProductoDAO.class);
        productoTipoProductoDAO = mock(ProductoTipoProductoDAO.class);
        productoTipoProductoCaracteristicaDAO = mock(ProductoTipoProductoCaracteristicaDAO.class);

        // Inyectar mocks usando reflexión
        inyectarCampo(frm, "productoDAO", productoDAO);
        inyectarCampo(frm, "tipoProductoDAO", tipoProductoDAO);
        inyectarCampo(frm, "productoTipoProductoDAO", productoTipoProductoDAO);
        inyectarCampo(frm, "productoTipoProductoCaracteristicaDAO", productoTipoProductoCaracteristicaDAO);

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
            assertNotNull(frm.getTipoProductoDisponiblesList());
            assertNotNull(frm.getValoresCaracteristicas());
            assertNotNull(frm.getPickListCaracteristicas());
            assertEquals(0, frm.getTabActivo());
        }
    }

    @Nested
    @DisplayName("Botones CRUD")
    class BotonesCRUDTests {

        @BeforeEach
        void setUp() {
            frm.init();
        }

        @Test
        @DisplayName("btnNuevo() debe crear producto activo")
        void btnNuevo_debeCrearProducto() {
            // When
            frm.btnNuevo();

            // Then
            assertNotNull(frm.getFilaSeleccionada());
            assertTrue(frm.getFilaSeleccionada().getActivo());
            assertEquals(CRUD.CREAR, frm.getEstado());
        }

        @Test
        @DisplayName("btnAgregar() sin nombre debe mostrar error")
        void btnAgregar_sinNombre_debeMostrarError() {
            // Given
            Producto producto = new Producto();
            producto.setNombreProducto(null);
            frm.setFilaSeleccionada(producto);
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
        @DisplayName("btnAgregar() con nombre válido debe crear")
        void btnAgregar_conNombreValido_debeCrear() throws Exception {
            // Given
            Producto producto = new Producto();
            producto.setNombreProducto("Laptop Dell");
            frm.setFilaSeleccionada(producto);
            frm.setEstado(CRUD.CREAR);

            // When
            frm.btnAgregar();

            // Then
            verify(productoDAO).crear(producto);
            messageHelperMock.verify(() ->
                    MessageHelper.addInfoMessage("mensaje.titulo.exito", "mensaje.crear.exito")
            );
        }

        @Test
        @DisplayName("btnActualizar() debe actualizar producto")
        void btnActualizar_debeActualizar() throws Exception {
            // Given
            Producto producto = new Producto();
            producto.setId(UUID.randomUUID());
            producto.setNombreProducto("Laptop HP");
            frm.setFilaSeleccionada(producto);
            frm.setEstado(CRUD.MODIFICAR);

            // When
            frm.btnActualizar();

            // Then
            verify(productoDAO).update(producto);
        }

        @Test
        @DisplayName("btnEliminar() debe eliminar producto")
        void btnEliminar_debeEliminar() throws Exception {
            // Given
            Producto producto = new Producto();
            producto.setId(UUID.randomUUID());
            producto.setNombreProducto("Test");
            frm.setFilaSeleccionada(producto);
            frm.setEstado(CRUD.MODIFICAR);

            // When
            frm.btnEliminar();

            // Then
            verify(productoDAO).delete(producto);
        }
    }

    @Nested
    @DisplayName("Manejo de Tabs")
    class ManejoTabsTests {

        @Test
        @DisplayName("onTabChange() debe actualizar índice")
        void onTabChange_debeActualizarIndice() {
            // Given
            TabChangeEvent<?> event = mock(TabChangeEvent.class);
            TabView tabView = mock(TabView.class);
            when(event.getComponent()).thenReturn(tabView);
            when(tabView.getActiveIndex()).thenReturn(2);

            // When
            frm.onTabChange(event);

            // Then
            assertEquals(2, frm.getTabActivo());
        }

        @Test
        @DisplayName("onTabChange() a tab 1 debe cargar tipos")
        void onTabChange_aTab1_debeCargarTipos() throws Exception {
            // Given
            TabChangeEvent<?> event = mock(TabChangeEvent.class);
            TabView tabView = mock(TabView.class);
            when(event.getComponent()).thenReturn(tabView);
            when(tabView.getActiveIndex()).thenReturn(1);

            List<TipoProducto> tipos = Arrays.asList(new TipoProducto());
            when(tipoProductoDAO.findRange(0, 1000)).thenReturn(tipos);

            // When
            frm.onTabChange(event);

            // Then
            verify(tipoProductoDAO).findRange(0, 1000);
        }
    }

    @Nested
    @DisplayName("Gestión de Tipo de Producto")
    class GestionTipoProductoTests {

        @Test
        @DisplayName("abrirDialogoSeleccionarTipo() sin producto debe advertir")
        void abrirDialogo_sinProducto_debeAdvertir() {
            // Given
            frm.setFilaSeleccionada(null);

            // When
            frm.abrirDialogoSeleccionarTipo();

            // Then
            messageHelperMock.verify(() ->
                    MessageHelper.addWarnMessage("mensaje.titulo.advertencia", "mensaje.seleccionar.producto")
            );
        }

        @Test
        @DisplayName("abrirDialogoSeleccionarTipo() con producto debe abrir")
        void abrirDialogo_conProducto_debeAbrir() throws Exception {
            // Given
            Producto producto = new Producto();
            producto.setId(UUID.randomUUID());
            frm.setFilaSeleccionada(producto);

            when(tipoProductoDAO.findRange(0, 1000)).thenReturn(new ArrayList<>());

            // When
            frm.abrirDialogoSeleccionarTipo();

            // Then
            assertTrue(frm.isMostrarDialogoTipo());
        }

        @Test
        @DisplayName("cancelarSeleccionTipo() debe cerrar diálogo")
        void cancelarSeleccion_debeCerrar() {
            // Given
            frm.setMostrarDialogoTipo(true);
            frm.setTipoProductoSeleccionadoId(1L);

            // When
            frm.cancelarSeleccionTipo();

            // Then
            assertFalse(frm.isMostrarDialogoTipo());
            assertNull(frm.getTipoProductoSeleccionadoId());
        }
    }

        @Nested
        @DisplayName("Gestión de Características")
        class GestionCaracteristicasTests {

            @Test
            @DisplayName("guardarCaracteristicas() sin productoTipo debe advertir")
            void guardarCaracteristicas_sinProductoTipo_debeAdvertir() {
                // Given
                frm.setProductoTipoProductoActual(null);

                // When
                frm.guardarCaracteristicas();

                // Then
                messageHelperMock.verify(() ->
                        MessageHelper.addWarnMessage("mensaje.titulo.advertencia", "mensaje.seleccionar.tipo.primero")
                );
            }

            @Test
            @DisplayName("cancelarModificacionCaracteristica() debe limpiar")
            void cancelarModificacion_debeLimpiar() {
                // Given
                ProductoTipoProductoCaracteristica ptpc = new ProductoTipoProductoCaracteristica();
                frm.setCaracteristicaParaModificar(ptpc);

                // When
                frm.cancelarModificacionCaracteristica();

                // Then
                assertNull(frm.getCaracteristicaParaModificar());
            }

            @Test
            @DisplayName("cancelarTiposDeProducto() debe limpiar todo")
            void cancelarTipos_debeLimpiarTodo() {
                // Given
                frm.init();
                frm.setProductoTipoProductoActual(new ProductoTipoProducto());
                frm.setPickListCaracteristicas(new DualListModel<>());
                frm.getValoresCaracteristicas().put(1L, "valor");

                // When
                frm.cancelarTiposDeProducto();

                // Then
                assertNull(frm.getProductoTipoProductoActual());
                assertNull(frm.getPickListCaracteristicas());
                assertTrue(frm.getValoresCaracteristicas().isEmpty());
            }
        }


    @Nested
    @DisplayName("Getters y Setters")
    class GettersSettersTests {

        @Test
        @DisplayName("Debe get/set tabActivo")
        void debeGetSetTabActivo() {
            frm.setTabActivo(3);
            assertEquals(3, frm.getTabActivo());
        }

        @Test
        @DisplayName("Debe get/set tipoProductoSeleccionadoId")
        void debeGetSetTipoProductoSeleccionadoId() {
            frm.setTipoProductoSeleccionadoId(42L);
            assertEquals(42L, frm.getTipoProductoSeleccionadoId());
        }

        @Test
        @DisplayName("Debe get/set mostrarDialogoTipo")
        void debeGetSetMostrarDialogoTipo() {
            frm.setMostrarDialogoTipo(true);
            assertTrue(frm.isMostrarDialogoTipo());
        }
    }
}
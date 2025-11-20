package sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.CRUD;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.DefaultFrm;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.MessageHelper;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.InventarioDefaultDataAccess;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultFrmTest {

    // Entidad de prueba simple
    static class EntidadPrueba {
        private Integer id;
        private String nombre;

        public EntidadPrueba() {}

        public EntidadPrueba(Integer id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }

    // Implementación concreta de DefaultFrm para testing
    static class DefaultFrmConcrete extends DefaultFrm<EntidadPrueba> {
        private InventarioDefaultDataAccess<EntidadPrueba> dao;
        private boolean validarCrearLlamado = false;
        private boolean validarActualizarLlamado = false;
        private boolean validarEliminarLlamado = false;
        private boolean debeElanzarExcepcionEnCrear = false;
        private boolean debeElanzarExcepcionEnActualizar = false;
        private boolean debeElanzarExcepcionEnEliminar = false;
        private boolean debeElanzarExcepcionEnInstanciar = false;

        public DefaultFrmConcrete(InventarioDefaultDataAccess<EntidadPrueba> dao) {
            this.dao = dao;
        }

        @Override
        protected InventarioDefaultDataAccess<EntidadPrueba> obtenerDAO() {
            return dao;
        }

        @Override
        protected EntidadPrueba instanciarEntidad() {
            if (debeElanzarExcepcionEnInstanciar) {
                throw new RuntimeException("Error al instanciar entidad");
            }
            return new EntidadPrueba();
        }

        @Override
        protected void validarAntesDeCrear(EntidadPrueba entidad) throws Exception {
            validarCrearLlamado = true;
            if (debeElanzarExcepcionEnCrear) {
                throw new Exception("validacion.prueba.crear");
            }
        }

        @Override
        protected void validarAntesDeActualizar(EntidadPrueba entidad) throws Exception {
            validarActualizarLlamado = true;
            if (debeElanzarExcepcionEnActualizar) {
                throw new Exception("validacion.prueba.actualizar");
            }
        }

        @Override
        protected void validarAntesDeEliminar(EntidadPrueba entidad, EntidadPrueba original) throws Exception {
            validarEliminarLlamado = true;
            if (debeElanzarExcepcionEnEliminar) {
                throw new Exception("validacion.prueba.eliminar");
            }
        }

        public void configurarExcepcionCrear(boolean lanzar) {
            this.debeElanzarExcepcionEnCrear = lanzar;
        }

        public void configurarExcepcionActualizar(boolean lanzar) {
            this.debeElanzarExcepcionEnActualizar = lanzar;
        }

        public void configurarExcepcionEliminar(boolean lanzar) {
            this.debeElanzarExcepcionEnEliminar = lanzar;
        }

        public void configurarExcepcionInstanciar(boolean lanzar) {
            this.debeElanzarExcepcionEnInstanciar = lanzar;
        }

        // Métodos públicos para exponer los métodos protected para testing
        public void crearEntidadPublic(EntidadPrueba entidad) throws Exception {
            crearEntidad(entidad);
        }

        public void actualizarEntidadPublic(EntidadPrueba entidad) throws Exception {
            actualizarEntidad(entidad);
        }

        public void eliminarEntidadPublic(EntidadPrueba entidad) throws Exception {
            eliminarEntidad(entidad);
        }

        public List<EntidadPrueba> buscarEntidadesPublic(int first, int pageSize) throws Exception {
            return buscarEntidades(first, pageSize);
        }

        public Long contarEntidadesPublic() throws Exception {
            return contarEntidades();
        }

        public Object obtenerIdEntidadPublic(EntidadPrueba entidad) {
            return obtenerIdEntidad(entidad);
        }
    }

    @Mock
    private InventarioDefaultDataAccess<EntidadPrueba> mockDAO;

    private DefaultFrmConcrete frm;
    private MockedStatic<MessageHelper> messageHelperMock;

    @BeforeEach
    void setUp() {
        frm = new DefaultFrmConcrete(mockDAO);
        messageHelperMock = mockStatic(MessageHelper.class);
    }

    @AfterEach
    void tearDown() {
        if (messageHelperMock != null) {
            messageHelperMock.close();
        }
    }

    // ========== 1. TESTS DE INICIALIZACIÓN ==========

    @Test
    void init_debeInicializarEstadoYFilaSeleccionada() {
        frm.init();

        assertEquals(CRUD.NINGUNO, frm.getEstado());
        assertNotNull(frm.getFilaSeleccionada());
    }

    // ========== 2. TESTS DE TEMPLATE METHOD - CRUD ==========

    // --- crearEntidad() ---
    @Test
    void crearEntidad_entidadNull_debeLanzarExcepcion() {
        Exception exception = assertThrows(Exception.class, () -> {
            frm.crearEntidadPublic(null);
        });
        assertEquals("validacion.entidad.nula", exception.getMessage());
    }

    @Test
    void crearEntidad_conIdNoNulo_debeLanzarExcepcion() {
        EntidadPrueba entidad = new EntidadPrueba(1, "Test");

        Exception exception = assertThrows(Exception.class, () -> {
            frm.crearEntidadPublic(entidad);
        });
        assertEquals("validacion.entidad.id.debe.ser.nulo", exception.getMessage());
    }

    @Test
    void crearEntidad_validacionPersonalizadaFalla_debeLanzarExcepcion() {
        EntidadPrueba entidad = new EntidadPrueba(null, "Test");
        frm.configurarExcepcionCrear(true);

        Exception exception = assertThrows(Exception.class, () -> {
            frm.crearEntidadPublic(entidad);
        });
        assertEquals("validacion.prueba.crear", exception.getMessage());
        assertTrue(frm.validarCrearLlamado);
    }

    @Test
    void crearEntidad_exitoso_debeLlamarDAO() throws Exception {
        EntidadPrueba entidad = new EntidadPrueba(null, "Test");

        frm.crearEntidadPublic(entidad);

        assertTrue(frm.validarCrearLlamado);
        verify(mockDAO, times(1)).crear(entidad);
    }

    // --- actualizarEntidad() ---
    @Test
    void actualizarEntidad_entidadNull_debeLanzarExcepcion() {
        Exception exception = assertThrows(Exception.class, () -> {
            frm.actualizarEntidadPublic(null);
        });
        assertEquals("validacion.entidad.nula", exception.getMessage());
    }

    @Test
    void actualizarEntidad_idNull_debeLanzarExcepcion() {
        EntidadPrueba entidad = new EntidadPrueba(null, "Test");

        Exception exception = assertThrows(Exception.class, () -> {
            frm.actualizarEntidadPublic(entidad);
        });
        assertEquals("validacion.entidad.id.requerido", exception.getMessage());
    }

    @Test
    void actualizarEntidad_noExisteEnBD_debeLanzarExcepcion() {
        EntidadPrueba entidad = new EntidadPrueba(1, "Test");
        when(mockDAO.finById(1)).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            frm.actualizarEntidadPublic(entidad);
        });
        assertEquals("validacion.registro.no.existe", exception.getMessage());
    }

    @Test
    void actualizarEntidad_validacionPersonalizadaFalla_debeLanzarExcepcion() {
        EntidadPrueba entidad = new EntidadPrueba(1, "Test");
        EntidadPrueba original = new EntidadPrueba(1, "Original");
        when(mockDAO.finById(1)).thenReturn(original);
        frm.configurarExcepcionActualizar(true);

        Exception exception = assertThrows(Exception.class, () -> {
            frm.actualizarEntidadPublic(entidad);
        });
        assertEquals("validacion.prueba.actualizar", exception.getMessage());
        assertTrue(frm.validarActualizarLlamado);
    }

    @Test
    void actualizarEntidad_exitoso_debeLlamarDAO() throws Exception {
        EntidadPrueba entidad = new EntidadPrueba(1, "Test");
        EntidadPrueba original = new EntidadPrueba(1, "Original");
        when(mockDAO.finById(1)).thenReturn(original);

        frm.actualizarEntidadPublic(entidad);

        assertTrue(frm.validarActualizarLlamado);
        verify(mockDAO, times(1)).update(entidad);
    }

    // --- eliminarEntidad() ---
    @Test
    void eliminarEntidad_entidadNull_debeLanzarExcepcion() {
        Exception exception = assertThrows(Exception.class, () -> {
            frm.eliminarEntidadPublic(null);
        });
        assertEquals("validacion.entidad.nula", exception.getMessage());
    }

    @Test
    void eliminarEntidad_idNull_debeLanzarExcepcion() {
        EntidadPrueba entidad = new EntidadPrueba(null, "Test");

        Exception exception = assertThrows(Exception.class, () -> {
            frm.eliminarEntidadPublic(entidad);
        });
        assertEquals("validacion.entidad.id.requerido", exception.getMessage());
    }

    @Test
    void eliminarEntidad_noExisteEnBD_debeLanzarExcepcion() {
        EntidadPrueba entidad = new EntidadPrueba(1, "Test");
        when(mockDAO.finById(1)).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            frm.eliminarEntidadPublic(entidad);
        });
        assertEquals("validacion.registro.no.existe", exception.getMessage());
    }

    @Test
    void eliminarEntidad_validacionPersonalizadaFalla_debeLanzarExcepcion() {
        EntidadPrueba entidad = new EntidadPrueba(1, "Test");
        EntidadPrueba original = new EntidadPrueba(1, "Original");
        when(mockDAO.finById(1)).thenReturn(original);
        frm.configurarExcepcionEliminar(true);

        Exception exception = assertThrows(Exception.class, () -> {
            frm.eliminarEntidadPublic(entidad);
        });
        assertEquals("validacion.prueba.eliminar", exception.getMessage());
        assertTrue(frm.validarEliminarLlamado);
    }

    @Test
    void eliminarEntidad_exitoso_debeLlamarDAO() throws Exception {
        EntidadPrueba entidad = new EntidadPrueba(1, "Test");
        EntidadPrueba original = new EntidadPrueba(1, "Original");
        when(mockDAO.finById(1)).thenReturn(original);

        frm.eliminarEntidadPublic(entidad);

        assertTrue(frm.validarEliminarLlamado);
        verify(mockDAO, times(1)).delete(entidad);
    }

    // ========== 3. TESTS DE MÉTODOS DE VALIDACIÓN POR DEFECTO ==========

    @Test
    void validarAntesDeCrear_implementacionPorDefecto_noDebeLanzarExcepcion() {
        // Crear un DefaultFrm sin sobrescribir validarAntesDeCrear
        DefaultFrmConcrete frmSinValidacion = new DefaultFrmConcrete(mockDAO) {
            @Override
            protected void validarAntesDeCrear(EntidadPrueba entidad) throws Exception {
                // Usar la implementación por defecto (vacía) del padre
                super.validarAntesDeCrear(entidad);
            }
        };

        EntidadPrueba entidad = new EntidadPrueba(null, "Test");

        // No debe lanzar excepción
        assertDoesNotThrow(() -> frmSinValidacion.crearEntidadPublic(entidad));
    }

    @Test
    void validarAntesDeActualizar_implementacionPorDefecto_noDebeLanzarExcepcion() throws Exception {
        // Crear un DefaultFrm sin sobrescribir validarAntesDeActualizar
        DefaultFrmConcrete frmSinValidacion = new DefaultFrmConcrete(mockDAO) {
            @Override
            protected void validarAntesDeActualizar(EntidadPrueba entidad) throws Exception {
                // Usar la implementación por defecto (vacía) del padre
                super.validarAntesDeActualizar(entidad);
            }
        };

        EntidadPrueba entidad = new EntidadPrueba(1, "Test");
        when(mockDAO.finById(1)).thenReturn(new EntidadPrueba(1, "Original"));

        // No debe lanzar excepción
        assertDoesNotThrow(() -> frmSinValidacion.actualizarEntidadPublic(entidad));
    }

    @Test
    void validarAntesDeEliminar_implementacionPorDefecto_noDebeLanzarExcepcion() throws Exception {
        // Crear un DefaultFrm sin sobrescribir validarAntesDeEliminar
        DefaultFrmConcrete frmSinValidacion = new DefaultFrmConcrete(mockDAO) {
            @Override
            protected void validarAntesDeEliminar(EntidadPrueba entidad, EntidadPrueba original) throws Exception {
                // Usar la implementación por defecto (vacía) del padre
                super.validarAntesDeEliminar(entidad, original);
            }
        };

        EntidadPrueba entidad = new EntidadPrueba(1, "Test");
        when(mockDAO.finById(1)).thenReturn(new EntidadPrueba(1, "Original"));

        // No debe lanzar excepción
        assertDoesNotThrow(() -> frmSinValidacion.eliminarEntidadPublic(entidad));
    }

    // ========== 4. TESTS DE MÉTODOS AUXILIARES ==========

    @Test
    void buscarEntidades_debeRetornarListaDelDAO() throws Exception {
        List<EntidadPrueba> lista = Arrays.asList(new EntidadPrueba(1, "Test"));
        when(mockDAO.findRange(0, 10)).thenReturn(lista);

        List<EntidadPrueba> resultado = frm.buscarEntidadesPublic(0, 10);

        assertEquals(lista, resultado);
        verify(mockDAO, times(1)).findRange(0, 10);
    }

    @Test
    void contarEntidades_debeRetornarCountDelDAO() throws Exception {
        when(mockDAO.count()).thenReturn(5L);

        Long resultado = frm.contarEntidadesPublic();

        assertEquals(5L, resultado);
        verify(mockDAO, times(1)).count();
    }

    @Test
    void obtenerIdEntidad_debeRetornarElId() {
        EntidadPrueba entidad = new EntidadPrueba(1, "Test");

        Object id = frm.obtenerIdEntidadPublic(entidad);

        assertEquals(1, id);
    }

    @Test
    void obtenerIdEntidad_idNull_debeRetornarNull() {
        EntidadPrueba entidad = new EntidadPrueba(null, "Test");

        Object id = frm.obtenerIdEntidadPublic(entidad);

        assertNull(id);
    }

    @Test
    void findRange_debeCargarListaDeEntidades() throws Exception {
        List<EntidadPrueba> lista = Arrays.asList(new EntidadPrueba(1, "Test"));
        when(mockDAO.findRange(0, 50)).thenReturn(lista);

        frm.findRange();

        assertEquals(lista, frm.getEntidadesList());
    }

    // ========== 5. TESTS DE SELECCIÓN Y NAVEGACIÓN ==========

    @Test
    void onRowSelect_debeSeleccionarFilaYCambiarEstado() {
        EntidadPrueba entidad = new EntidadPrueba(1, "Test");
        SelectEvent<EntidadPrueba> event = mock(SelectEvent.class);
        when(event.getObject()).thenReturn(entidad);

        frm.onRowSelect(event);

        assertEquals(entidad, frm.getFilaSeleccionada());
        assertEquals(CRUD.MODIFICAR, frm.getEstado());
    }

    @Test
    void limpiarSeleccionado_debeLimpiarSeleccionYEstado() {
        frm.setFilaSeleccionada(new EntidadPrueba(1, "Test"));
        frm.setEstado(CRUD.MODIFICAR);

        frm.limpiarSeleccionado();

        // El getter crea una nueva instancia si es null, así que verificamos que sea nueva (sin ID)
        assertNull(frm.getFilaSeleccionada().getId());
        assertEquals(CRUD.NINGUNO, frm.getEstado());
    }

    // ========== 6. TESTS DE BOTONES UI ==========

    @Test
    void btnNuevo_debeCrearNuevaEntidadYCambiarEstado() {
        frm.btnNuevo();

        assertNotNull(frm.getFilaSeleccionada());
        assertEquals(CRUD.CREAR, frm.getEstado());
    }

    @Test
    void btnAgregar_exitoso_debeCrearYReiniciar() throws Exception {
        EntidadPrueba entidad = new EntidadPrueba(null, "Test");
        frm.setFilaSeleccionada(entidad);

        frm.btnAgregar();

        assertEquals(CRUD.NINGUNO, frm.getEstado());
        verify(mockDAO, times(1)).crear(entidad);
    }

    @Test
    void btnAgregar_conError_noDebeReiniciarEstado() {
        EntidadPrueba entidad = new EntidadPrueba(1, "Test"); // ID no nulo causa error
        frm.setFilaSeleccionada(entidad);

        frm.btnAgregar();

        // Estado no cambia porque hubo error
        assertEquals(CRUD.NINGUNO, frm.getEstado());
    }

    @Test
    void btnEliminar_exitoso_debeEliminarYReiniciar() throws Exception {
        EntidadPrueba entidad = new EntidadPrueba(1, "Test");
        EntidadPrueba original = new EntidadPrueba(1, "Original");
        frm.setFilaSeleccionada(entidad);
        when(mockDAO.finById(1)).thenReturn(original);

        frm.btnEliminar();

        assertEquals(CRUD.NINGUNO, frm.getEstado());
        verify(mockDAO, times(1)).delete(entidad);
    }

    @Test
    void btnEliminar_conError_noDebeReiniciarEstado() {
        EntidadPrueba entidad = new EntidadPrueba(1, "Test");
        frm.setFilaSeleccionada(entidad);
        when(mockDAO.finById(1)).thenReturn(null); // No existe, causa error

        frm.btnEliminar();

        assertEquals(CRUD.NINGUNO, frm.getEstado());
    }

    @Test
    void btnActualizar_exitoso_debeActualizarYReiniciar() throws Exception {
        EntidadPrueba entidad = new EntidadPrueba(1, "Test");
        EntidadPrueba original = new EntidadPrueba(1, "Original");
        frm.setFilaSeleccionada(entidad);
        when(mockDAO.finById(1)).thenReturn(original);

        frm.btnActualizar();

        assertEquals(CRUD.NINGUNO, frm.getEstado());
        verify(mockDAO, times(1)).update(entidad);
    }

    @Test
    void btnActualizar_conError_noDebeReiniciarEstado() {
        EntidadPrueba entidad = new EntidadPrueba(1, "Test");
        frm.setFilaSeleccionada(entidad);
        when(mockDAO.finById(1)).thenReturn(null); // No existe, causa error

        frm.btnActualizar();

        assertEquals(CRUD.NINGUNO, frm.getEstado());
    }

    // ========== 7. TESTS DE LAZYDATA MODEL ==========

    @Test
    void initLazyModel_debeCrearLazyModel() {
        frm.initLazyModel();

        assertNotNull(frm.getLazyModel());
    }

    @Test
    void getLazyModel_cuandoEsNull_debeInicializarlo() {
        frm.setLazyModel(null);

        LazyDataModel<EntidadPrueba> modelo = frm.getLazyModel();

        assertNotNull(modelo);
    }

    @Test
    void lazyModel_count_debeRetornarCount() throws Exception {
        when(mockDAO.count()).thenReturn(10L);
        frm.initLazyModel();

        int count = frm.getLazyModel().count(Collections.emptyMap());

        assertEquals(10, count);
    }

    @Test
    void lazyModel_count_conError_debeRetornarCero() throws Exception {
        when(mockDAO.count()).thenThrow(new RuntimeException("Error"));
        frm.initLazyModel();

        int count = frm.getLazyModel().count(Collections.emptyMap());

        assertEquals(0, count);
    }

    @Test
    void lazyModel_load_debeCargarDatos() throws Exception {
        List<EntidadPrueba> lista = Arrays.asList(new EntidadPrueba(1, "Test"));
        when(mockDAO.findRange(0, 10)).thenReturn(lista);
        frm.initLazyModel();

        List<EntidadPrueba> resultado = frm.getLazyModel().load(0, 10, Collections.emptyMap(), Collections.emptyMap());

        assertEquals(lista, resultado);
    }

    @Test
    void lazyModel_load_conListaNull_debeRetornarListaVacia() throws Exception {
        when(mockDAO.findRange(anyInt(), anyInt())).thenReturn(null);
        frm.initLazyModel();

        List<EntidadPrueba> resultado = frm.getLazyModel().load(0, 10, Collections.emptyMap(), Collections.emptyMap());

        assertTrue(resultado.isEmpty());
    }

    @Test
    void lazyModel_load_conError_debeRetornarListaVacia() throws Exception {
        when(mockDAO.findRange(anyInt(), anyInt())).thenThrow(new RuntimeException("Error"));
        frm.initLazyModel();

        List<EntidadPrueba> resultado = frm.getLazyModel().load(0, 10, Collections.emptyMap(), Collections.emptyMap());

        assertTrue(resultado.isEmpty());
    }

    @Test
    void lazyModel_getRowKey_debeRetornarIdComoString() {
        EntidadPrueba entidad = new EntidadPrueba(1, "Test");
        frm.initLazyModel();

        String rowKey = frm.getLazyModel().getRowKey(entidad);

        assertEquals("1", rowKey);
    }

    @Test
    void lazyModel_getRowKey_conIdNull_debeRetornarNull() {
        EntidadPrueba entidad = new EntidadPrueba(null, "Test");
        frm.initLazyModel();

        String rowKey = frm.getLazyModel().getRowKey(entidad);

        assertNull(rowKey);
    }

    @Test
    void lazyModel_getRowData_debeEncontrarEntidad() {
        EntidadPrueba entidad1 = new EntidadPrueba(1, "Test1");
        EntidadPrueba entidad2 = new EntidadPrueba(2, "Test2");
        List<EntidadPrueba> lista = Arrays.asList(entidad1, entidad2);
        frm.initLazyModel();
        frm.getLazyModel().setWrappedData(lista);

        EntidadPrueba resultado = frm.getLazyModel().getRowData("2");

        assertEquals(entidad2, resultado);
    }

    @Test
    void lazyModel_getRowData_noEncontrado_debeRetornarNull() {
        EntidadPrueba entidad1 = new EntidadPrueba(1, "Test1");
        List<EntidadPrueba> lista = Arrays.asList(entidad1);
        frm.initLazyModel();
        frm.getLazyModel().setWrappedData(lista);

        EntidadPrueba resultado = frm.getLazyModel().getRowData("999");

        assertNull(resultado);
    }

    @Test
    void lazyModel_getRowData_listaNull_debeRetornarNull() {
        frm.initLazyModel();
        frm.getLazyModel().setWrappedData(null);

        EntidadPrueba resultado = frm.getLazyModel().getRowData("1");

        assertNull(resultado);
    }

    // ========== 8. TESTS DE GETTERS ESPECIALES ==========

    @Test
    void getFilaSeleccionada_cuandoEsNull_debeInstanciarNueva() {
        frm.setFilaSeleccionada(null);

        EntidadPrueba resultado = frm.getFilaSeleccionada();

        assertNotNull(resultado);
    }

    // ========== 9. TESTS DE GETTERS Y SETTERS BÁSICOS ==========

    @Test
    void setGetEntidadesList_debeFuncionar() {
        List<EntidadPrueba> lista = Arrays.asList(new EntidadPrueba(1, "Test"));

        frm.setEntidadesList(lista);

        assertEquals(lista, frm.getEntidadesList());
    }

    @Test
    void setGetEstado_debeFuncionar() {
        frm.setEstado(CRUD.CREAR);

        assertEquals(CRUD.CREAR, frm.getEstado());
    }

    // ========== 10. TESTS DE EXCEPCIONES EN CATCH BLOCKS ==========

    @Test
    void btnNuevo_conExcepcionEnInstanciar_debeManejarError() {
        frm.setEstado(CRUD.MODIFICAR); // Estado inicial diferente a NINGUNO
        frm.configurarExcepcionInstanciar(true);

        // No debe lanzar excepción, debe capturarla e imprimirla
        assertDoesNotThrow(() -> frm.btnNuevo());

        // El estado NO debe cambiar porque la excepción ocurre antes de establecer estado = CREAR
        assertEquals(CRUD.MODIFICAR, frm.getEstado());
    }

    @Test
    void findRange_conExcepcionEnDAO_debeManejarError() throws Exception {
        when(mockDAO.findRange(0, 50)).thenThrow(new RuntimeException("Error de base de datos"));

        // No debe lanzar excepción, debe capturarla e imprimirla
        assertDoesNotThrow(() -> frm.findRange());

        // La lista debe permanecer null ya que no se pudo cargar
        assertNull(frm.getEntidadesList());
    }

    @Test
    void obtenerIdEntidad_sinMetodoGetId_debeLanzarRuntimeException() {
        // Crear un DefaultFrm con una entidad que no tiene método getId()
        class EntidadSinGetId {
            private Integer valor;
            // No tiene método getId()
        }

        var frmConEntidadSinId = new DefaultFrm<EntidadSinGetId>() {
            @Override
            protected InventarioDefaultDataAccess<EntidadSinGetId> obtenerDAO() {
                return null;
            }

            @Override
            protected EntidadSinGetId instanciarEntidad() {
                return new EntidadSinGetId();
            }

            public Object obtenerIdPublico(EntidadSinGetId entidad) {
                return obtenerIdEntidad(entidad);
            }
        };

        EntidadSinGetId entidad = new EntidadSinGetId();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            frmConEntidadSinId.obtenerIdPublico(entidad);
        });

        assertTrue(exception.getMessage().contains("No se pudo obtener el ID de la entidad"));
    }
}

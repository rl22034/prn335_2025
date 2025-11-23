package core.boundory.jsf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.ProductoFrm;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.ProductoDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Producto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductoFrmTest {

    @Mock
    private ProductoDAO productoDAO;

    private ProductoFrmTestable productoFrm;

    // Subclase para acceder a métodos protected
    static class ProductoFrmTestable extends ProductoFrm {

        private ProductoDAO daoMock;

        @Override
        protected ProductoDAO obtenerDAO() {
            return daoMock;
        }

        public void setDaoMock(ProductoDAO dao) {
            this.daoMock = dao;
        }

        // Exponer métodos protected como public para testing
        public void validarCrear(Producto entidad) throws Exception {
            validarAntesDeCrear(entidad);
        }

        public void validarActualizar(Producto entidad) throws Exception {
            validarAntesDeActualizar(entidad);
        }

        public void validarEliminar(Producto entidad, Producto original) throws Exception {
            validarAntesDeEliminar(entidad, original);
        }

        public Producto crearNuevaInstancia() {
            return instanciarEntidad();
        }
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productoFrm = new ProductoFrmTestable();
        productoFrm.setDaoMock(productoDAO);
        productoFrm.setProductoDAO(productoDAO);
    }

    // ========== Tests para obtenerDAO ==========

    @Test
    void testObtenerDAO() {
        ProductoDAO dao = productoFrm.obtenerDAO();
        assertNotNull(dao);
        assertEquals(productoDAO, dao);
    }

    // ========== Tests para validarAntesDeCrear ==========

    @Test
    void testValidarAntesDeCrearExitoso() {
        Producto producto = new Producto();
        producto.setNombreProducto("Laptop HP");

        assertDoesNotThrow(() -> productoFrm.validarCrear(producto));
    }

    @Test
    void testValidarAntesDeCrearNombreNull() {
        Producto producto = new Producto();
        producto.setNombreProducto(null);

        Exception ex = assertThrows(Exception.class,
                () -> productoFrm.validarCrear(producto));
        assertEquals("validacion.nombre.requerido", ex.getMessage());
    }

    @Test
    void testValidarAntesDeCrearNombreVacio() {
        Producto producto = new Producto();
        producto.setNombreProducto("");

        Exception ex = assertThrows(Exception.class,
                () -> productoFrm.validarCrear(producto));
        assertEquals("validacion.nombre.requerido", ex.getMessage());
    }

    @Test
    void testValidarAntesDeCrearNombreSoloEspacios() {
        Producto producto = new Producto();
        producto.setNombreProducto("   ");

        Exception ex = assertThrows(Exception.class,
                () -> productoFrm.validarCrear(producto));
        assertEquals("validacion.nombre.requerido", ex.getMessage());
    }

    // ========== Tests para validarAntesDeActualizar ==========

    @Test
    void testValidarAntesDeActualizarExitoso() {
        Producto producto = new Producto();
        producto.setNombreProducto("Monitor Dell");

        assertDoesNotThrow(() -> productoFrm.validarActualizar(producto));
    }

    @Test
    void testValidarAntesDeActualizarNombreNull() {
        Producto producto = new Producto();
        producto.setNombreProducto(null);

        Exception ex = assertThrows(Exception.class,
                () -> productoFrm.validarActualizar(producto));
        assertEquals("validacion.nombre.requerido", ex.getMessage());
    }

    @Test
    void testValidarAntesDeActualizarNombreVacio() {
        Producto producto = new Producto();
        producto.setNombreProducto("");

        Exception ex = assertThrows(Exception.class,
                () -> productoFrm.validarActualizar(producto));
        assertEquals("validacion.nombre.requerido", ex.getMessage());
    }

    @Test
    void testValidarAntesDeActualizarNombreSoloEspacios() {
        Producto producto = new Producto();
        producto.setNombreProducto("  \t  ");

        Exception ex = assertThrows(Exception.class,
                () -> productoFrm.validarActualizar(producto));
        assertEquals("validacion.nombre.requerido", ex.getMessage());
    }

    // ========== Tests para validarAntesDeEliminar ==========

    @Test
    void testValidarAntesDeEliminarExitoso() {
        Producto entidad = new Producto();
        entidad.setNombreProducto("Teclado Logitech");

        Producto original = new Producto();
        original.setNombreProducto("Teclado Logitech");

        assertDoesNotThrow(() -> productoFrm.validarEliminar(entidad, original));
    }

    @Test
    void testValidarAntesDeEliminarNombreCambiado() {
        Producto entidad = new Producto();
        entidad.setNombreProducto("Teclado Logitech Modificado");

        Producto original = new Producto();
        original.setNombreProducto("Teclado Logitech");

        Exception ex = assertThrows(Exception.class,
                () -> productoFrm.validarEliminar(entidad, original));
        assertEquals("validacion.nombre.cambiado", ex.getMessage());
    }

    // ========== Tests para instanciarEntidad ==========

    @Test
    void testInstanciarEntidad() {
        Producto producto = productoFrm.crearNuevaInstancia();

        assertNotNull(producto);
        assertTrue(producto.getActivo());
    }

    // ========== Tests para getProductosDeCompra ==========

    @Test
    void testGetProductosDeCompra() {
        Long idCompra = 1L;
        Producto p1 = new Producto();
        p1.setNombreProducto("Producto 1");
        Producto p2 = new Producto();
        p2.setNombreProducto("Producto 2");

        List<Producto> productosEsperados = Arrays.asList(p1, p2);
        when(productoDAO.findByCompra(idCompra)).thenReturn(productosEsperados);

        List<Producto> resultado = productoFrm.getProductosDeCompra(idCompra);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Producto 1", resultado.get(0).getNombreProducto());
        assertEquals("Producto 2", resultado.get(1).getNombreProducto());
        verify(productoDAO).findByCompra(idCompra);
    }

    @Test
    void testGetProductosDeCompraVacio() {
        Long idCompra = 999L;
        when(productoDAO.findByCompra(idCompra)).thenReturn(Collections.emptyList());

        List<Producto> resultado = productoFrm.getProductosDeCompra(idCompra);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(productoDAO).findByCompra(idCompra);
    }

    // ========== Tests para getters y setters ==========

    @Test
    void testGetProductoDAO() {
        assertEquals(productoDAO, productoFrm.getProductoDAO());
    }

    @Test
    void testSetProductoDAO() {
        ProductoDAO nuevoDAO = mock(ProductoDAO.class);
        productoFrm.setProductoDAO(nuevoDAO);
        assertEquals(nuevoDAO, productoFrm.getProductoDAO());
    }
}
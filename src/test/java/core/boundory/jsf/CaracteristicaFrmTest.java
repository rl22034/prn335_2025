package core.boundory.jsf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.CaracteristicaFrm;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.CaracteristicaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.control.TipoUnidadMedidaDAO;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.Caracteristica;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.entity.TipoUnidadMedida;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaracteristicaFrmTest {

    @Mock
    private CaracteristicaDAO caracteristicaDAO;

    @Mock
    private TipoUnidadMedidaDAO tipoUnidadMedidaDAO;

    private CaracteristicaFrmTesteable caracteristicaFrm;

    private Caracteristica caracteristica;
    private TipoUnidadMedida tipoUnidadMedida;

    // Clase interna para acceder a métodos protected
    private static class CaracteristicaFrmTesteable extends CaracteristicaFrm {
        @Override
        public CaracteristicaDAO obtenerDAO() {
            return super.obtenerDAO();
        }

        @Override
        public void validarAntesDeCrear(Caracteristica entidad) throws Exception {
            super.validarAntesDeCrear(entidad);
        }

        @Override
        public void validarAntesDeActualizar(Caracteristica entidad) throws Exception {
            super.validarAntesDeActualizar(entidad);
        }

        @Override
        public void validarAntesDeEliminar(Caracteristica entidad, Caracteristica original) throws Exception {
            super.validarAntesDeEliminar(entidad, original);
        }

        @Override
        public Caracteristica instanciarEntidad() {
            return super.instanciarEntidad();
        }
    }

    @BeforeEach
    void setUp() {
        caracteristicaFrm = new CaracteristicaFrmTesteable();
        caracteristicaFrm.setCaracteristicaDAO(caracteristicaDAO);
        caracteristicaFrm.setTipoUnidadMedidaDAO(tipoUnidadMedidaDAO);

        tipoUnidadMedida = new TipoUnidadMedida();
        tipoUnidadMedida.setNombre("Kilogramos");

        caracteristica = new Caracteristica();
        caracteristica.setNombre("Peso");
        caracteristica.setIdTipoUnidadMedida(tipoUnidadMedida);
        caracteristica.setActivo(true);
    }

    @Test
    void testObtenerDAO() {
        // When
        CaracteristicaDAO resultado = caracteristicaFrm.obtenerDAO();

        // Then
        assertNotNull(resultado);
        assertEquals(caracteristicaDAO, resultado);
    }

    @Test
    void testValidarAntesDeCrear_ConDatosValidos() {
        // Given
        caracteristica.setNombre("Peso");
        caracteristica.setIdTipoUnidadMedida(tipoUnidadMedida);

        // When & Then
        assertDoesNotThrow(() -> caracteristicaFrm.validarAntesDeCrear(caracteristica));
    }

    @Test
    void testValidarAntesDeCrear_ConTipoUnidadMedidaNulo() {
        // Given
        caracteristica.setNombre("Peso");
        caracteristica.setIdTipoUnidadMedida(null);

        // When & Then
        Exception exception = assertThrows(Exception.class,
                () -> caracteristicaFrm.validarAntesDeCrear(caracteristica));
        assertEquals("validacion.tipounidadmedida.requerido", exception.getMessage());
    }

    @Test
    void testValidarAntesDeCrear_ConNombreNulo() {
        // Given
        caracteristica.setNombre(null);
        caracteristica.setIdTipoUnidadMedida(tipoUnidadMedida);

        // When & Then
        Exception exception = assertThrows(Exception.class,
                () -> caracteristicaFrm.validarAntesDeCrear(caracteristica));
        assertEquals("validacion.nombre.requerido", exception.getMessage());
    }

    @Test
    void testValidarAntesDeCrear_ConNombreVacio() {
        // Given
        caracteristica.setNombre("");
        caracteristica.setIdTipoUnidadMedida(tipoUnidadMedida);

        // When & Then
        Exception exception = assertThrows(Exception.class,
                () -> caracteristicaFrm.validarAntesDeCrear(caracteristica));
        assertEquals("validacion.nombre.requerido", exception.getMessage());
    }

    @Test
    void testValidarAntesDeCrear_ConNombreSoloEspacios() {
        // Given
        caracteristica.setNombre("   ");
        caracteristica.setIdTipoUnidadMedida(tipoUnidadMedida);

        // When & Then
        Exception exception = assertThrows(Exception.class,
                () -> caracteristicaFrm.validarAntesDeCrear(caracteristica));
        assertEquals("validacion.nombre.requerido", exception.getMessage());
    }

    @Test
    void testValidarAntesDeActualizar_ConDatosValidos() {
        // Given
        caracteristica.setNombre("Peso Actualizado");
        caracteristica.setIdTipoUnidadMedida(tipoUnidadMedida);

        // When & Then
        assertDoesNotThrow(() -> caracteristicaFrm.validarAntesDeActualizar(caracteristica));
    }

    @Test
    void testValidarAntesDeActualizar_ConTipoUnidadMedidaNulo() {
        // Given
        caracteristica.setNombre("Peso");
        caracteristica.setIdTipoUnidadMedida(null);

        // When & Then
        Exception exception = assertThrows(Exception.class,
                () -> caracteristicaFrm.validarAntesDeActualizar(caracteristica));
        assertEquals("validacion.tipounidadmedida.requerido", exception.getMessage());
    }

    @Test
    void testValidarAntesDeActualizar_ConNombreNulo() {
        // Given
        caracteristica.setNombre(null);
        caracteristica.setIdTipoUnidadMedida(tipoUnidadMedida);

        // When & Then
        Exception exception = assertThrows(Exception.class,
                () -> caracteristicaFrm.validarAntesDeActualizar(caracteristica));
        assertEquals("validacion.nombre.requerido", exception.getMessage());
    }

    @Test
    void testValidarAntesDeActualizar_ConNombreVacio() {
        // Given
        caracteristica.setNombre("");
        caracteristica.setIdTipoUnidadMedida(tipoUnidadMedida);

        // When & Then
        Exception exception = assertThrows(Exception.class,
                () -> caracteristicaFrm.validarAntesDeActualizar(caracteristica));
        assertEquals("validacion.nombre.requerido", exception.getMessage());
    }

    @Test
    void testValidarAntesDeEliminar_ConNombreIgual() {
        // Given
        Caracteristica original = new Caracteristica();
        original.setNombre("Peso");
        caracteristica.setNombre("Peso");

        // When & Then
        assertDoesNotThrow(() -> caracteristicaFrm.validarAntesDeEliminar(caracteristica, original));
    }

    @Test
    void testValidarAntesDeEliminar_ConNombreDiferente() {
        // Given
        Caracteristica original = new Caracteristica();
        original.setNombre("Peso Original");
        caracteristica.setNombre("Peso Modificado");

        // When & Then
        Exception exception = assertThrows(Exception.class,
                () -> caracteristicaFrm.validarAntesDeEliminar(caracteristica, original));
        assertEquals("validacion.nombre.cambiado", exception.getMessage());
    }

    @Test
    void testInstanciarEntidad() {
        // When
        Caracteristica nuevo = caracteristicaFrm.instanciarEntidad();

        // Then
        assertNotNull(nuevo);
        assertTrue(nuevo.getActivo());
        assertNull(nuevo.getNombre());
        assertNull(nuevo.getIdTipoUnidadMedida());
    }

    @Test
    void testGetTipoUnidadMedidaList_PrimeraVez() throws Exception {
        // Given
        List<TipoUnidadMedida> listaMock = new ArrayList<>();
        listaMock.add(tipoUnidadMedida);
        when(tipoUnidadMedidaDAO.findRange(0, 1000)).thenReturn(listaMock);

        // When
        List<TipoUnidadMedida> resultado = caracteristicaFrm.getTipoUnidadMedidaList();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(tipoUnidadMedida, resultado.get(0));
        verify(tipoUnidadMedidaDAO, times(1)).findRange(0, 1000);
    }

    @Test
    void testGetTipoUnidadMedidaList_SegundaVez() throws Exception {
        // Given
        List<TipoUnidadMedida> listaMock = new ArrayList<>();
        listaMock.add(tipoUnidadMedida);
        when(tipoUnidadMedidaDAO.findRange(0, 1000)).thenReturn(listaMock);

        // When - Primera llamada
        caracteristicaFrm.getTipoUnidadMedidaList();
        // Segunda llamada
        List<TipoUnidadMedida> resultado = caracteristicaFrm.getTipoUnidadMedidaList();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        // Debe llamarse solo una vez porque se cachea
        verify(tipoUnidadMedidaDAO, times(1)).findRange(0, 1000);
    }

    @Test
    void testGetTipoUnidadMedidaList_ConExcepcion() throws Exception {
        // Given
        when(tipoUnidadMedidaDAO.findRange(0, 1000))
                .thenThrow(new RuntimeException("Error de base de datos"));

        // When
        List<TipoUnidadMedida> resultado = caracteristicaFrm.getTipoUnidadMedidaList();

        // Then
        assertNull(resultado);
        verify(tipoUnidadMedidaDAO, times(1)).findRange(0, 1000);
    }

    @Test
    void testSetTipoUnidadMedidaList() {
        // Given
        List<TipoUnidadMedida> listaNueva = new ArrayList<>();
        listaNueva.add(tipoUnidadMedida);

        // When
        caracteristicaFrm.setTipoUnidadMedidaList(listaNueva);

        // Then
        assertEquals(listaNueva, caracteristicaFrm.getTipoUnidadMedidaList());
        // No debe llamar al DAO porque ya está seteada la lista
        verify(tipoUnidadMedidaDAO, never()).findRange(anyInt(), anyInt());
    }

    @Test
    void testGetCaracteristicaDAO() {
        // When
        CaracteristicaDAO resultado = caracteristicaFrm.getCaracteristicaDAO();

        // Then
        assertNotNull(resultado);
        assertEquals(caracteristicaDAO, resultado);
    }

    @Test
    void testSetCaracteristicaDAO() {
        // Given
        CaracteristicaDAO nuevoDAO = mock(CaracteristicaDAO.class);

        // When
        caracteristicaFrm.setCaracteristicaDAO(nuevoDAO);

        // Then
        assertEquals(nuevoDAO, caracteristicaFrm.getCaracteristicaDAO());
    }

    @Test
    void testGetTipoUnidadMedidaDAO() {
        // When
        TipoUnidadMedidaDAO resultado = caracteristicaFrm.getTipoUnidadMedidaDAO();

        // Then
        assertNotNull(resultado);
        assertEquals(tipoUnidadMedidaDAO, resultado);
    }

    @Test
    void testSetTipoUnidadMedidaDAO() {
        // Given
        TipoUnidadMedidaDAO nuevoDAO = mock(TipoUnidadMedidaDAO.class);

        // When
        caracteristicaFrm.setTipoUnidadMedidaDAO(nuevoDAO);

        // Then
        assertEquals(nuevoDAO, caracteristicaFrm.getTipoUnidadMedidaDAO());
    }
}
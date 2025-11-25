package core.boundory.jsf;

import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.EstadoVenta;

import static org.junit.jupiter.api.Assertions.*;

class EstadoVentaTest {

    @Test
    void testEnumValues() {
        EstadoVenta[] valores = EstadoVenta.values();
        assertEquals(3, valores.length);
        assertEquals(EstadoVenta.PENDIENTE, valores[0]);
        assertEquals(EstadoVenta.DESPACHADA, valores[1]);
        assertEquals(EstadoVenta.CANCELADA, valores[2]);
    }

    @Test
    void testValueOf() {
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.valueOf("PENDIENTE"));
        assertEquals(EstadoVenta.DESPACHADA, EstadoVenta.valueOf("DESPACHADA"));
        assertEquals(EstadoVenta.CANCELADA, EstadoVenta.valueOf("CANCELADA"));
    }

    @Test
    void testGetDescripcionPendiente() {
        assertEquals("Pendiente", EstadoVenta.PENDIENTE.getDescripcion());
    }

    @Test
    void testGetDescripcionDespachada() {
        assertEquals("Despachada", EstadoVenta.DESPACHADA.getDescripcion());
    }

    @Test
    void testGetDescripcionCancelada() {
        assertEquals("Cancelada", EstadoVenta.CANCELADA.getDescripcion());
    }

    @Test
    void testGetDescripcionNoEsNull() {
        for (EstadoVenta estado : EstadoVenta.values()) {
            assertNotNull(estado.getDescripcion());
            assertFalse(estado.getDescripcion().isEmpty());
        }
    }

    @Test
    void testFromStringValoresValidosMayusculas() {
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.fromString("PENDIENTE"));
        assertEquals(EstadoVenta.DESPACHADA, EstadoVenta.fromString("DESPACHADA"));
        assertEquals(EstadoVenta.CANCELADA, EstadoVenta.fromString("CANCELADA"));
    }

    @Test
    void testFromStringValoresValidosMinusculas() {
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.fromString("pendiente"));
        assertEquals(EstadoVenta.DESPACHADA, EstadoVenta.fromString("despachada"));
        assertEquals(EstadoVenta.CANCELADA, EstadoVenta.fromString("cancelada"));
    }

    @Test
    void testFromStringValoresValidosMixedCase() {
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.fromString("Pendiente"));
    }

    @Test
    void testFromStringNull() {
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.fromString(null));
    }

    @Test
    void testFromStringVacio() {
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.fromString(""));
    }

    @Test
    void testFromStringEspaciosEnBlanco() {
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.fromString("   "));
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.fromString("\t"));
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.fromString("\n"));
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.fromString("  \t  "));
    }

    @Test
    void testFromStringValorInvalido() {
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.fromString("INVALIDO"));
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.fromString("NO_EXISTE"));
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.fromString("OTRO_ESTADO"));
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.fromString("xyz"));
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.fromString("123"));
    }

    @Test
    void testFromStringCapturaIllegalArgumentException() {
        EstadoVenta resultado = EstadoVenta.fromString("ESTADO_INEXISTENTE");
        assertEquals(EstadoVenta.PENDIENTE, resultado);
    }
}

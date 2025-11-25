package core.boundory.jsf;

import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.EstadoVentaDetalle;

import static org.junit.jupiter.api.Assertions.*;

class EstadoVentaDetalleTest {

    @Test
    void testEnumValues() {
        EstadoVentaDetalle[] valores = EstadoVentaDetalle.values();
        assertEquals(3, valores.length);
        assertEquals(EstadoVentaDetalle.PENDIENTE, valores[0]);
        assertEquals(EstadoVentaDetalle.DESPACHADO, valores[1]);
        assertEquals(EstadoVentaDetalle.CANCELADO, valores[2]);
    }

    @Test
    void testValueOf() {
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.valueOf("PENDIENTE"));
        assertEquals(EstadoVentaDetalle.DESPACHADO, EstadoVentaDetalle.valueOf("DESPACHADO"));
        assertEquals(EstadoVentaDetalle.CANCELADO, EstadoVentaDetalle.valueOf("CANCELADO"));
    }

    @Test
    void testGetDescripcionPendiente() {
        assertEquals("Pendiente", EstadoVentaDetalle.PENDIENTE.getDescripcion());
    }

    @Test
    void testGetDescripcionDespachado() {
        assertEquals("Despachado", EstadoVentaDetalle.DESPACHADO.getDescripcion());
    }

    @Test
    void testGetDescripcionCancelado() {
        assertEquals("Cancelado", EstadoVentaDetalle.CANCELADO.getDescripcion());
    }

    @Test
    void testGetDescripcionNoEsNull() {
        for (EstadoVentaDetalle estado : EstadoVentaDetalle.values()) {
            assertNotNull(estado.getDescripcion());
            assertFalse(estado.getDescripcion().isEmpty());
        }
    }

    @Test
    void testFromStringValoresValidosMayusculas() {
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.fromString("PENDIENTE"));
        assertEquals(EstadoVentaDetalle.DESPACHADO, EstadoVentaDetalle.fromString("DESPACHADO"));
        assertEquals(EstadoVentaDetalle.CANCELADO, EstadoVentaDetalle.fromString("CANCELADO"));
    }

    @Test
    void testFromStringValoresValidosMinusculas() {
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.fromString("pendiente"));
        assertEquals(EstadoVentaDetalle.DESPACHADO, EstadoVentaDetalle.fromString("despachado"));
        assertEquals(EstadoVentaDetalle.CANCELADO, EstadoVentaDetalle.fromString("cancelado"));
    }

    @Test
    void testFromStringValoresValidosMixedCase() {
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.fromString("Pendiente"));
        assertEquals(EstadoVentaDetalle.DESPACHADO, EstadoVentaDetalle.fromString("DeSpAcHaDo"));
    }

    @Test
    void testFromStringNull() {
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.fromString(null));
    }

    @Test
    void testFromStringVacio() {
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.fromString(""));
    }

    @Test
    void testFromStringEspaciosEnBlanco() {
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.fromString("   "));
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.fromString("\t"));
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.fromString("\n"));
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.fromString("  \t  "));
    }

    @Test
    void testFromStringValorInvalido() {
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.fromString("INVALIDO"));
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.fromString("NO_EXISTE"));
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.fromString("OTRO_ESTADO"));
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.fromString("xyz"));
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.fromString("123"));
    }

    @Test
    void testFromStringCapturaIllegalArgumentException() {
        EstadoVentaDetalle resultado = EstadoVentaDetalle.fromString("ESTADO_INEXISTENTE");
        assertEquals(EstadoVentaDetalle.PENDIENTE, resultado);
    }
}

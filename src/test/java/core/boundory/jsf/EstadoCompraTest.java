package core.boundory.jsf;

import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.EstadoCompra;

import static org.junit.jupiter.api.Assertions.*;

class EstadoCompraTest {

    @Test
    void testEnumValues() {
        EstadoCompra[] valores = EstadoCompra.values();
        assertEquals(5, valores.length);
        assertEquals(EstadoCompra.PENDIENTE, valores[0]);
        assertEquals(EstadoCompra.EN_PROCESO, valores[1]);
        assertEquals(EstadoCompra.COMPLETADA, valores[2]);
        assertEquals(EstadoCompra.PAGADA, valores[3]);
        assertEquals(EstadoCompra.CANCELADA, valores[4]);
    }

    @Test
    void testValueOf() {
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.valueOf("PENDIENTE"));
        assertEquals(EstadoCompra.EN_PROCESO, EstadoCompra.valueOf("EN_PROCESO"));
        assertEquals(EstadoCompra.COMPLETADA, EstadoCompra.valueOf("COMPLETADA"));
        assertEquals(EstadoCompra.PAGADA, EstadoCompra.valueOf("PAGADA"));
        assertEquals(EstadoCompra.CANCELADA, EstadoCompra.valueOf("CANCELADA"));
    }

    @Test
    void testGetDescripcionPendiente() {
        assertEquals("Pendiente", EstadoCompra.PENDIENTE.getDescripcion());
    }

    @Test
    void testGetDescripcionEnProceso() {
        assertEquals("En Proceso", EstadoCompra.EN_PROCESO.getDescripcion());
    }

    @Test
    void testGetDescripcionCompletada() {
        assertEquals("Completada", EstadoCompra.COMPLETADA.getDescripcion());
    }

    @Test
    void testGetDescripcionPagada() {
        assertEquals("Pagada", EstadoCompra.PAGADA.getDescripcion());
    }

    @Test
    void testGetDescripcionCancelada() {
        assertEquals("Cancelada", EstadoCompra.CANCELADA.getDescripcion());
    }

    @Test
    void testGetDescripcionNoEsNull() {
        for (EstadoCompra estado : EstadoCompra.values()) {
            assertNotNull(estado.getDescripcion());
            assertFalse(estado.getDescripcion().isEmpty());
        }
    }

    @Test
    void testFromStringValoresValidosMayusculas() {
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString("PENDIENTE"));
        assertEquals(EstadoCompra.EN_PROCESO, EstadoCompra.fromString("EN_PROCESO"));
        assertEquals(EstadoCompra.COMPLETADA, EstadoCompra.fromString("COMPLETADA"));
        assertEquals(EstadoCompra.PAGADA, EstadoCompra.fromString("PAGADA"));
        assertEquals(EstadoCompra.CANCELADA, EstadoCompra.fromString("CANCELADA"));
    }

    @Test
    void testFromStringValoresValidosMinusculas() {
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString("pendiente"));
        assertEquals(EstadoCompra.EN_PROCESO, EstadoCompra.fromString("en_proceso"));
        assertEquals(EstadoCompra.COMPLETADA, EstadoCompra.fromString("completada"));
        assertEquals(EstadoCompra.PAGADA, EstadoCompra.fromString("pagada"));
        assertEquals(EstadoCompra.CANCELADA, EstadoCompra.fromString("cancelada"));
    }

    @Test
    void testFromStringValoresValidosMixedCase() {
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString("Pendiente"));
        assertEquals(EstadoCompra.EN_PROCESO, EstadoCompra.fromString("En_Proceso"));
        assertEquals(EstadoCompra.COMPLETADA, EstadoCompra.fromString("ComPleTaDa"));
    }

    @Test
    void testFromStringNull() {
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString(null));
    }

    @Test
    void testFromStringVacio() {
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString(""));
    }

    @Test
    void testFromStringEspaciosEnBlanco() {
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString("   "));
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString("\t"));
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString("\n"));
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString("  \t  "));
    }

    @Test
    void testFromStringValorInvalido() {
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString("INVALIDO"));
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString("NO_EXISTE"));
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString("OTRO_ESTADO"));
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString("xyz"));
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString("123"));
    }

    @Test
    void testFromStringCapturaIllegalArgumentException() {
        EstadoCompra resultado = EstadoCompra.fromString("ESTADO_INEXISTENTE");
        assertEquals(EstadoCompra.PENDIENTE, resultado);
    }
}

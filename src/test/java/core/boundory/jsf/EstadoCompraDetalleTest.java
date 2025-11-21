package core.boundory.jsf;

import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.EstadoCompraDetalle;

import static org.junit.jupiter.api.Assertions.*;

class EstadoCompraDetalleTest {

    @Test
    void testEnumValues() {
        EstadoCompraDetalle[] valores = EstadoCompraDetalle.values();
        assertEquals(4, valores.length);
        assertEquals(EstadoCompraDetalle.PENDIENTE, valores[0]);
        assertEquals(EstadoCompraDetalle.RECIBIDO, valores[1]);
        assertEquals(EstadoCompraDetalle.PARCIAL, valores[2]);
        assertEquals(EstadoCompraDetalle.CANCELADO, valores[3]);
    }

    @Test
    void testValueOf() {
        assertEquals(EstadoCompraDetalle.PENDIENTE, EstadoCompraDetalle.valueOf("PENDIENTE"));
        assertEquals(EstadoCompraDetalle.RECIBIDO, EstadoCompraDetalle.valueOf("RECIBIDO"));
        assertEquals(EstadoCompraDetalle.PARCIAL, EstadoCompraDetalle.valueOf("PARCIAL"));
        assertEquals(EstadoCompraDetalle.CANCELADO, EstadoCompraDetalle.valueOf("CANCELADO"));
    }

    @Test
    void testGetDescripcionPendiente() {
        assertEquals("Pendiente", EstadoCompraDetalle.PENDIENTE.getDescripcion());
    }

    @Test
    void testGetDescripcionRecibido() {
        assertEquals("Recibido", EstadoCompraDetalle.RECIBIDO.getDescripcion());
    }

    @Test
    void testGetDescripcionParcial() {
        assertEquals("Parcial", EstadoCompraDetalle.PARCIAL.getDescripcion());
    }

    @Test
    void testGetDescripcionCancelado() {
        assertEquals("Cancelado", EstadoCompraDetalle.CANCELADO.getDescripcion());
    }

    @Test
    void testGetDescripcionNoEsNull() {
        for (EstadoCompraDetalle estado : EstadoCompraDetalle.values()) {
            assertNotNull(estado.getDescripcion());
            assertFalse(estado.getDescripcion().isEmpty());
        }
    }
}
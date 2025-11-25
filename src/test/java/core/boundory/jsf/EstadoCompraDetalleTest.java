package core.boundory.jsf;

import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.EstadoCompraDetalle;

import static org.junit.jupiter.api.Assertions.*;

class EstadoCompraDetalleTest {

    @Test
    void testEnumValues() {
        EstadoCompraDetalle[] valores = EstadoCompraDetalle.values();
        // 🚨 CAMBIO 1: El Enum solo tiene 3 valores, no 4.
        assertEquals(3, valores.length);

        // 🚨 CAMBIO 2: Se ajusta la posición de los valores.
        assertEquals(EstadoCompraDetalle.PENDIENTE, valores[0]);
        assertEquals(EstadoCompraDetalle.RECIBIDO, valores[1]);
        assertEquals(EstadoCompraDetalle.CANCELADO, valores[2]); // CANCELADO ahora es el índice 2
    }

    @Test
    void testValueOf() {
        assertEquals(EstadoCompraDetalle.PENDIENTE, EstadoCompraDetalle.valueOf("PENDIENTE"));
        assertEquals(EstadoCompraDetalle.RECIBIDO, EstadoCompraDetalle.valueOf("RECIBIDO"));
        // 🚨 CAMBIO 3: Se elimina la prueba para PARCIAL, ya que no existe.
        // assertEquals(EstadoCompraDetalle.PARCIAL, EstadoCompraDetalle.valueOf("PARCIAL"));
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

    // 🚨 CAMBIO 4: Se elimina la prueba para getDescripcionParcial.
    /*
    @Test
    void testGetDescripcionParcial() {
        assertEquals("Parcial", EstadoCompraDetalle.PARCIAL.getDescripcion());
    }
    */

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
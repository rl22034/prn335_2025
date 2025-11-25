package core.boundory.jsf;

import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.EstadoVentaDetalle;

import static org.junit.jupiter.api.Assertions.*;

class EstadoVentaDetalleTest {

    // ---------------------------------------------
    // 1. Pruebas de Métodos Estándar del Enum
    // ---------------------------------------------

    @Test
    void testEnumValuesAndOrder() {
        EstadoVentaDetalle[] valores = EstadoVentaDetalle.values();

        // Verifica que el número total de constantes sea 3
        assertEquals(3, valores.length, "Debe haber 3 constantes en el Enum.");

        // Verifica el orden de las constantes
        assertEquals(EstadoVentaDetalle.PENDIENTE, valores[0], "La primera constante debe ser PENDIENTE.");
        assertEquals(EstadoVentaDetalle.DESPACHADO, valores[1], "La segunda constante debe ser DESPACHADO.");
        assertEquals(EstadoVentaDetalle.CANCELADO, valores[2], "La tercera constante debe ser CANCELADO.");
    }

    @Test
    void testValueOf() {
        // Prueba todas las constantes existentes por su nombre exacto
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.valueOf("PENDIENTE"));
        assertEquals(EstadoVentaDetalle.DESPACHADO, EstadoVentaDetalle.valueOf("DESPACHADO"));
        assertEquals(EstadoVentaDetalle.CANCELADO, EstadoVentaDetalle.valueOf("CANCELADO"));

        // Cobertura de excepción (IllegalArgumentException) para nombre no válido
        assertThrows(IllegalArgumentException.class, () -> {
            EstadoVentaDetalle.valueOf("NO_ES_UN_ESTADO");
        }, "Debe lanzar IllegalArgumentException para un nombre no válido.");
    }

    // ---------------------------------------------
    // 2. Pruebas del Método de Instancia (getDescripcion)
    // ---------------------------------------------

    @Test
    void testGetDescripcion() {
        // Comprueba la descripción de cada constante (cubre el constructor y el método)
        assertEquals("Pendiente", EstadoVentaDetalle.PENDIENTE.getDescripcion());
        assertEquals("Despachado", EstadoVentaDetalle.DESPACHADO.getDescripcion());
        assertEquals("Cancelado", EstadoVentaDetalle.CANCELADO.getDescripcion());

        // Asegura que ninguna descripción sea nula o vacía
        for (EstadoVentaDetalle estado : EstadoVentaDetalle.values()) {
            assertNotNull(estado.getDescripcion(), "La descripción no debe ser null.");
            assertFalse(estado.getDescripcion().isEmpty(), "La descripción no debe ser vacía.");
        }
    }

    // ---------------------------------------------
    // 3. Pruebas del Método Personalizado (fromString)
    // ---------------------------------------------

    @Test
    void testFromString_Success_ValidCases() {
        // Prueba camino exitoso con mayúsculas
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.fromString("PENDIENTE"));
        // Prueba éxito con minúsculas (asegura la llamada a toUpperCase)
        assertEquals(EstadoVentaDetalle.DESPACHADO, EstadoVentaDetalle.fromString("despachado"));
        assertEquals(EstadoVentaDetalle.CANCELADO, EstadoVentaDetalle.fromString("cancelado"));
    }

    @Test
    void testFromString_ExceptionCase_ReturnsPendiente() {
        // Prueba el caso que lanza IllegalArgumentException (cubre el bloque catch)
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.fromString("ESTADO_INEXISTENTE"),
                "Un valor inválido debe retornar PENDIENTE.");
    }

    @Test
    void testFromString_NullCase_ReturnsPendiente() {
        // Prueba la condición 'if (valor == null)'
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.fromString(null),
                "Un valor null debe retornar PENDIENTE.");
    }

    @Test
    void testFromString_EmptyCase_ReturnsPendiente() {
        // Prueba la condición '|| valor.trim().isEmpty()' con cadena vacía
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.fromString(""),
                "Una cadena vacía debe retornar PENDIENTE.");
    }

    @Test
    void testFromString_WhitespaceCase_ReturnsPendiente() {
        // Prueba la condición '|| valor.trim().isEmpty()' con solo espacios en blanco
        assertEquals(EstadoVentaDetalle.PENDIENTE, EstadoVentaDetalle.fromString("  \t  "),
                "Una cadena con solo espacios en blanco debe retornar PENDIENTE.");
    }
}

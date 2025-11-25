package core.boundory.jsf;

import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.EstadoVenta;

import static org.junit.jupiter.api.Assertions.*;

class EstadoVentaTest {

    // ---------------------------------------------
    // 1. Pruebas de Métodos Estándar del Enum
    // ---------------------------------------------

    @Test
    void testEnumValuesAndOrder() {
        EstadoVenta[] valores = EstadoVenta.values();

        // Verifica que el número total de constantes sea 3
        assertEquals(3, valores.length, "Debe haber 3 constantes en el Enum.");

        // Verifica el orden de las constantes
        assertEquals(EstadoVenta.PENDIENTE, valores[0], "La primera constante debe ser PENDIENTE.");
        assertEquals(EstadoVenta.DESPACHADA, valores[1], "La segunda constante debe ser DESPACHADA.");
        assertEquals(EstadoVenta.CANCELADA, valores[2], "La tercera constante debe ser CANCELADA.");
    }

    @Test
    void testValueOf() {
        // Prueba todas las constantes existentes por su nombre exacto
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.valueOf("PENDIENTE"));
        assertEquals(EstadoVenta.DESPACHADA, EstadoVenta.valueOf("DESPACHADA"));
        assertEquals(EstadoVenta.CANCELADA, EstadoVenta.valueOf("CANCELADA"));

        // Cobertura de excepción (IllegalArgumentException) para nombre no válido
        assertThrows(IllegalArgumentException.class, () -> {
            EstadoVenta.valueOf("ESTADO_INEXISTENTE");
        }, "Debe lanzar IllegalArgumentException para un nombre no válido.");
    }

    // ---------------------------------------------
    // 2. Pruebas del Método de Instancia (getDescripcion)
    // ---------------------------------------------

    @Test
    void testGetDescripcion() {
        // Comprueba la descripción de cada constante (cubre el constructor y el método)
        assertEquals("Pendiente", EstadoVenta.PENDIENTE.getDescripcion());
        assertEquals("Despachada", EstadoVenta.DESPACHADA.getDescripcion());
        assertEquals("Cancelada", EstadoVenta.CANCELADA.getDescripcion());

        // Asegura que ninguna descripción sea nula o vacía
        for (EstadoVenta estado : EstadoVenta.values()) {
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
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.fromString("PENDIENTE"));
        // Prueba éxito con minúsculas (asegura la llamada a toUpperCase)
        assertEquals(EstadoVenta.DESPACHADA, EstadoVenta.fromString("despachada"));
        // Prueba éxito con caso mixto
        assertEquals(EstadoVenta.CANCELADA, EstadoVenta.fromString("CAncElAdA"));
    }

    @Test
    void testFromString_ExceptionCase_ReturnsPendiente() {
        // Prueba el caso que lanza IllegalArgumentException (cubre el bloque catch)
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.fromString("OTRO_ESTADO"),
                "Un valor inválido debe retornar PENDIENTE.");
    }

    @Test
    void testFromString_NullCase_ReturnsPendiente() {
        // Prueba la condición 'if (valor == null)'
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.fromString(null),
                "Un valor null debe retornar PENDIENTE.");
    }

    @Test
    void testFromString_EmptyCase_ReturnsPendiente() {
        // Prueba la condición '|| valor.trim().isEmpty()' con cadena vacía
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.fromString(""),
                "Una cadena vacía debe retornar PENDIENTE.");
    }

    @Test
    void testFromString_WhitespaceCase_ReturnsPendiente() {
        // Prueba la condición '|| valor.trim().isEmpty()' con solo espacios en blanco
        assertEquals(EstadoVenta.PENDIENTE, EstadoVenta.fromString("  \t  "),
                "Una cadena con solo espacios en blanco debe retornar PENDIENTE.");
    }
}
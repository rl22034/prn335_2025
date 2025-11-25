package core.boundory.jsf;

import org.junit.jupiter.api.Test;
import sv.edu.ues.occ.ingenieria.prn335.web.inventariowebappprn335_2025.core.boundory.jsf.EstadoCompra;

import static org.junit.jupiter.api.Assertions.*;

class EstadoCompraTest {

    // ---------------------------------------------
    // 1. Pruebas de Métodos Estándar del Enum
    // ---------------------------------------------

    @Test
    void testEnumValuesAndOrder() {
        EstadoCompra[] valores = EstadoCompra.values();

        // Verifica que el número total de constantes sea 3
        assertEquals(3, valores.length, "Debe haber 3 constantes en el Enum.");

        // Verifica el orden de las constantes
        assertEquals(EstadoCompra.PENDIENTE, valores[0], "La primera constante debe ser PENDIENTE.");
        assertEquals(EstadoCompra.PAGADA, valores[1], "La segunda constante debe ser PAGADA.");
        assertEquals(EstadoCompra.CANCELADA, valores[2], "La tercera constante debe ser CANCELADA.");
    }

    @Test
    void testValueOf() {
        // Prueba todas las constantes existentes por su nombre exacto
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.valueOf("PENDIENTE"));
        assertEquals(EstadoCompra.PAGADA, EstadoCompra.valueOf("PAGADA"));
        assertEquals(EstadoCompra.CANCELADA, EstadoCompra.valueOf("CANCELADA"));

        // Cobertura de excepción (IllegalArgumentException) para nombre no válido
        assertThrows(IllegalArgumentException.class, () -> {
            EstadoCompra.valueOf("ESTADO_INVALIDO");
        }, "Debe lanzar IllegalArgumentException para un nombre no válido.");
    }

    // ---------------------------------------------
    // 2. Pruebas del Método de Instancia (getDescripcion)
    // ---------------------------------------------

    @Test
    void testGetDescripcion() {
        // Comprueba la descripción de cada constante (cubre el constructor y el método)
        assertEquals("Pendiente", EstadoCompra.PENDIENTE.getDescripcion());
        assertEquals("Pagada", EstadoCompra.PAGADA.getDescripcion());
        assertEquals("Cancelada", EstadoCompra.CANCELADA.getDescripcion());

        // Asegura que ninguna descripción sea nula o vacía
        for (EstadoCompra estado : EstadoCompra.values()) {
            assertNotNull(estado.getDescripcion(), "La descripción no debe ser null.");
            assertFalse(estado.getDescripcion().isEmpty(), "La descripción no debe ser vacía.");
        }
    }

    // ---------------------------------------------
    // 3. Pruebas del Método Personalizado (fromString)
    // ---------------------------------------------

    @Test
    void testFromString_Success_ValidUppercase() {
        // Prueba el camino exitoso con nombres en mayúsculas
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString("PENDIENTE"));
        assertEquals(EstadoCompra.PAGADA, EstadoCompra.fromString("PAGADA"));
        assertEquals(EstadoCompra.CANCELADA, EstadoCompra.fromString("CANCELADA"));
    }

    @Test
    void testFromString_Success_ValidLowercase() {
        // Prueba que maneja correctamente las minúsculas (se convierte a mayúsculas)
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString("pendiente"));
        assertEquals(EstadoCompra.PAGADA, EstadoCompra.fromString("pagada"));
        assertEquals(EstadoCompra.CANCELADA, EstadoCompra.fromString("cancelada"));
    }

    @Test
    void testFromString_ExceptionCase_ReturnsPendiente() {
        // Prueba el caso que lanza IllegalArgumentException (cubre el bloque catch)
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString("NO_EXISTE"),
                "Un valor inválido debe retornar PENDIENTE.");
    }

    @Test
    void testFromString_NullCase_ReturnsPendiente() {
        // Prueba la condición 'if (valor == null)'
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString(null),
                "Un valor null debe retornar PENDIENTE.");
    }

    @Test
    void testFromString_EmptyCase_ReturnsPendiente() {
        // Prueba la condición '|| valor.trim().isEmpty()' con cadena vacía
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString(""),
                "Una cadena vacía debe retornar PENDIENTE.");
    }

    @Test
    void testFromString_WhitespaceCase_ReturnsPendiente() {
        // Prueba la condición '|| valor.trim().isEmpty()' con solo espacios en blanco
        assertEquals(EstadoCompra.PENDIENTE, EstadoCompra.fromString("   "),
                "Una cadena con solo espacios en blanco debe retornar PENDIENTE.");
    }
}
